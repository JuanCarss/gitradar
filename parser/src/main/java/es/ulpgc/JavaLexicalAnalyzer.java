package es.ulpgc;

import java.util.Collections;
import java.util.List;

import static es.ulpgc.Token.Type.*;

public class JavaLexicalAnalyzer implements LexicalAnalyzer {
    @Override
    public List<Token> analyze(String line) {
        line = line.trim();
        if (isEmpty(line) || isComment(line)) return Collections.emptyList();
        if (isClassDefinition(line)) return tokens(new Token(IDENTIFIER, getClassName(line)));
        if (isSpecialToken(line)) return tokens(findSpecialToken(line));
        return List.of(new Token(LINE_BREAK, ""));
    }

    private Token findSpecialToken(String line) {
        return isMethodDefinition(line) ? new Token(IDENTIFIER, getMethodName(line)) : new Token(CONTROL_STRUCTURE, "");
    }

    private boolean isMethodDefinition(String line) {
        return line.substring(0, line.indexOf("(")).split(" ").length > 1;
    }

    private boolean isSpecialToken(String line) {
        return line.contains(") {");
    }

    private List<Token> tokens(Token token) {
        return List.of(token, new Token(LINE_BREAK, ""));
    }

    private boolean isClassDefinition(String line) {
        return line.contains(" class ");
    }

    private String getMethodName(String line) {
        int end = line.indexOf("(") - 1;
        StringBuilder sb = new StringBuilder();
        for (int i = end; i > 0; i--) {
            if (Character.isSpaceChar(line.charAt(i))) break;
            sb.append(line.charAt(i));
        }
        return sb.reverse().toString();
    }

    private String getClassName(String line) {
        int start = line.indexOf("class") + "class".length() + 1;
        int end = line.substring(start).indexOf(" ") + start;
        return line.substring(start, end);
    }

    private boolean isEmpty(String line) {
        return line.isEmpty();
    }

    private boolean isComment(String line) {
        return line.charAt(0) == '/';
    }
}

