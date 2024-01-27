package es.ulpgc;

public record Token(Type type, String text) {
    public enum Type {
        IDENTIFIER, CONTROL_STRUCTURE, LINE_BREAK
    }
}
