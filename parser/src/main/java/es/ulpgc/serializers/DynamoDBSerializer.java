package es.ulpgc.serializers;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import es.ulpgc.Token;

import java.util.*;
import java.util.stream.Collectors;

public class DynamoDBSerializer {
    public Map<String, AttributeValue> serialize(String filename, List<Token> tokens) {
        return Map.of(
                "filename", new AttributeValue().withS(filename),
                "tokens", new AttributeValue().withL(toDynamoDBList(tokens)));
    }

    private List<AttributeValue> toDynamoDBList(List<Token> tokens) {
        return tokens.stream().parallel()
                .map(DynamoDBSerializer::toMapAttributeValue)
                .collect(Collectors.toList());
    }

    private static AttributeValue toMapAttributeValue(Token token) {
        return new AttributeValue().withM(Map.of(
                "type", new AttributeValue().withS(String.valueOf(token.type())),
                "text", new AttributeValue().withS(token.text())));
    }
}
