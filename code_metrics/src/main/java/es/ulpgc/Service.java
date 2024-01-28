package es.ulpgc;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import es.ulpgc.aws.AWSDynamoDBClientBuilder;
import es.ulpgc.aws.AWSEventClientBuilder;
import es.ulpgc.aws.DynamoDB;
import es.ulpgc.deserializers.ParsedEventDeserializer;
import es.ulpgc.events.MetricsEvent;
import es.ulpgc.events.ParsedEvent;
import es.ulpgc.metrics.Metrics;
import es.ulpgc.readers.DynamoDBTokenReader;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;

public class Service implements RequestHandler<InputStream, String> {
    @Override
    public String handleRequest(InputStream is, Context context) {
        String region = "us-east-1";
        String endpoint = "http://localhost:4566";
        String tableName = "SemanticTokens";
        AmazonDynamoDB dynamoDBClient = new AWSDynamoDBClientBuilder().withRegion(region).withEndpoint(endpoint).build();
        EventBridgeClient eventBridgeClient = new AWSEventClientBuilder().withEndpoint(endpoint).build();

        ParsedEvent parsedEvent = new ParsedEventDeserializer().deserialize(read(is));
        List<Token> tokens = new DynamoDBTokenReader().read(
                new DynamoDB().getItem(dynamoDBClient, tableName, "filename", parsedEvent.filename()));
        Metrics metrics = new MetricsGenerator().generate(tokens);
        MetricsEvent metricsEvent = new MetricsEvent.MetricsEventBuilder()
                .withTs(Instant.now())
                .withSource("lambda.code_metrics")
                .withFilename(parsedEvent.filename())
                .build();
        new EventBridge().publishEvent(metricsEvent, eventBridgeClient);
        return null;
    }

    private static String read(InputStream is) {
        try (BufferedInputStream bis = new BufferedInputStream(is)) {
            return new String(bis.readAllBytes());
        } catch (IOException e) {
            return "{}";
        }
    }
}
