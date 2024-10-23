 package au.edu.anu.quirkus;

import au.edu.anu.quirkus.data.User;

public class UserModel {
    private final User user;
    private final boolean staff;

    public UserModel(User user, boolean staff) {
        this.user = user;
        this.staff = staff;
    }

    public User getUser() {
        return user;
    }

    public boolean isStaff() {
        return staff;
    }
}
