package au.edu.anu.quirkus.ui.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.data.User;
import au.edu.anu.quirkus.ui.forum.options.RequestModel;

public class RequestsListAdapter extends ListAdapter<User, RequestsListAdapter.MyViewHolder> {
    private String courseId;

    public RequestsListAdapter(String courseId) {
        super(DIFF_CALLBACK);
        this.courseId = courseId;
    }

    @NonNull
    @Override
    public RequestsListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_requests, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsListAdapter.MyViewHolder holder, int position) {
        User user = getItem(position);
        holder.userId = user.getId();
        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
        holder.profilePic.setImageResource(user.getProfilePicture().getDrawable());
    }

    public static final DiffUtil.ItemCallback<User> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getEmail().equals(newItem.getEmail())
                    && oldItem.getName().equals(newItem.getName());
        }
    };

    public interface Callback {
        void callback(String userId);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RequestModel requestModel = new RequestModel();
        private String userId;
        private ImageView profilePic;
        private TextView name;
        private TextView email;
        private Button accept;
        private Button decline;

        public MyViewHolder(View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_icon);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            accept = itemView.findViewById(R.id.accept_button);
            decline = itemView.findViewById(R.id.decline_button);

            requestModel.setCourseId(courseId);

            // accept and decline users by setting the user's enrollment access
            accept.setOnClickListener(e -> {
                requestModel.acceptRequest(userId, result -> {
                    if (result) {
                        Toast.makeText(itemView.getContext(), "user accepted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(itemView.getContext(), "failed to accept user", Toast.LENGTH_LONG).show();
                    }
                });
            });

            decline.setOnClickListener(e -> {
                requestModel.declineRequest(userId, result -> {
                    if (result) {
                        Toast.makeText(itemView.getContext(), "user decline", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(itemView.getContext(), "failed to decline user", Toast.LENGTH_LONG).show();
                    }
                });
            });
        }
    }
}
