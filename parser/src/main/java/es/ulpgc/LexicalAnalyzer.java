package es.ulpgc;

import java.util.List;

public interface LexicalAnalyzer {
    List<Token> analyze(String line);
}
