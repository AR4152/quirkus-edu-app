package au.edu.anu.quirkus.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import au.edu.anu.quirkus.ActiveUser;
import au.edu.anu.quirkus.AssessmentResult;
import au.edu.anu.quirkus.Keywords;
import au.edu.anu.quirkus.data.firebase.FirestoreLiveDataDocument;
import au.edu.anu.quirkus.data.firebase.FirestoreLiveDataList;
import au.edu.anu.quirkus.postSearch.Parser;

public class PostsRepository {
    private static final String TAG = "PostsRepository";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Post>> getPostsInCourse(String courseID, boolean getPrivatePosts) {
        Query q = db.collection("courses/" + courseID + "/posts");

        // TODO: Merge self of publicPosts
//        if (!getPrivatePosts) {
//            q = q.whereEqualTo("publicPost", true);
//        }

        q = q.orderBy("created", Query.Direction.DESCENDING);
        LiveData<List<DocumentSnapshot>> snap = new FirestoreLiveDataList(q);

        return Transformations.map(snap, doc -> {
            Log.d(TAG, "posts updated.");
            Stream<Post> posts = doc.stream().map(c -> createPost(c));
            if (!getPrivatePosts) {
                posts = posts.filter(p -> {
                    PostRoot post = (PostRoot) p;
                    return post.isPublicPost() || post.getAuthorId().equals(ActiveUser.getFirebaseID());
                });
            }
            return posts.collect(Collectors.toList());
        });
    }

    /// It is expected that searchParser has already parsed and that applyConditionsToQuery can be called.
    public LiveData<List<Post>> getPostsInCourseSearch(String courseId, Parser searchParser, boolean getPrivatePosts) {
        Query q = db.collection("courses/" + courseId + "/posts");

        // TODO: Merge self of publicPosts
//        if (!getPrivatePosts) {
//            q = q.whereEqualTo("publicPost", true);
//        }

        q = q.orderBy("created", Query.Direction.DESCENDING);

        q = searchParser.applyConditionsToQuery(q);

        LiveData<List<DocumentSnapshot>> snap = new FirestoreLiveDataList(q);

        return Transformations.map(snap, doc -> {
            Log.d(TAG, "posts updated.");
            Stream<Post> posts = doc.stream().map(c -> createPost(c));
            if (!getPrivatePosts) {
                posts = posts.filter(p -> {
                    PostRoot post = (PostRoot) p;
                    return post.isPublicPost() || post.getAuthorId().equals(ActiveUser.getFirebaseID());
                });
            }
            return posts.collect(Collectors.toList());
        });
    }

    public LiveData<Post> getPostByID(String courseID, String postID) {
        DocumentReference q = db.collection("courses/" + courseID + "/posts").document(postID);
        LiveData<DocumentSnapshot> snap = new FirestoreLiveDataDocument(q);

        return Transformations.map(snap, doc -> {
            Log.d(TAG, "post updated.");
            return createPost(doc);
        });
    }

    public void addUpvoteToPost(String courseId, String postId, String userId, Consumer<Boolean> callback) {
        DocumentReference doc = db.collection("courses/" + courseId + "/posts").document(postId);
        doc.update("upvoters", FieldValue.arrayUnion(userId)).addOnCompleteListener(e -> callback.accept(e.isSuccessful()));
    }

    public void removeUpvoteFromPost(String courseId, String postId, String userId, Consumer<Boolean> callback) {
        DocumentReference doc = db.collection("courses/" + courseId + "/posts").document(postId);
        doc.update("upvoters", FieldValue.arrayRemove(userId)).addOnCompleteListener(e -> callback.accept(e.isSuccessful()));
    }

    public void updateSolvedReplyId(String courseId, String postId, String replyId, Consumer<Boolean> callback) {
        DocumentReference doc = db.collection("courses/" + courseId + "/posts").document(postId);
        doc.update("solvedReplyId", replyId).addOnCompleteListener(e -> callback.accept(e.isSuccessful()));
    }

    public void updateAssessmentResult(String courseId, String postId, String replyId, AssessmentResult result, Consumer<Boolean> callback) {
        DocumentReference doc = db.collection("courses/" + courseId + "/posts").document(postId);
        doc.update("resultReplyId", replyId).addOnCompleteListener(e -> callback.accept(e.isSuccessful()));
        doc.update("result", result.toString()).addOnCompleteListener(e -> callback.accept(e.isSuccessful()));
    }

    public PostRoot createPost(DocumentSnapshot doc) {
        return switch (doc.getString("postType")) {
             case "QUESTION" -> new PostQuestion(doc.getId(),
                    doc.getString("authorId"),
                    doc.getString("title"),
                    doc.getString("body"),
                    doc.getString("tag"),
                    doc.getString("subTag"),
                    (List<String>) doc.get("keywords"),
                    doc.getDate("created"),
                    (List<String>) doc.get("upvoters"),
                    Boolean.TRUE.equals(doc.getBoolean("publicPost")),
                    Boolean.TRUE.equals(doc.getBoolean("anonymousPost")),
                    doc.getString("solvedReplyId")
                );
             case "ANNOUNCEMENT" -> new PostAnnouncement(doc.getId(),
                     doc.getString("authorId"),
                     doc.getString("title"),
                     doc.getString("body"),
                     doc.getString("tag"),
                     doc.getString("subTag"),
                     (List<String>) doc.get("keywords"),
                     doc.getDate("created"),
                     (List<String>) doc.get("upvoters"),
                     Boolean.TRUE.equals(doc.getBoolean("publicPost")),
                     Boolean.TRUE.equals(doc.getBoolean("anonymousPost"))
             );
            case "ASSESSMENT" -> new PostAssessment(doc.getId(),
                    doc.getString("authorId"),
                    doc.getString("title"),
                    doc.getString("body"),
                    doc.getString("tag"),
                    doc.getString("subTag"),
                    (List<String>) doc.get("keywords"),
                    doc.getDate("created"),
                    (List<String>) doc.get("upvoters"),
                    Boolean.TRUE.equals(doc.getBoolean("publicPost")),
                    Boolean.TRUE.equals(doc.getBoolean("anonymousPost")),
                    AssessmentResult.createFromString(doc.getString("assessmentResult")),
                    doc.getString("resultReplyId")
            );
            default -> throw new RuntimeException("Unknown postType");
        };
    }

    public void createNewPost(String courseId, String authorId, String postType, String title, String body, String tag, String subTag, boolean publicPost, boolean anonymousPost, Consumer<Boolean> callback) {
        Log.d(TAG, "createPost:start");

        Map<String, Object> post = new HashMap<>();
        post.put("authorId", authorId);
        post.put("postType", postType);
        post.put("title", title);
        post.put("body", body);
        post.put("tag", tag);
        post.put("subTag", subTag);
        post.put("keywords", new ArrayList<>(Keywords.getKeywordsFromString(title + " " + body)));
        post.put("created", FieldValue.serverTimestamp());
        post.put("publicPost", publicPost);
        post.put("anonymousPost", anonymousPost);

        db.collection("courses/" + courseId + "/posts").add(post).addOnCompleteListener(e -> {
            callback.accept(e.isSuccessful());
        });
    }
}
