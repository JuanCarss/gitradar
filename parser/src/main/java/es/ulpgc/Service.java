package es.ulpgc;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import es.ulpgc.aws.dynamodb.DynamoDB;
import es.ulpgc.aws.dynamodb.DynamoDBClientBuilder;
import es.ulpgc.aws.eventbridge.EventBridge;
import es.ulpgc.aws.eventbridge.EventBridgeClientBuilder;
import es.ulpgc.aws.s3.S3;
import es.ulpgc.aws.s3.S3ClientBuilder;
import es.ulpgc.deserializers.CodeUploadEventDeserializer;
import es.ulpgc.events.CodeUploadEvent;
import es.ulpgc.events.ParsedEvent;
import es.ulpgc.readers.S3Reader;
import es.ulpgc.serializers.DynamoDBSerializer;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

public class Service implements RequestHandler<InputStream, String> {
    @Override
    public String handleRequest(InputStream is, Context context) {
        CodeUploadEvent codeUploadEvent = new CodeUploadEventDeserializer().deserialize(read(is));
        List<Token> tokens = SyntacticAnalyzer
                .create(new JavaLexicalAnalyzer())
                .analyze(getLinesFromS3Object(buildS3Client(), codeUploadEvent.filename()));
        uploadTokensToDynamoDB(buildDynamoDBClient(), codeUploadEvent.filename(), tokens);
        ParsedEvent parsedEvent = buildParsedEvent(codeUploadEvent.filename());
        EventBridge.putEvent(buildEventBridgeClient(), parsedEvent);
        return tokens.toString();
    }

    private static Stream<String> getLinesFromS3Object(S3Client s3Client, String filename) {
        return new S3Reader().read(getS3Object(s3Client, filename));
    }

    private static S3Client buildS3Client() {
        return new S3ClientBuilder()
                .withEndpoint(System.getenv("CUSTOM_ENDPOINT_URL"))
                .at(System.getenv("REGION"))
                .build();
    }

    private static void uploadTokensToDynamoDB(AmazonDynamoDB dynamoDBClient, String filename, List<Token> tokens) {
        DynamoDB.putItem(dynamoDBClient,
                System.getenv("DYNAMODB_TABLE_NAME"),
                new DynamoDBSerializer().serialize(filename, tokens));
    }

    private static AmazonDynamoDB buildDynamoDBClient() {
        return new DynamoDBClientBuilder()
                .withEndpoint(System.getenv("CUSTOM_ENDPOINT_URL"))
                .at(System.getenv("REGION"))
                .build();
    }

    private static ParsedEvent buildParsedEvent(String filename) {
        return new ParsedEvent.ParsedEventBuilder()
                .withTs(Instant.now())
                .withSource("lambda.parser")
                .withFilename(filename)
                .build();
    }

    private static EventBridgeClient buildEventBridgeClient() {
        return new EventBridgeClientBuilder()
                .withEndpoint(System.getenv("CUSTOM_ENDPOINT_URL"))
                .at(System.getenv("REGION"))
                .build();
    }

    private String read(InputStream is) {
        try (BufferedInputStream bis = new BufferedInputStream(is)) {
            return new String(bis.readAllBytes());
        } catch (IOException e) {
            return "{}";
        }
    }

    private static ResponseInputStream<GetObjectResponse> getS3Object(S3Client s3Client, String filename) {
        return S3.getObjectFrom(s3Client, System.getenv("CODEFILES_BUCKET_ID"), filename);
    }
}
