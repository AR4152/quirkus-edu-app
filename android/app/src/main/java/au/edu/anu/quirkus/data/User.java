package au.edu.anu.quirkus.data;

import java.util.Objects;

import au.edu.anu.quirkus.profilePictures.ProfilePicture;

public class User implements Comparable<User>{
    private final String id;
    private final String name;
    private final String email;
    private final ProfilePicture profilePicture;

    public User(String id, String name, String email, ProfilePicture profilePicture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    @Override
    public int compareTo(User user) {
        if (this.name.equals(user.name))
            return this.id.compareTo(user.id);

        return this.name.compareTo(user.name);
    }
}
