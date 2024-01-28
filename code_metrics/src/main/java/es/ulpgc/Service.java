package es.ulpgc;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import es.ulpgc.aws.*;
import es.ulpgc.deserializers.ParsedEventDeserializer;
import es.ulpgc.events.MetricsEvent;
import es.ulpgc.events.ParsedEvent;
import es.ulpgc.metrics.Metrics;
import es.ulpgc.readers.DynamoDBTokenReader;
import es.ulpgc.serializers.JsonMetricsSerializer;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

public class Service implements RequestHandler<InputStream, String> {
    @Override
    public String handleRequest(InputStream is, Context context) {
        String region = getEnvironmentVariable("REGION");
        String endpoint = getEnvironmentVariable("CUSTOM_ENDPOINT_URL");
        String tableName = getEnvironmentVariable("DYNAMODB_TABLE_NAME");
        String bucket = getEnvironmentVariable("METRICS_BUCKET_ID");
        AmazonDynamoDB dynamoDBClient = new AWSDynamoDBClientBuilder().withRegion(region).withEndpoint(endpoint).build();
        EventBridgeClient eventBridgeClient = new AWSEventClientBuilder().withRegion(region).withEndpoint(endpoint).build();
        S3Client s3Client = new AWSS3ClientBuilder().withRegion(region).withEndpoint(endpoint).build();
        ParsedEvent parsedEvent = new ParsedEventDeserializer().deserialize(read(is));
        Metrics metrics = new MetricsGenerator().generate(
                new DynamoDBTokenReader().read(
                        DynamoDB.getItem(dynamoDBClient, tableName, "filename", parsedEvent.filename())));
        MetricsEvent metricsEvent = new MetricsEvent.MetricsEventBuilder()
                .withTs(Instant.now())
                .withSource("lambda.code_metrics")
                .withFilename(parsedEvent.filename())
                .build();
        String json = new JsonMetricsSerializer().serialize(metrics);
        S3.putObject(s3Client, bucket, metricsEvent.filename(), json);
        EventBridge.publishEvent(metricsEvent, eventBridgeClient);
        return json;
    }

    private static String read(InputStream is) {
        try (BufferedInputStream bis = new BufferedInputStream(is)) {
            return new String(bis.readAllBytes());
        } catch (IOException e) {
            return "{}";
        }
    }

    private static String getEnvironmentVariable(String variable) {
        return System.getenv(variable);
    }
}
