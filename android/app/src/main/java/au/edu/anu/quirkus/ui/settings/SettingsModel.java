package au.edu.anu.quirkus.ui.settings;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.function.Consumer;

import au.edu.anu.quirkus.ActiveUser;
import au.edu.anu.quirkus.data.User;
import au.edu.anu.quirkus.data.UserRepository;
import au.edu.anu.quirkus.profilePictures.ProfilePicture;

public class SettingsModel extends ViewModel {

    private final UserRepository userRepository = new UserRepository();
    private LiveData<User> activeUser;

    public LiveData<User> getActiveUser() {
        if (activeUser == null) {
            activeUser = userRepository.getUserByID(ActiveUser.getFirebaseID());
        }
        return activeUser;
    }

    public void setUserProfilePicture(ProfilePicture pic) {
        userRepository.setUserProfilePicture(ActiveUser.getFirebaseID(), pic.getName());
    }

    public void setUserName(String newName, Consumer<Boolean> callback) {
        userRepository.setUserName(ActiveUser.getFirebaseID(), newName, callback);
    }

    public void setPassword(String newPassword, Consumer<Boolean> callback) {
        ActiveUser.updatePassword(newPassword, callback);
    }

    public void setEmail(String newEmail, Consumer<Boolean> callback) {
        ActiveUser.updateEmail(newEmail, success -> {
            if (success) {
                userRepository.setUserEmail(ActiveUser.getFirebaseID(), newEmail, callback);
            } else {
                callback.accept(false);
            }
        });
    }
}
