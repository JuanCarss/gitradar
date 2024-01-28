package es.ulpgc.aws;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

import java.net.URI;

public class AWSEventClientBuilder {
    private String endpoint;
    private String region;

    public AWSEventClientBuilder withRegion(String region) {
        this.region = region;
        return this;
    }

    public AWSEventClientBuilder withEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public EventBridgeClient build() {
        return EventBridgeClient.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .build();
    }
}
