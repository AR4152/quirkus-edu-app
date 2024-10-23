package au.edu.anu.quirkus.ui.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import au.edu.anu.quirkus.ActiveUser;
import au.edu.anu.quirkus.data.Course;
import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.data.CoursesRepository;
import au.edu.anu.quirkus.data.EnrollmentsRepository;

public class CoursesListAdapter extends ListAdapter<Course, CoursesListAdapter.MyViewHolder> {
    private Callback callback;
    private int num = 0;

    public CoursesListAdapter(Callback callback) {
        super(DIFF_CALLBACK);
        this.callback = callback;
    }

    public CoursesListAdapter(Callback callback, int num) {
        super(DIFF_CALLBACK);
        this.callback = callback;

        this.num = num;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item and return a new ViewHolder object
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_course, parent, false);
        return new MyViewHolder(itemView);
    }

    // This method binds the data to the ViewHolder object
    // for each item in the RecyclerView
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Course course = getItem(position);
        holder.courseID = course.getId();
        holder.name.setText(course.getName());
        holder.desc.setText(course.getDescription());

        if (num == 1){
            holder.itemView.findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EnrollmentsRepository repository = new EnrollmentsRepository();
                    repository.setEnrollmentAccess(
                            course.getId(),
                            ActiveUser.getFirebaseID(),
                            "PENDING",
                            result -> {
                                if (result)
                                    Toast.makeText(view.getContext(), "Request Successful", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(view.getContext(), "Request Unsuccessful", Toast.LENGTH_SHORT).show();
                            }
                            );
                }
            });
        }
        else {
            holder.itemView.findViewById(R.id.button3).setVisibility(View.GONE);
        }
    }

    public static final DiffUtil.ItemCallback<Course> DIFF_CALLBACK = new DiffUtil.ItemCallback<Course>() {
        @Override
        public boolean areItemsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getName().equals(newItem.getName()) && oldItem.getDescription().equals(newItem.getDescription());
        }
    };

    public interface Callback {
        void callback(String courseID);
    }

    // This class defines the ViewHolder object for each item in the RecyclerView
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private String courseID;
        private TextView name;
        private TextView desc;
        private Button go;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.body);
            itemView.setOnClickListener(e -> {
                callback.callback(courseID);
            });
        }
    }
}