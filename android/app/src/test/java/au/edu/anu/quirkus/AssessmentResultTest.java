package au.edu.anu.quirkus;

import org.junit.Test;

import static org.junit.Assert.*;

public class AssessmentResultTest {
    @Test
    public void testAssessmentResult() {
        assertEquals(AssessmentResult.PENDING, AssessmentResult.createFromString(null));
        assertEquals(AssessmentResult.PENDING, AssessmentResult.createFromString("PENDING"));
        assertEquals(AssessmentResult.PASS, AssessmentResult.createFromString("PASS"));
        assertEquals(AssessmentResult.FAIL, AssessmentResult.createFromString("FAIL"));
    }

    @Test(expected = RuntimeException.class)
    public void testInvalid() {
        AssessmentResult.createFromString("");
        AssessmentResult.createFromString("COMP2100");
    }
}
