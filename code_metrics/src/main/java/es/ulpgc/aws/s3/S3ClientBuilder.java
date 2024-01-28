package es.ulpgc.aws.s3;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

public class S3ClientBuilder {
    private String region;
    private String endpoint;

    public S3ClientBuilder withEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public S3ClientBuilder at(String region) {
        this.region = region;
        return this;
    }

    public S3Client build() {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .build();
    }
}
