package au.edu.anu.quirkus.postSearch;

public class Tokenizer {
    private String buffer;
    private int index;
    private Token currentToken;

    public Tokenizer(String text) {
        buffer = text.trim();
        next();
    }

    public void next() {
        if (index == buffer.length()) {
            currentToken = null;
            return;
        }

        char chr;

        // Skip over all space characters
        while ((chr = buffer.charAt(index)) == ' ') {index++;}

        // Grab numbers
        if (Character.isDigit(chr)) {
            int start = index;
            while (index < buffer.length() && Character.isDigit(buffer.charAt(index))) {index++;}
            int i = Integer.parseInt(buffer.substring(start, index));
            currentToken = new Token(i);
            return;
        }

        // Grab other tokens
        switch (chr) {
            case '#' -> currentToken = new Token(Token.Type.HASH);
            case '@' -> currentToken = new Token(Token.Type.AT);
            case '%' -> currentToken = new Token(Token.Type.PERCENT);
            case '.' -> {
                char chr2 = buffer.charAt(index++);
                if (chr2 == '.') {
                    currentToken = new Token(Token.Type.DOTDOT);
                } else {
                    throw new RuntimeException("Expected dot");
                }
            }
            case '/' -> currentToken = new Token(Token.Type.FSLASH);
            case '-' -> currentToken = new Token(Token.Type.HYPEN);
            default -> {
                // Take all alphebetic characters into a string
                if (!Character.isAlphabetic(chr)) {
                    throw new RuntimeException("Not a valid character");
                }
                int start = index;
                while (index < buffer.length() && Character.isAlphabetic(buffer.charAt(index))) {index++;}
                currentToken = new Token(buffer.substring(start, index));
                return;
            }
        }
        index++;
    }

    public Token getCurrentToken() {
        return currentToken;
    }
}
