package es.ulpgc.aws;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

public class AWSS3ClientBuilder {
    private String region;
    private String endpoint;

    public AWSS3ClientBuilder withRegion(String region) {
        this.region = region;
        return this;
    }

    public AWSS3ClientBuilder withEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public S3Client build() {
        return S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .build();
    }
}
