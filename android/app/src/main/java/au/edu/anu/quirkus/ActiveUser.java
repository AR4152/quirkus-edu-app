package au.edu.anu.quirkus;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.util.function.Consumer;

import au.edu.anu.quirkus.data.UserRepository;

public class ActiveUser {
    static String TAG = "User";
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static boolean isLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public static void login(String email, String password, Consumer<Boolean> callback) {
        Log.d(TAG, "login:start (" + email + ")");
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(e -> {
            if (e.isSuccessful()) {
                Log.d(TAG, "login:success (" + email + ")");
                callback.accept(true);
            } else {
                Log.d(TAG, "login:fail (" + email + ")");
                callback.accept(false);
            }
        });
    }

    public static void signup(String email, String password, String name, Consumer<Boolean> callback) {
        Log.d(TAG, "signup:start (" + email + ")");
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(e -> {
            if (e.isSuccessful()) {
                Log.d(TAG, "signup:success (" + email + ")");
                UserRepository u = new UserRepository();
                u.createNewUser(getFirebaseID(), name, email);
                callback.accept(true);
            } else {
                Log.d(TAG, "signup:fail (" + email + ")");
                callback.accept(false);
            }
        });
    }

    public static void updatePassword(String password, Consumer<Boolean> callback) {
        Log.d(TAG, "update password");
        mAuth.getCurrentUser().updatePassword(password).addOnCompleteListener(e -> callback.accept(e.isSuccessful()));
    }

    public static void updateEmail(String email, Consumer<Boolean> callback) {
        Log.d(TAG, "update email");
        mAuth.getCurrentUser().updateEmail(email).addOnCompleteListener(e -> callback.accept(e.isSuccessful()));
        // TODO: Update public database /users
    }

    public static void logout() {
        mAuth.signOut();
    }

    public static String getFirebaseID() {
        return mAuth.getUid();
    }
}
