package es.ulpgc.aws.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.Map;

public class DynamoDB {
    public static void putItem(AmazonDynamoDB client, String tableName, Map<String, AttributeValue> item) {
        client.putItem(tableName, item);
    }
}
