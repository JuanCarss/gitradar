package es.ulpgc;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import es.ulpgc.aws.s3.S3;
import es.ulpgc.aws.s3.S3ClientBuilder;
import es.ulpgc.readers.S3Reader;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Service implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        Map<String, String> params = input.getQueryStringParameters();
        S3Client s3Client = new S3ClientBuilder().withEndpoint(System.getenv("CUSTOM_ENDPOINT_URL")).at(System.getenv("REGION")).build();
        String response = new S3Reader().read(S3.getObjectFrom(s3Client, System.getenv("METRICS_BUCKET_ID"), params.get("filename")));
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setBody(response);
        responseEvent.setStatusCode(200);
        return responseEvent;
    }

    private String read(InputStream is) {
        try (BufferedInputStream bis = new BufferedInputStream(is)) {
            return new String(bis.readAllBytes());
        } catch (IOException e) {
            return "{}";
        }
    }
}
