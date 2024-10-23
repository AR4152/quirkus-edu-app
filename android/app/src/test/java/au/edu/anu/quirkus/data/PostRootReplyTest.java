package au.edu.anu.quirkus.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Date;
import java.util.List;

public class PostRootReplyTest {

    @Test
    public void testPostReply() {
        List key = List.of("word");
        List upvoters = List.of("user1", "user2");
        Date d = new Date();
        PostReply p = new PostReply("id", "author", "reply", "body", key, d, upvoters, false);

        assertEquals("id", p.getId());
        assertEquals("author", p.getAuthorId());
        assertEquals("reply", p.getReplyToId());
        assertEquals("body", p.getBody());
        assertEquals(key, p.getKeywords());
        assertEquals(d, p.getCreated());
        assertEquals(upvoters, p.getUpvoters());
        assertEquals(2, p.getUpvoteCount());
        assertEquals(false, p.isAnonymousPost());

        PostReply nulls = new PostReply("id", "author", "reply", "body", null, d, null, false);
        assertEquals(List.of(), nulls.getKeywords());
        assertEquals(List.of(), nulls.getUpvoters());
    }
}
