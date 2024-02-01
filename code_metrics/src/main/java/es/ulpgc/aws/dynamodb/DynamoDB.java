package es.ulpgc.aws.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.Collections;
import java.util.Map;

public class DynamoDB {
    public static Map<String, AttributeValue> getItem(AmazonDynamoDB client, String tableName, String key, String id) {
        return client.getItem(tableName, Collections.singletonMap(key, new AttributeValue().withS(id))).getItem();
    }
}
