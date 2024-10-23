package au.edu.anu.quirkus.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import au.edu.anu.quirkus.profilePictures.Avatar;
import au.edu.anu.quirkus.profilePictures.ProfilePicture;

public class UserTest {

    @Test
    public void testUser() {
        ProfilePicture profile = new Avatar();
        User u = new User("id", "name", "email", profile);

        assertEquals("id", u.getId());
        assertEquals("name", u.getName());
        assertEquals("email", u.getEmail());
        assertEquals(profile, u.getProfilePicture());
    }
}
