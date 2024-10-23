package au.edu.anu.quirkus.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import au.edu.anu.quirkus.EnrollmentAccess;

public class EnrollmentTest {

    @Test
    public void testEnrollment() {
        Enrollment e = new Enrollment("user", "asda_comp2100__asda", "STUDENT");

        assertEquals("user", e.getUserId());
        assertEquals("asda_comp2100__asda", e.getCourseId());
        assertEquals(EnrollmentAccess.STUDENT, e.getAccess());

        // Test the other access types
        Enrollment b = new Enrollment("", "", "BLOCKED");
        assertEquals(EnrollmentAccess.BLOCKED, b.getAccess());

        // Test the other access types
        Enrollment p = new Enrollment("", "", "PENDING");
        assertEquals(EnrollmentAccess.PENDING, p.getAccess());

        // Test the other access types
        Enrollment s = new Enrollment("", "", "STAFF");
        assertEquals(EnrollmentAccess.STAFF, s.getAccess());
    }

    @Test(expected = RuntimeException.class)
    public void testEnrollmentInvalidAccess() {
        new Enrollment("", "", "BLAH");
        new Enrollment("", "", "Staff");
    }
}
