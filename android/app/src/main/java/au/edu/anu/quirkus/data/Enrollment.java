package au.edu.anu.quirkus.data;

import au.edu.anu.quirkus.EnrollmentAccess;

public class Enrollment {
    private final String userId;
    private final String courseId;
    private final EnrollmentAccess access;

    public Enrollment(String userId, String courseId, String access) {
        this.userId = userId;
        this.courseId = courseId;

        this.access = switch (access) {
            case "BLOCKED" -> EnrollmentAccess.BLOCKED;
            case "PENDING" -> EnrollmentAccess.PENDING;
            case "STUDENT" -> EnrollmentAccess.STUDENT;
            case "STAFF" -> EnrollmentAccess.STAFF;
            default -> {
                throw new RuntimeException("Invalid enrollment type");
            }
        };
    }

    public String getUserId() {
        return userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public EnrollmentAccess getAccess() {
        return access;
    }
}
