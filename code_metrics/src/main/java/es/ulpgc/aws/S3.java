package es.ulpgc.aws;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.ByteBuffer;

public class S3 {
    public static void putObject(S3Client s3Client, String bucket, String key, String object) {
        s3Client.putObject(createPutRequest(bucket, key), RequestBody.fromByteBuffer(createByteBuffer(object)));
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
