package es.ulpgc.aws.eventbridge;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

import java.net.URI;

public class EventBridgeClientBuilder {
    private String endpoint;
    private String region;

    public EventBridgeClientBuilder withEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public EventBridgeClientBuilder at(String region) {
        this.region = region;
        return this;
    }

    public EventBridgeClient build() {
        return EventBridgeClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .build();
    }
}
