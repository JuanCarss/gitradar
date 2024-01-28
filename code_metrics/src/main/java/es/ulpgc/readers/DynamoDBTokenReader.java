package es.ulpgc.readers;


import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import es.ulpgc.Token;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DynamoDBTokenReader {
    public List<Token> read(Map<String, AttributeValue> item) {
        return item.get("tokens").getL()
                .parallelStream()
                .map(AttributeValue::getM)
                .map(DynamoDBTokenReader::createToken)
                .collect(Collectors.toList());
    }

    private static Token createToken(Map<String, AttributeValue> token) {
        return new Token(Token.Type.valueOf(token.get("type").getS()), token.get("text").getS());
    }
}
