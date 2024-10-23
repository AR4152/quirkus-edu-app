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
import au.edu.anu.quirkus.Keywords;
import au.edu.anu.quirkus.data.firebase.FirestoreLiveDataDocument;
import au.edu.anu.quirkus.data.firebase.FirestoreLiveDataList;
import au.edu.anu.quirkus.postSearch.Parser;

public class PostsReplyRepository {
    private static String TAG = "PostsReplyRepository";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Post>> getReplies(String courseID, String postID) {
        Query q = db.collection("courses/" + courseID + "/posts/" + postID + "/replies").orderBy("created", Query.Direction.ASCENDING);

        LiveData<List<DocumentSnapshot>> snap = new FirestoreLiveDataList(q);

        return Transformations.map(snap, doc -> {
            Log.d(TAG, "posts replies updated.");
            return doc.stream().map(c -> createReply(c)).collect(Collectors.toList());
        });
    }

    /// It is expected that searchParser has already parsed and that applyConditionsToQuery can be called.
    public LiveData<List<Post>> getPostsInCourseSearch(String courseId, Parser searchParser) {
        Query q = db.collectionGroup("replies");

        q = q.orderBy("created", Query.Direction.DESCENDING);

        q = searchParser.applyConditionsToQuery(q);

        LiveData<List<DocumentSnapshot>> snap = new FirestoreLiveDataList(q);

        return Transformations.map(snap, doc -> {
            Log.d(TAG, "posts replies updated.");
            return doc.stream().filter(d -> d.getReference().getParent().getParent().getId().equals(courseId)).map(c -> createReply(c)).collect(Collectors.toList());
        });
    }


    public LiveData<Post> getReplyByID(String courseID, String postID, String replyID) {
        DocumentReference q = db.collection("courses/" + courseID + "/posts/" + postID + "/replies").document(replyID);
        LiveData<DocumentSnapshot> snap = new FirestoreLiveDataDocument(q);

        return Transformations.map(snap, doc -> {
            Log.d(TAG, "post reply updated.");
            return createReply(doc);
        });
    }

    public void addUpvoteToReply(String courseId, String postId, String replyId, String userId, Consumer<Boolean> callback) {
        DocumentReference doc = db.collection("courses/" + courseId + "/posts/" + postId + "/replies").document(replyId);
        doc.update("upvoters", FieldValue.arrayUnion(userId)).addOnCompleteListener(e -> callback.accept(e.isSuccessful()));
    }

    public void removeUpvoteFromReply(String courseId, String postId, String replyId, String userId, Consumer<Boolean> callback) {
        DocumentReference doc = db.collection("courses/" + courseId + "/posts/" + postId + "/replies").document(replyId);
        doc.update("upvoters", FieldValue.arrayRemove(userId)).addOnCompleteListener(e -> callback.accept(e.isSuccessful()));
    }

    public PostReply createReply(DocumentSnapshot doc) {
        return new PostReply(doc.getId(),
            doc.getString("authorId"),
            doc.getString("replyToId"),
            doc.getString("body"),
            (List<String>) doc.get("keywords"),
            doc.getDate("created"),
            (List<String>) doc.get("upvoters"),
            Boolean.TRUE.equals(doc.getBoolean("anonymousPost"))
        );
    }

    public void createNewReply(String courseId, String postId, String authorId, String replyToId, String body, Consumer<Boolean> callback) {
        Log.d(TAG, "createReply:start");
        Map<String, Object> post = new HashMap<>();
        post.put("authorId", authorId);
        post.put("replyToId", replyToId);
        post.put("body", body);
        post.put("keywords", new ArrayList<>(Keywords.getKeywordsFromString(body)));
        post.put("created", FieldValue.serverTimestamp());

        db.collection("courses/" + courseId + "/posts/" + postId + "/replies").add(post).addOnCompleteListener(e -> {
            callback.accept(e.isSuccessful());
        });
    }
}
