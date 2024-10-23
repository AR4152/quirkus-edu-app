package au.edu.anu.quirkus.profilePictures;

public abstract class ProfilePicture {
    public String getName() {
        return getClass().getSimpleName();
    }

    public abstract int getDrawable();
}