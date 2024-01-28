package es.ulpgc;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import es.ulpgc.aws.dynamodb.DynamoDB;
import es.ulpgc.aws.dynamodb.DynamoDBClientBuilder;
import es.ulpgc.aws.eventbridge.EventBridge;
import es.ulpgc.aws.eventbridge.EventBridgeClientBuilder;
import es.ulpgc.aws.s3.S3;
import es.ulpgc.aws.s3.S3ClientBuilder;
import es.ulpgc.deserializers.ParsedEventDeserializer;
import es.ulpgc.events.MetricsEvent;
import es.ulpgc.events.ParsedEvent;
import es.ulpgc.readers.DynamoDBTokenReader;
import es.ulpgc.serializers.JsonMetricsSerializer;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;

public class Service implements RequestHandler<InputStream, String> {
    @Override
    public String handleRequest(InputStream is, Context context) {
        ParsedEvent parsedEvent = new ParsedEventDeserializer().deserialize(read(is));
        String metrics = new JsonMetricsSerializer()
                .serialize(new MetricsGenerator().generate(readTokens(buildDynamoDBClient(), parsedEvent.filename())));
        S3.putObject(buildS3Client(), System.getenv("METRICS_BUCKET_ID"), parsedEvent.filename(), metrics);
        EventBridge.putEvent(buildEventBridgeClient(), buildMetricsEvent(parsedEvent));
        return metrics;
    }

    private static String read(InputStream is) {
        try (BufferedInputStream bis = new BufferedInputStream(is)) {
            return new String(bis.readAllBytes());
        } catch (IOException e) {
            return "{}";
        }
    }

    private static List<Token> readTokens(AmazonDynamoDB dynamoDBClient, String filename) {
        return new DynamoDBTokenReader()
                .read(DynamoDB.getItem(dynamoDBClient,
                        System.getenv("DYNAMODB_TABLE_NAME"),
                        "filename", filename));
    }

    private static AmazonDynamoDB buildDynamoDBClient() {
        return new DynamoDBClientBuilder()
                .at(System.getenv("REGION"))
                .withEndpoint(System.getenv("CUSTOM_ENDPOINT_URL"))
                .build();
    }

    private static S3Client buildS3Client() {
        return new S3ClientBuilder()
                .at(System.getenv("REGION"))
                .withEndpoint(System.getenv("CUSTOM_ENDPOINT_URL"))
                .build();
    }

    private static EventBridgeClient buildEventBridgeClient() {
        return new EventBridgeClientBuilder()
                .at(System.getenv("REGION"))
                .withEndpoint(System.getenv("CUSTOM_ENDPOINT_URL"))
                .build();
    }

    private static MetricsEvent buildMetricsEvent(ParsedEvent parsedEvent) {
        return new MetricsEvent.MetricsEventBuilder()
                .withTs(Instant.now())
                .withSource("lambda.code_metrics")
                .withFilename(parsedEvent.filename())
                .build();
    }

}
