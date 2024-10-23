package au.edu.anu.quirkus.data;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

public class PostRootTest {

    @Test
    public void testPost() {
        List key = List.of("word");
        List upvoters = List.of("user1", "user2");
        Date d = new Date();
        PostRoot p = new PostRoot("id", "author", "title", "body", "tag", "subTag", key, d, upvoters, false, false);

        assertEquals("id", p.getId());
        assertEquals("author", p.getAuthorId());
        assertEquals("title", p.getTitle());
        assertEquals("body", p.getBody());
        assertEquals("tag", p.getTag());
        assertEquals("subTag", p.getSubTag());
        assertEquals(key, p.getKeywords());
        assertEquals(d, p.getCreated());
        assertEquals(false, p.isPublicPost());
        assertEquals(false, p.isAnonymousPost());
        assertEquals(upvoters, p.getUpvoters());
        assertEquals(2, p.getUpvoteCount());
        assertEquals("tag - subTag", p.getPrintableTag());

        PostRoot nulls = new PostRoot("id", "author", "title", "body", "tag", null, null, d, null, false, false);
        assertEquals(List.of(), nulls.getKeywords());
        assertEquals(List.of(), nulls.getUpvoters());
        assertEquals(0, nulls.getUpvoteCount());
        assertEquals("tag", nulls.getPrintableTag());
    }
}
