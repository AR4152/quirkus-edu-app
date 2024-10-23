package au.edu.anu.quirkus;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import au.edu.anu.quirkus.data.PostRoot;

public class PostRootModelTest {
    @Test
    public void testPostModel() {
        PostRoot p = new PostRoot("id", "author", "title", "body", "tag", "sub", List.of(), new Date(), List.of(), false, false);
        UserModel um = UserModelFactory.createLoadingUserModel(null);

        AuthorPostModel pm = new AuthorPostModel(p, um);

        assertEquals(p, pm.getPost());
        assertEquals(um, pm.getAuthor());
    }
}
