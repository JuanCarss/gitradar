package es.ulpgc.readers;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import es.ulpgc.Token;
import es.ulpgc.TokenReader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DynamoDBTokenReader implements TokenReader {

    private final DynamoDB dynamoDB;

    public DynamoDBTokenReader(DynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
    }

    @Override
    public List<Token> read(String filename, String tableName) {
        return getTokens(dynamoDB.getTable(tableName).getItem("filename", filename));
    }

    private List<Token> getTokens(Item item) {
        return ((List<Map<String, String>>) item.asMap().get("tokens"))
                .stream()
                .map(token -> new Token(Token.Type.valueOf(token.get("type")), token.get("text")))
                .collect(Collectors.toList());
    }
}
