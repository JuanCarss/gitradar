package es.ulpgc;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import es.ulpgc.analytics.FileAnalytics;
import es.ulpgc.aws.AWSClientBuilder;
import es.ulpgc.deserializers.AWSParsedFileEventDeserializer;
import es.ulpgc.events.FileAnalyticsEvent;
import es.ulpgc.events.ParsedFileEvent;
import es.ulpgc.readers.DynamoDBTokenReader;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.stream.Collectors;

public class Handler implements RequestHandler<InputStream, String> {

    @Override
    public String handleRequest(InputStream event, Context context) {
        AWSClientBuilder awsClientBuilder = new AWSClientBuilder("http://localhost:4566", "us-east-1");
        EventBridgeClient eventBridgeClient = awsClientBuilder.buildEventBridgeClient();
        AmazonDynamoDB amazonDynamoDBClient = awsClientBuilder.buildDynamoDBClient();

        ParsedFileEvent parsedFileEvent = new AWSParsedFileEventDeserializer().deserialize(
                new BufferedReader(new InputStreamReader(event, StandardCharsets.UTF_8)).lines().collect(Collectors.joining(""))
        );

        FileAnalytics analytics = new TokensAnalyzer().analyze(
                new DynamoDBTokenReader(new DynamoDB(amazonDynamoDBClient))
                        .read(parsedFileEvent.filename(), "SemanticTokens"));

        FileAnalyticsEvent fileAnalyticsEvent = new FileAnalyticsEvent.FileAnalyticsEventBuilder()
                .withTs(Instant.now())
                .withSource("lambda.code_metrics")
                .withFilename(parsedFileEvent.filename())
                .build();



        new AWSEventPublisher(eventBridgeClient).publishEvent(fileAnalyticsEvent);
        return null;
    }
}