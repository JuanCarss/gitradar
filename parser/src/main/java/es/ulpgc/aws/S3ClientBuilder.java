package es.ulpgc.aws;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3ClientBuilder {
    private String region;
    private String endpoint;

    public S3ClientBuilder at(String region) {
        this.region = region;
        return this;
    }

    public S3ClientBuilder with_endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public AmazonS3 build() {
        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(this.endpoint, this.region))
                .build();
    }
}
