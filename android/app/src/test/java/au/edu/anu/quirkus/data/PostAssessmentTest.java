package au.edu.anu.quirkus.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import au.edu.anu.quirkus.AssessmentResult;
import au.edu.anu.quirkus.AssessmentResultTest;

public class PostAssessmentTest {

    @Test
    public void testPostAssessment() {
        PostAssessment p = new PostAssessment("id", "author", "title", "body", null, null, null, null, null, false, false, AssessmentResult.PASS, "replyId");

        assertEquals(AssessmentResult.PASS, p.getAssessmentResult());
        assertEquals("replyId", p.getResultReplyId());
    }
}
