package es.ulpgc.aws.s3;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.stream.Stream;

public class S3 {
    public static ResponseInputStream<GetObjectResponse> getObjectFrom(S3Client client, String bucketName, String keyName) {
        return client.getObject(GetObjectRequest.builder().bucket(bucketName).key(keyName).build());
    }

    public static Stream<String> keysIn(S3Client client, String bucketName, String prefix) {
        return client.listObjects(ListObjectsRequest.builder().bucket(bucketName).prefix(prefix).build())
                .contents().stream().map(S3Object::key);
    }
}
