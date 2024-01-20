package es.ulpgc;

import java.util.ArrayList;
import java.util.List;

public interface SyntacticAnalyzer {
    List<Token> analyze();

    static SyntacticAnalyzer create(final LexicalAnalyzer lexicalAnalyzer) {
        return () -> {
            List<Token> tokens = new ArrayList<>();
            while (lexicalAnalyzer.hasNext()) {
                Token token = lexicalAnalyzer.next();
                tokens.add(token);
            }
            return tokens;
        };
    }
}
