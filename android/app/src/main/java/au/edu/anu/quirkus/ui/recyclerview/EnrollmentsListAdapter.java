package au.edu.anu.quirkus.ui.recyclerview;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.edu.anu.quirkus.ActiveUser;
import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.UserModel;
import au.edu.anu.quirkus.data.Enrollment;
import au.edu.anu.quirkus.data.EnrollmentsRepository;
import au.edu.anu.quirkus.data.User;
import au.edu.anu.quirkus.data.UserRepository;
import au.edu.anu.quirkus.ui.forum.Forum;
import au.edu.anu.quirkus.ui.forum.ForumModel;
import au.edu.anu.quirkus.ui.forum.options.EnrollmentsModel;

public class EnrollmentsListAdapter extends ListAdapter<au.edu.anu.quirkus.UserModel, EnrollmentsListAdapter.MyViewHolder> {
    private String courseID;
    ForumModel forumModel = new ForumModel();

    public EnrollmentsListAdapter(String courseID) {
        super(DIFF_CALLBACK);
        this.courseID = courseID;
    }

    @NonNull
    @Override
    public EnrollmentsListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_enrollments, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EnrollmentsListAdapter.MyViewHolder holder, int position) {
        au.edu.anu.quirkus.UserModel userModel = getItem(position);
        holder.userId = userModel.getUser().getId();
        holder.name.setText(userModel.getUser().getName());
        holder.email.setText(userModel.getUser().getEmail());
        holder.profilePic.setImageResource(userModel.getUser().getProfilePicture().getDrawable());
        holder.userModel = userModel;

        if (userModel.isStaff()) {
            holder.role.setText("Staff");
        } else {
            holder.role.setVisibility(View.GONE);
        }

        if (userModel.getUser().getId().equals(ActiveUser.getFirebaseID())) {
            holder.options.setVisibility(View.GONE);
        }

    }

    public static final DiffUtil.ItemCallback<au.edu.anu.quirkus.UserModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull au.edu.anu.quirkus.UserModel oldItem, @NonNull au.edu.anu.quirkus.UserModel newItem) {
            return oldItem.getUser().getId().equals(newItem.getUser().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull au.edu.anu.quirkus.UserModel oldItem, @NonNull au.edu.anu.quirkus.UserModel newItem) {
            User o = oldItem.getUser();
            User n = newItem.getUser();
            return o.getEmail().equals(n.getEmail())
                    && o.getName().equals(n.getName())
                    && oldItem.isStaff() == newItem.isStaff();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private EnrollmentsModel enrollmentsModel = new EnrollmentsModel();
        private String userId;
        private ImageView profilePic;
        private TextView role;
        private TextView name;
        private TextView email;
        private ImageButton options;
        private au.edu.anu.quirkus.UserModel userModel;

        public MyViewHolder(View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_icon);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            options = itemView.findViewById(R.id.options);
            role = itemView.findViewById(R.id.role);

            enrollmentsModel.setCourseID(courseID);

            options.setOnClickListener(e -> {
                showPopupMenu(e);
            });
        }

        public void showPopupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.enrollment_popup_menu, popupMenu.getMenu());


            // set the visibility of option menu items depending on enrollment access
            if (userModel.isStaff()) {
                popupMenu.getMenu().findItem(R.id.promote_user).setVisible(false);
                popupMenu.getMenu().findItem(R.id.demote_user).setVisible(true);
            }
            else {
                popupMenu.getMenu().findItem(R.id.promote_user).setVisible(true);
                popupMenu.getMenu().findItem(R.id.demote_user).setVisible(false);
            }

            popupMenu.setOnMenuItemClickListener(e -> {
                switch (e.getItemId()) {
                    case R.id.promote_user -> {
                        enrollmentsModel.promoteUserToStaff(userId, result -> {
                            if (result) {
                                Toast.makeText(itemView.getContext(), "user set to staff", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(itemView.getContext(), "failed to promote user", Toast.LENGTH_LONG).show();
                            }
                        });
                        return true;
                    }
                    case R.id.demote_user -> {
                        enrollmentsModel.demoteUserToStudent(userId, result -> {
                            if (result) {
                                Toast.makeText(itemView.getContext(), "user set to student", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(itemView.getContext(), "failed to demote user", Toast.LENGTH_LONG).show();
                            }
                        });
                        return true;
                    }
                    case R.id.remove_user -> {
                        enrollmentsModel.removeUser(userId, result -> {
                            if (result) {
                                Toast.makeText(itemView.getContext(), "user removed", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(itemView.getContext(), "failed to remove user", Toast.LENGTH_LONG).show();
                            }
                        });
                        return true;
                    }
                }
                return false;
            });
            popupMenu.show();
        }
    }
}
