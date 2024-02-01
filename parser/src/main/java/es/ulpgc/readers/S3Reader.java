package es.ulpgc.readers;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Stream;

public class S3Reader {
    public Stream<String> read(ResponseInputStream<GetObjectResponse> object) {
        return new BufferedReader(new InputStreamReader(object)).lines();
    }
}
