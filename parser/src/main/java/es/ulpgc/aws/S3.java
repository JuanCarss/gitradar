package es.ulpgc.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class S3 {
    public static S3Object getObjectFrom(AmazonS3 client, String bucketName, String keyName) {
        return client.getObject(new GetObjectRequest(bucketName, keyName));
    }
}
