package au.edu.anu.quirkus.profilePicture;

import au.edu.anu.quirkus.R;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;

import au.edu.anu.quirkus.profilePictures.ProfilePicture;
import au.edu.anu.quirkus.profilePictures.ProfilePictureFactory;

public class ProfilePictureFactoryTest {
    @Test
    public void createPictures() {
        List<String> profilePics = List.of("Avatar", "Batman", "Bee", "Fox", "GermanShepherd", "Koala", "Panda", "Penguin", "Rhino", "Tiger", "Anonymous");

        List<Integer> imageIds   = List.of(R.drawable.avatar, R.drawable.batman, R.drawable.bee, R.drawable.fox, R.drawable.german_shepherd, R.drawable.koala, R.drawable.panda, R.drawable.penguin, R.drawable.rhino, R.drawable.tiger, R.drawable.hacker);
        for (int i = 0; i < profilePics.size(); i++) {
            String profilePic = profilePics.get(i);
            int imageId = imageIds.get(i);
            ProfilePicture avatar = ProfilePictureFactory.createProfilePicture(profilePic);
            assertEquals(profilePic, avatar.getClass().getSimpleName());
            assertEquals(imageId, avatar.getDrawable());
        }
    }

    @Test
    public void createDefaultPicture() {
        ProfilePicture avatar = ProfilePictureFactory.createProfilePicture(null);
        assertEquals("Anonymous", avatar.getClass().getSimpleName());

        ProfilePicture avatar2 = ProfilePictureFactory.createProfilePicture("something");
        assertEquals("Anonymous", avatar2.getClass().getSimpleName());
    }
}
