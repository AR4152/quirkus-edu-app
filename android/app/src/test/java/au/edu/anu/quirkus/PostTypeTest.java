package au.edu.anu.quirkus;

import org.junit.Test;

import static org.junit.Assert.*;

public class PostTypeTest {
    @Test
    public void testCreatePostType() {
        assertEquals(PostType.ANNOUNCEMENT, PostType.createPostType("ANNOUNCEMENT"));
        assertEquals(PostType.ASSESSMENT, PostType.createPostType("ASSESSMENT"));
        assertEquals(PostType.QUESTION, PostType.createPostType("QUESTION"));
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidPostTypeNone() {
        PostType.createPostType("");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidPostTypeCase() {
        PostType.createPostType("QuEsTiOn");
    }
}
