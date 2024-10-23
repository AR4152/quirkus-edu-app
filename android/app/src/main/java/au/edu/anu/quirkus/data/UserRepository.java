package au.edu.anu.quirkus.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import au.edu.anu.quirkus.data.firebase.FirestoreLiveDataDocument;
import au.edu.anu.quirkus.profilePictures.ProfilePictureFactory;
import au.edu.anu.quirkus.data.firebase.FirestoreLiveDataList;

public class UserRepository {
    private final static String TAG = "UserRepository";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map<String, LiveData<User>> users = new HashMap<>();

    public LiveData<User> getUserByID(String id) {
        LiveData<User> user = users.get(id);

        if (user == null) {
            DocumentReference ref = db.collection("users").document(id);

            LiveData<DocumentSnapshot> snap = new FirestoreLiveDataDocument(ref);

            user = Transformations.map(snap, doc -> {
                Log.d(TAG, "user data updated: " + id);
                return createUser(doc);
            });
            users.put(id, user);
        }
        return user;
    }

    public void setUserProfilePicture(String id, String profilePicture) {
        db.collection("users").document(id).update("profilePicture", profilePicture);
    }

    public void createNewUser(String id, String name, String email) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);

        db.collection("users").document(id).set(map);
    }

    public void setUserName(String id, String name, Consumer<Boolean> callback) {
        db.collection("users").document(id).update("name", name).addOnCompleteListener(e -> callback.accept(e.isSuccessful()));
    }

    public void setUserEmail(String id, String email, Consumer<Boolean> callback) {
        db.collection("users").document(id).update("email", email).addOnCompleteListener(e -> callback.accept(e.isSuccessful()));
    }


    public LiveData<List<User>> getAllUsers() {
        Query q = db.collection("users");
        LiveData<List<DocumentSnapshot>> snap = new FirestoreLiveDataList(q);

        return Transformations.map(snap, doc -> {
            Log.d(TAG, "users updated.");
            return doc.stream().map(d -> createUser(d)).collect(Collectors.toList());
        });
    }

    public LiveData<List<User>> getUsersById(List<String> ids) {
        // If no ids early return nothing
        if (ids.isEmpty()) {
            MutableLiveData<List<User>> d = new MutableLiveData<>();
            d.postValue(List.of());
            return d;
        }

        Query q = db.collection("users").whereIn("__name__", ids);

        LiveData<List<DocumentSnapshot>> snap = new FirestoreLiveDataList(q);
        return Transformations.map(snap, doc -> {
            Log.d(TAG, "users updated.");
            return doc.stream().map(d -> createUser(d)).collect(Collectors.toList());
        });
    }

    private User createUser(DocumentSnapshot doc) {
        return new User(
            doc.getId(),
            doc.getString("name"),
            doc.getString("email"),
            ProfilePictureFactory.createProfilePicture(doc.getString("profilePicture"))
        );
    }
}
