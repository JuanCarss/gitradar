package es.ulpgc.aws.s3;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class S3 {
    public static ResponseInputStream<GetObjectResponse> getObjectFrom(S3Client client, String bucketName, String keyName) {
        return client.getObject(GetObjectRequest.builder().bucket(bucketName).key(keyName).build());
    }
}
