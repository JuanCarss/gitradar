package es.ulpgc;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import es.ulpgc.aws.s3.S3;
import es.ulpgc.aws.s3.S3ClientBuilder;
import es.ulpgc.readers.S3Reader;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Map;

public class Service implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        Map<String, String> params = input.getQueryStringParameters();
        S3Client s3Client = buildS3Client();
        if (!metricsExists(s3Client, params.get("filename"))) {
            responseEvent.setBody("File does not exist.");
            responseEvent.setStatusCode(404);
            return responseEvent;
        }
        String metrics = getFileMetrics(s3Client, params.get("filename"));
        if (isClassRequest(params)) metrics = getClassMetrics(metrics, params.get("class"));
        if (metrics.isEmpty()) {
            responseEvent.setBody("Class does not exist in file.");
            responseEvent.setStatusCode(404);
            return responseEvent;
        }
        responseEvent.setBody(metrics);
        responseEvent.setStatusCode(200);
        return responseEvent;
    }

    private static boolean isClassRequest(Map<String, String> params) {
        return params.containsKey("class");
    }

    private static S3Client buildS3Client() {
        return new S3ClientBuilder()
                .withEndpoint(System.getenv("CUSTOM_ENDPOINT_URL"))
                .at(System.getenv("REGION"))
                .build();
    }

    private static boolean metricsExists(S3Client s3Client, String filename) {
        return S3.keysIn(s3Client, System.getenv("METRICS_BUCKET_ID"), "")
                .anyMatch(s -> s.equals(filename));
    }

    private static String getFileMetrics(S3Client s3Client, String filename) {
        return new S3Reader().read(S3.getObjectFrom(s3Client, System.getenv("METRICS_BUCKET_ID"), filename));
    }

    private String getClassMetrics(String metrics, String className) {
        JsonArray classes = JsonParser.parseString(metrics).getAsJsonObject().get("classes").getAsJsonArray();
        for (int i = 0; i < classes.size(); i++) {
            JsonObject classObject = classes.get(i).getAsJsonObject();
            if (classObject.get("identifier").getAsString().equals(className)) return classObject.toString();
        }
        return "";
    }
}
