package es.ulpgc.aws;

import software.amazon.awssdk.services.eventbridge.EventBridgeClient;

import java.net.URI;

public class AWSEventClientBuilder {

    private String endpoint;

    public AWSEventClientBuilder withEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public EventBridgeClient build() {
        return EventBridgeClient.builder()
                .endpointOverride(URI.create(endpoint))
                .build();
    }
}
