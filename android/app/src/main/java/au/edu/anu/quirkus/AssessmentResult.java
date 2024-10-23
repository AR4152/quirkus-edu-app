package au.edu.anu.quirkus;

public enum AssessmentResult {
    PENDING,
    PASS,
    FAIL;

    public static AssessmentResult createFromString(String str) {
        if (str == null) return PENDING;
        return switch (str) {
            case "PENDING" -> PENDING;
            case "PASS" -> PASS;
            case "FAIL" -> FAIL;
            default -> throw new RuntimeException("NOT A VALID ASSESSMENT RESULT: " + str);
        };
    }
}
