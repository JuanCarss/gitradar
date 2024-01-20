package es.ulpgc;

public interface LexicalAnalyzer {
    Token next();

    boolean hasNext();
}
