package es.ulpgc.aws;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

import java.net.URI;

public class AWSClientBuilder {

    private final String endpoint;
    private final String region;

    public AWSClientBuilder(String endpoint, String region) {
        this.endpoint = endpoint;
        this.region = region;
    }

    public EventBridgeClient buildEventBridgeClient() {
        return EventBridgeClient.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .build();
    }

    public AmazonDynamoDB buildDynamoDBClient() {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                endpoint,
                                region))
                .build();
    }
}
