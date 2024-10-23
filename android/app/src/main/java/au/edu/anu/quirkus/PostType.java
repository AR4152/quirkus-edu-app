package au.edu.anu.quirkus;

public enum PostType {
    ANNOUNCEMENT,
    QUESTION,
    ASSESSMENT;

    public static PostType createPostType(String value) {
        if (value.equals("ANNOUNCEMENT")) {
            return PostType.ANNOUNCEMENT;
        } else if (value.equals("QUESTION")) {
            return PostType.QUESTION;
        } else if (value.equals("ASSESSMENT")) {
            return PostType.ASSESSMENT;
        } else {
            throw new RuntimeException("Unknown postType (" + value + ")");
        }
    }
}
