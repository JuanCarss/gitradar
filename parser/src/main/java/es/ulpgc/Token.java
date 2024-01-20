package es.ulpgc;

public record Token(Type type, String text) {
    public enum Type {
        CONTROL, IDENTIFIER, BR
    }
}
