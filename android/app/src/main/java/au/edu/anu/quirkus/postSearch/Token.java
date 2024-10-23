package au.edu.anu.quirkus.postSearch;

public class Token {
    public enum Type {NUMBER, STR, HASH, AT, PERCENT, DOTDOT, FSLASH, HYPEN}

    private final String str;
    private final Integer number;
    private final Type type;

    public Token(String str) {
        this.str = str;
        this.number = null;
        this.type = Type.STR;
    }

    public Token(Integer number) {
        this.str = null;
        this.number = number;
        this.type = Type.NUMBER;
    }

    public Token(Type type) {
        this.str = null;
        this.number = null;
        this.type = type;
    }

    public String getString() {
        return str;
    }

    public Integer getNumber() {
        return number;
    }

    public Type getType() {
        return type;
    }
}
