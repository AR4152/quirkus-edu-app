package au.edu.anu.quirkus.data.firebase;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.List;

public class FirestoreLiveDataList extends LiveData<List<DocumentSnapshot>> {
    private final String TAG = "LiveDataFirebaseList";
    private final Query query;
    private ListenerRegistration lr;

    public FirestoreLiveDataList(Query query) {
        this.query = query;
    }
    @Override
    protected void onActive() {
        Log.d(TAG, "Activating listener: " + this);
        lr = query.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            Log.d(TAG, "Got new data");
            this.postValue(value.getDocuments());
        });
    }

    @Override
    protected void onInactive() {
        Log.d(TAG, "Deactivating listener: " + this);
        lr.remove();
    }
}
