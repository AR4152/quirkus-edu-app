package au.edu.anu.quirkus.ui.recyclerview;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import au.edu.anu.quirkus.AuthorPostModel;
import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.data.CoursesRepository;
import au.edu.anu.quirkus.data.Post;
import au.edu.anu.quirkus.data.PostReply;
import au.edu.anu.quirkus.data.PostRoot;

public class PostsListAdapter extends ListAdapter<AuthorPostModel, PostsListAdapter.MyViewHolder> {
    private Callback callback;
    private String courseID;
    private ArrayList<String> headings = new ArrayList<>();
    private List<AuthorPostModel> originalList;

    public PostsListAdapter(Callback callback, String courseID) {
        super(DIFF_CALLBACK);
        this.callback = callback;
        this.courseID = courseID;
        this.originalList = new ArrayList<>();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item and return a new ViewHolder object
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_post, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AuthorPostModel currentPost = getItem(position);
        holder.update(currentPost);
        Date currentPostDate = currentPost.getPost().getCreated();
        // Always hide the header initially
        holder.headerBackground.setVisibility(View.GONE);
        holder.headerText.setVisibility(View.GONE);
        // If this is the first item or the date has changed compared to the previous item, show the header
        if (position == 0 || !holder.isSameDateRange(currentPostDate, getItem(position - 1).getPost().getCreated())) {
            String dateRange = holder.getDateRangeText(currentPostDate);
            // Ignore "Today" for displaying header
            if (!dateRange.equals("Today")) {
                holder.showHeaderForDateRange(currentPostDate);
            }
        }
    }


    public static final DiffUtil.ItemCallback<AuthorPostModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<AuthorPostModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull AuthorPostModel oldItem, @NonNull AuthorPostModel newItem) {
            return oldItem.getPost().getId().equals(newItem.getPost().getId()) && oldItem.getPost().getClass().equals(newItem.getPost().getClass());
        }

        @Override
        public boolean areContentsTheSame(@NonNull AuthorPostModel oldItem, @NonNull AuthorPostModel newItem) {
            if (oldItem.getPost() instanceof PostRoot) {
                PostRoot o = (PostRoot) oldItem.getPost();
                PostRoot n = (PostRoot) newItem.getPost();
                return o.getTitle().equals(n.getTitle())
                        && o.getBody().equals(n.getBody())
                        && Objects.equals(o.getTag(), n.getTag())
                        && Objects.equals(o.getSubTag(), n.getSubTag())
                        && oldItem.getAuthor().getUser().getName().equals(newItem.getAuthor().getUser().getName())
                        && oldItem.getAuthor().isStaff() == newItem.getAuthor().isStaff();
            } else {
                PostReply o = (PostReply) oldItem.getPost();
                PostReply n = (PostReply) newItem.getPost();
                return o.getBody().equals(n.getBody())
                        && oldItem.getAuthor().getUser().getName().equals(newItem.getAuthor().getUser().getName())
                        && oldItem.getAuthor().isStaff() == newItem.getAuthor().isStaff();

            }
        }
    };

    public interface Callback {
        void onPostClick(String postID);
    }

    // This class defines the ViewHolder object for each item in the RecyclerView
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private String postID;
        private TextView title;
        private TextView body;
        private TextView tag;
        private TextView author;
        private TextView staff;
        private TextView date;
        private TextView upVoteCount;
        public View headerBackground;
        public TextView headerText;
        private CoursesRepository coursesRepository = new CoursesRepository();


        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            tag = itemView.findViewById(R.id.tag);
            author = itemView.findViewById(R.id.author);
            staff = itemView.findViewById(R.id.staff);
            headerBackground = itemView.findViewById(R.id.heading_bg);
            headerText = itemView.findViewById(R.id.heading_text);
            date = itemView.findViewById(R.id.date);
            upVoteCount = itemView.findViewById(R.id.upvotecount);


            itemView.setOnClickListener(e -> {
                callback.onPostClick(postID); // Please add some way to get the course ID @Daniel
            });
        }

        public void update(AuthorPostModel post) {
            Post p = post.getPost();
            postID = p.getId();
            body.setText(p.getBody());

            if (p instanceof PostRoot) {
                PostRoot pr = (PostRoot) p;
                title.setText(pr.getTitle());
                tag.setText(pr.getPrintableTag());

                coursesRepository.getCourse(courseID).observe((LifecycleOwner) itemView.getContext(), course -> {
                    if (course != null) {
                        if (course.getTagDefinitions().containsKey(pr.getTag())) {
                            String colour = course.getTagDefinitions().get(pr.getTag()).getColor();
                            // Parse the color string to an integer
                            int colorInt = Color.parseColor(colour);
                            // Set the color of the text
                            tag.setTextColor(colorInt);
                        }
                    }
                });

            } else {
                PostReply pr = (PostReply) p;
                title.setText("<reply>");
                tag.setText("");
            }

            author.setText(post.getAuthor().getUser().getName());
            updateTimeAgo(post.getPost().getCreated());
            upVoteCount.setText(String.valueOf(post.getPost().getUpvoteCount()));

            if (post.getAuthor().isStaff()) {
                staff.setVisibility(View.VISIBLE);
            }
        }

        public void showHeaderForDateRange(Date date) {
            String dateRange = getDateRangeText(date);
            headerText.setText(dateRange);
            headerBackground.setVisibility(View.VISIBLE);
            headerText.setVisibility(View.VISIBLE);
        }

        public boolean isSameDateRange(Date date1, Date date2) {
            String range1 = getDateRangeText(date1);
            String range2 = getDateRangeText(date2);

            return range1.equals(range2);
        }


        public String getDateRangeText(Date date) {
            Calendar today = Calendar.getInstance();
            Calendar postDate = Calendar.getInstance();
            postDate.setTime(date);

            if (today.get(Calendar.YEAR) == postDate.get(Calendar.YEAR)) {
                if (today.get(Calendar.DAY_OF_YEAR) == postDate.get(Calendar.DAY_OF_YEAR)) {
                    return "Today";
                }
                if (today.get(Calendar.DAY_OF_YEAR) - postDate.get(Calendar.DAY_OF_YEAR) == 1) {
                    return "Yesterday";
                }
                else if (today.get(Calendar.WEEK_OF_YEAR) == postDate.get(Calendar.WEEK_OF_YEAR)) {
                    return "This Week";
                }
                else if ((today.get(Calendar.WEEK_OF_YEAR) - postDate.get(Calendar.WEEK_OF_YEAR)) == 1) {
                    return "Last Week";
                }
                else if (today.get(Calendar.MONTH) == postDate.get(Calendar.MONTH)) {
                    return "This Month";
                }
            }
            return "Older";
        }

        public String getTimeAgo(Date past) {
            Date now = new Date();

            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
            long weeks = days / 7;

            if (seconds <= 0) {
                return "0 seconds ago";
            } else if (seconds < 60) {
                return seconds + " seconds ago";
            } else if (minutes < 60) {
                return minutes + " minutes ago";
            } else if (hours < 24) {
                return hours + " hours ago";
            } else if (days < 7) {
                return days + " days ago";
            } else {
                return weeks + " weeks ago";
            }
        }
        public void updateTimeAgo(Date past) {
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable updateTime = new Runnable() {
                @Override
                public void run() {
                    date.setText(getTimeAgo(past));
                    handler.postDelayed(this, 60000); // 1 minute
                }
            };
            handler.post(updateTime); // initial call
        }
    }
}