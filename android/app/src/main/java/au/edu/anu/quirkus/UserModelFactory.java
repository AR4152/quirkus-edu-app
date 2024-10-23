package au.edu.anu.quirkus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import au.edu.anu.quirkus.data.Enrollment;
import au.edu.anu.quirkus.data.User;
import au.edu.anu.quirkus.profilePictures.Anonymous;
import au.edu.anu.quirkus.profilePictures.Avatar;

public class UserModelFactory {
    // Creates a list of userModels based on if they are are staff in the enrollments list
    public static List<UserModel> createUserModels(List<User> users, List<Enrollment> staffEnrollments) {
        if (users == null) return new ArrayList<>();
        if (staffEnrollments == null) staffEnrollments = new ArrayList<>();

        // Ensure list only contains STAFF
        List<String> staff = staffEnrollments.stream().filter(e -> e.getAccess().equals(EnrollmentAccess.STAFF)).map(user -> user.getUserId()).collect(Collectors.toList());

        // Map and get staff status
        return users.stream().map(user -> new UserModel(user, staff.contains(user.getId()))).collect(Collectors.toList());
    }

    public static UserModel createLoadingUserModel(String userId) {
        return new UserModel(new User(userId, "Loading...", null, new Avatar()), false);
    }

    public static UserModel createAnonymousUserModel() {
        return new UserModel(new User(null, "Anonymous User", null, new Anonymous()), false);
    }
}
