package au.edu.anu.quirkus.data.firebase;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

/// Adapted from https://firebase.blog/posts/2017/12/using-android-architecture-components
public class FirestoreLiveDataDocument extends LiveData<DocumentSnapshot> {
    private final String TAG = "LiveDataFirebaseDocument";
    private final DocumentReference ref;
    private ListenerRegistration lr;

    public FirestoreLiveDataDocument(DocumentReference ref) {
        this.ref = ref;
    }

    @Override
    protected void onActive() {
        Log.d(TAG, "Activating listener: " + this);
        lr = ref.addSnapshotListener((value, e) -> {
            if (e != null) Log.w(TAG, "Listen failed.", e);
            Log.d(TAG, "Got new data");
            this.postValue(value);
        });
    }

    @Override
    protected void onInactive() {
        Log.d(TAG, "Deactivating listener: " + this);
        lr.remove();
    }
}
