package es.ulpgc.readers;

import com.amazonaws.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class S3Reader {
    public Stream<String> read(S3Object object) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()))) {
            while (reader.ready()) lines.add(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines.stream();
    }
}
