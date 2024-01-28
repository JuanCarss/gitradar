package es.ulpgc.aws;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

public class AWSDynamoDBClientBuilder {
    private String endpoint;
    private String region;

    public AWSDynamoDBClientBuilder withEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }
    public AWSDynamoDBClientBuilder withRegion(String region) {
        this.region = region;
        return this;
    }

    public AmazonDynamoDB build() {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                endpoint,
                                region))
                .build();
    }
}
