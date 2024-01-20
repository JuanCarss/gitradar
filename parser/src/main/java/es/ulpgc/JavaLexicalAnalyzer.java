package es.ulpgc;

import static es.ulpgc.Token.Type.*;

public interface JavaLexicalAnalyzer extends LexicalAnalyzer {
    static JavaLexicalAnalyzer create(String text) {
        return create(text.toCharArray());
    }

    static JavaLexicalAnalyzer create(char[] chars) {
        return new JavaLexicalAnalyzer() {
            private static final char CR = '\n';
            private static final char OPEN_BRACKET = '{';
            private static final char CLOSE_BRACKET = '}';
            int index = 0;
            int openBrackets = 0;
            final StringBuilder sb = new StringBuilder();

            @Override
            public Token next() {
                while (hasChars()) {
                    char c = nextChar();
                    if (isEndOfLine(c)) {
                        if (text().isEmpty() || text().trim().charAt(0) == '/') {
                            clear();
                            continue;
                        }
                        clear();
                        return new Token(BR, "");
                    }
                    if (c == OPEN_BRACKET) {
                        openBrackets++;
                        return tokenFor(text());
                    }
                    if (c == CLOSE_BRACKET) {
                        openBrackets--;
                    }
                    add(c);
                }
                return new Token(BR, "");
            }

            @Override
            public boolean hasNext() {
                return hasChars();
            }

            private Token tokenFor(String text) {
                return isIdentifier() ? getIdentifierToken(text) : getControlToken();
            }

            private boolean isIdentifier() {
                return openBrackets < 3;
            }

            private Token getIdentifierToken(String text) {
                return new Token(IDENTIFIER, isClassName() ? getClassName(text) : getMethodName(text));
            }

            private Token getControlToken() {
                return new Token(CONTROL, "");
            }

            private boolean isClassName() {
                return openBrackets == 1;
            }

            private String getClassName(String text) {
                int start = 0;
                for (int i = 0; i < text.length(); i++) {
                    if (Character.isUpperCase(text.charAt(i))) {
                        start = i;
                        break;
                    }
                }
                int end = text.length();
                if (text.substring(start).contains(" ")) {
                    end = text.substring(start).indexOf(" ") + start;
                }
                return text.substring(start,  end);
            }

            private String getMethodName(String text) {
                int end = 0;
                for (int i = text.length() - 1; i >= 0; i--) {
                    if (text.charAt(i) == '(') {
                        end = i;
                        break;
                    }
                }
                return text.substring(text.substring(0, end).lastIndexOf(" "), end).trim();
            }

            private static boolean isEndOfLine(char c) {
                return c == CR;
            }

            private String text() {
                return sb.toString().trim();
            }

            private void add(char c) {
                sb.append(c);
            }

            private char nextChar() {
                return chars[index++];
            }

            private boolean hasChars() {
                return index < chars.length;
            }

            private void clear() {
                sb.setLength(0);
            }
        };
    }
}

