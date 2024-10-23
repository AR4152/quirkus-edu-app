package au.edu.anu.quirkus.profilePictures;

import android.util.Log;

import java.util.List;
import java.util.stream.Collectors;
// factory class, responsible for generating the profile picture instances based on the corresponding name.
public class ProfilePictureFactory {
    // tag for the class
    private static String TAG = "profilePictures.ProfilePictureFactory";

    // method for creating a profile picture instance based on the icon name
    public static ProfilePicture createProfilePicture(String name) {
        // If the name is null, return the default profile picture
        if (name == null) {
            return defaultProfilePicture(null);
        }
        // select the correct profile picture class based on the icons in profilePictures
        // If nothing matches any of the known icons, select the default profile picture
        return switch (name) {
            case "Avatar" -> new Avatar();
            case "Batman" -> new Batman();
            case "Bee" -> new Bee();
            case "Fox" -> new Fox();
            case "GermanShepherd" -> new GermanShepherd();
            case "Koala" -> new Koala();
            case "Panda" -> new Panda();
            case "Penguin" -> new Penguin();
            case "Rhino" -> new Rhino();
            case "Tiger" -> new Tiger();
            default -> defaultProfilePicture(name);
        };
    }

    // Method for the default profile picture generator with invalid tag logged
    private static ProfilePicture defaultProfilePicture(String name) {
        Log.d(TAG, "Invalid tag found (" + name + "), defaulting to Anonymous");
        return new Anonymous();
    }

    // Method that return a list of all the possible profile picture icons
    public static List<ProfilePicture> getAllProfilePictures() {
        // List of all profile picture names
        List<String> profilePics = List.of("Avatar", "Batman", "Bee", "Fox", "GermanShepherd", "Koala", "Panda", "Penguin", "Rhino", "Tiger", "Anonymous");

        // List of profile picture icon instances based on the names
        return profilePics.stream().map(name -> createProfilePicture(name)).collect(Collectors.toList());
    }
}
