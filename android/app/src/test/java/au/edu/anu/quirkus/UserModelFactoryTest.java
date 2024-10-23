package au.edu.anu.quirkus;

import org.junit.Test;

import static org.junit.Assert.*;

import au.edu.anu.quirkus.data.User;
import au.edu.anu.quirkus.profilePictures.Avatar;

public class UserModelFactoryTest {
    @Test
    public void testUserModelFactory() {
        User u = new User("id","name", null, new Avatar());
        UserModel um = new UserModel(u, false);

        assertEquals(u, um.getUser());
        assertEquals(false, um.isStaff());
    }
}
