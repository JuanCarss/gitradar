package es.ulpgc;

import java.util.List;

public interface TokenReader {

    List<Token> read(String id, String source);
}
