package au.edu.anu.quirkus.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Date;
import java.util.List;

public class PostQuestionTest {

    @Test
    public void testPostQuestion() {
        PostQuestion p = new PostQuestion("id", "author", "title", "body", null, null, null, null, null, false, false, "replyId");

        assertEquals("replyId", p.getSolvedReplyId());
    }
}
