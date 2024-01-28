package es.ulpgc.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.Collections;
import java.util.Map;

public class DynamoDB {
    public static Map<String, AttributeValue> getItem(AmazonDynamoDB clientDynamodb, String tablename, String key, String id) {
        return clientDynamodb.getItem(tablename, Collections.singletonMap(key, new AttributeValue().withS(id))).getItem();
    }
}
