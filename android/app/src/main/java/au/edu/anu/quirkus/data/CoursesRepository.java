package au.edu.anu.quirkus.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import au.edu.anu.quirkus.data.firebase.FirestoreLiveDataDocument;
import au.edu.anu.quirkus.data.firebase.FirestoreLiveDataList;
import au.edu.anu.quirkus.postSearch.Parser;

public class CoursesRepository {
    private static final String TAG = "CoursesRepository";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // IMPORTANT: getAllUnarchivedCourses is Daniel's method
    public LiveData<List<Course>> getAllUnarchivedCourses() {
        Query q = db.collection("courses").whereNotEqualTo("archived", true);
        LiveData<List<DocumentSnapshot>> snap = new FirestoreLiveDataList(q);

        return Transformations.map(snap, doc -> {
            Log.d(TAG, "all courses updated.");
            return doc.stream().map(c -> createCourse(c)).collect(Collectors.toList());
        });
    }

    public LiveData<List<Course>> getCourses(List<String> courseIDs) {
        // If no courses early return nothing
        if (courseIDs.isEmpty()) {
            MutableLiveData<List<Course>> d = new MutableLiveData<>();
            d.postValue(new ArrayList<>());
            return d;
        }

        Query q = db.collection("courses").whereIn("__name__", courseIDs);

        LiveData<List<DocumentSnapshot>> snap = new FirestoreLiveDataList(q);

        return Transformations.map(snap, doc -> {
            Log.d(TAG, "courses updated.");
            return doc.stream().map(c -> createCourse(c)).collect(Collectors.toList());
        });
    }

    public LiveData<Course> getCourse(String courseID) {
        DocumentReference q = db.collection("courses").document(courseID);
        LiveData<DocumentSnapshot> snap = new FirestoreLiveDataDocument(q);

        return Transformations.map(snap, doc -> {
            Log.d(TAG, "course updated.");
            return createCourse(doc);
        });
    }

    public void setCourseArchiveStatus(String courseId, boolean status, Consumer<Boolean> callback) {
        DocumentReference doc = db.collection("courses").document(courseId);
        doc.update("archived", status).addOnCompleteListener(e -> callback.accept(e.isSuccessful()));
    }

    // Updates the entire courses tags with the provided tags
    public void setCourseTags(String courseId, Map<String, TagDefinition> tags) {
        Map<String, Map<String, Object>> rawTags = new HashMap<>();

        // Transform into a firebase serializable Object
        for (String key : tags.keySet()) {
            HashMap<String, Object> tag = new HashMap<>();
            TagDefinition t = tags.get(key);
            tag.put("color", t.getColor());
            List<Object> sTags = new ArrayList(t.getSubTags());

            // Don't insert if there are no subTags
            if (sTags != null && sTags.size() > 0) {
                tag.put("subTags", sTags);
            }
            rawTags.put(key, tag);
        }

        DocumentReference doc = db.collection("courses").document(courseId);
        doc.update("tags", rawTags);
    }

    private Course createCourse(DocumentSnapshot doc) {
        Map<String, Object> rawTags = (Map<String, Object>) doc.get("tags");

        Map<String, TagDefinition> tags = new HashMap<>();

        if (rawTags != null) {
            for (String key : rawTags.keySet()) {
                Map<String, Object> t = (Map<String, Object>) rawTags.get(key);

                List<String> subTags = (List<String>) t.get("subTags");

                Set<String> subTagSet = new HashSet<>();

                if (subTags != null && subTags.size() > 0) {
                    subTagSet.addAll(subTags);
                }
                tags.put(key, new TagDefinition(key, (String) t.get("color"), subTagSet));
            }
        }

        return new Course(doc.getId(),
                        doc.getString("name"),
                        doc.getString("description"),
                        tags,
                        Boolean.TRUE.equals(doc.get("archived")));
    }
}
