package es.ulpgc.aws.s3;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.ByteBuffer;

public class S3 {
    public static void putObject(S3Client client, String bucketName, String key, String object) {
        client.putObject(createPutRequest(bucketName, key), RequestBody.fromByteBuffer(createByteBuffer(object)));
    }

    private static ByteBuffer createByteBuffer(String object) {
        return ByteBuffer.wrap(object.getBytes());
    }

    private static PutObjectRequest createPutRequest(String bucket, String key) {
        return PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
    }
}
