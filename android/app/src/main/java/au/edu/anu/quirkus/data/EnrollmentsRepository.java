package au.edu.anu.quirkus.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import au.edu.anu.quirkus.data.firebase.FirestoreLiveDataList;

public class EnrollmentsRepository {
    private final static String TAG = "EnrollmentsRepository";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Enrollment>> getUserEnrolledCourses(String userID) {
        Query q = db.collectionGroup("enrollments").whereEqualTo("user", userID);

        LiveData<List<DocumentSnapshot>> snap = new FirestoreLiveDataList(q);

        return Transformations.map(snap, doc -> {
            Log.d(TAG, "enrollments updated.");
            return doc.stream().map(c -> createEnrollment(c)).collect(Collectors.toList());
        });
    }

    public LiveData<List<Enrollment>> getStaffForCourse(String courseID) {
        Query q = db.collection("courses/" + courseID + "/enrollments").whereEqualTo("access", "STAFF");

        LiveData<List<DocumentSnapshot>> snap = new FirestoreLiveDataList(q);

        return Transformations.map(snap, doc -> {
            Log.d(TAG, "staff list updated.");
            return doc.stream().map(c -> createEnrollment(c)).collect(Collectors.toList());
        });
    }

    public LiveData<List<Enrollment>> getEnrollmentsForCourse(String courseID) {
        Query q = db.collection("courses/" + courseID + "/enrollments").whereIn("access", List.of("STAFF", "STUDENT"));

        LiveData<List<DocumentSnapshot>> snap = new FirestoreLiveDataList(q);

        return Transformations.map(snap, doc -> {
            Log.d(TAG, "enrollments list updated.");
            return doc.stream().map(c -> createEnrollment(c)).collect(Collectors.toList());
        });
    }

    public LiveData<List<Enrollment>> getPendingRequests(String courseID) {
        Query q = db.collection("courses/" + courseID + "/enrollments").whereEqualTo("access", "PENDING");

        LiveData<List<DocumentSnapshot>> snap = new FirestoreLiveDataList(q);

        return Transformations.map(snap, doc -> {
            Log.d(TAG, "enrollments list updated.");
            return doc.stream().map(c -> createEnrollment(c)).collect(Collectors.toList());
        });
    }

    public void setEnrollmentAccess(String courseId, String userId, String access, Consumer<Boolean> callback) {
        DocumentReference doc = db.collection("courses/" + courseId + "/enrollments").document(userId);
        doc.update("access", access).addOnCompleteListener(e -> callback.accept(e.isSuccessful()));
    }

    public void leaveCourse(String courseId, String userId, Consumer<Boolean> callback) {
        db.collection("courses/" + courseId + "/enrollments")
                .document(userId)
                .delete()
                .addOnCompleteListener(e -> callback.accept(e.isSuccessful()));
    }

    private Enrollment createEnrollment(DocumentSnapshot doc) {
        return new Enrollment(doc.getId(),
                doc.getReference().getParent().getParent().getId(),
                doc.getString("access")
        );
    }
}
