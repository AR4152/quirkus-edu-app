package au.edu.anu.quirkus.ui.recyclerview;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.time.Instant;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.edu.anu.quirkus.ActiveUser;
import au.edu.anu.quirkus.EnrollmentAccess;
import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.UserModel;
import au.edu.anu.quirkus.UserModelFactory;
import au.edu.anu.quirkus.data.EnrollmentsRepository;
import au.edu.anu.quirkus.data.Post;
import au.edu.anu.quirkus.data.PostReply;
import au.edu.anu.quirkus.data.PostsReplyRepository;
import au.edu.anu.quirkus.data.PostsRepository;
import au.edu.anu.quirkus.data.UserRepository;
import au.edu.anu.quirkus.ui.thread.Thread;

public class RepliesListAdapter extends ListAdapter<Post, RepliesListAdapter.MyViewHolder> {
    public interface ReplyClickListener {
        void onReplyClick(String replyId);
    }
    private ReplyClickListener replyClickListener;

    private final Context context;
    private Thread thread;
    private String postID;
    private String courseID;
    private PostsReplyRepository postsReplyRepository;
    public RepliesListAdapter(Context context, Thread thread, String courseID, String postID, ReplyClickListener replyClickListener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.courseID = courseID;
        this.postID = postID;
        this.thread = thread;
        this.replyClickListener = replyClickListener;

        postsReplyRepository = new PostsReplyRepository();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_thread_reply, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // Add the read more text for long chunks of text
        holder.body.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_full_layout);
        });
        // set reply listener
        holder.replyButton.setOnClickListener(v -> {
            thread.showReplyMenu();
            thread.showSubReplyButton();
            if (replyClickListener != null) {
                replyClickListener.onReplyClick(getItem(position).getId()); // replyId is the ID of the reply this ViewHolder is showing
            }
        });

        holder.update(getItem(position));
    }

    public static final DiffUtil.ItemCallback<Post> DIFF_CALLBACK = new DiffUtil.ItemCallback<Post>() {
        @Override
        public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            PostReply o = (PostReply) oldItem;
            PostReply n = (PostReply) newItem;
            return o.getBody().equals(newItem.getBody()) &&
                    o.getAuthorId().equals(newItem.getAuthorId()) &&
                    (o.getReplyToId() == null || o.getReplyToId().equals(n.getReplyToId()))
                    && o.getUpvoteCount() == n.getUpvoteCount();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView body;
        private TextView userText;
        private TextView created;
        private ImageView profile;
        private TextView isStaff;
        private TextView upVoteCount;
        private TextView originalName;
        private TextView originalComment;
        private ImageButton upvote;
        private Button replyButton;
        private LinearLayout originalReplyLayout;
        private EnrollmentsRepository enrollmentsRepository = new EnrollmentsRepository();
        private final UserRepository userRepository = new UserRepository();
        private Handler handler = new Handler();
        private Runnable updateTime;
        private PostReply currentReply;
        public MyViewHolder(View itemView) {
            super(itemView); // declare all the elements in threads
            body = itemView.findViewById(R.id.comment_text_view2);
            userText = itemView.findViewById(R.id.name_text_view2);
            created = itemView.findViewById(R.id.time_text_view2);
            profile = itemView.findViewById(R.id.profilepic);
            isStaff = itemView.findViewById(R.id.isStaff2);
            originalName = itemView.findViewById(R.id.originalName);
            originalComment = itemView.findViewById(R.id.originalComment);
            originalReplyLayout = itemView.findViewById(R.id.line_reply);
            replyButton = itemView.findViewById(R.id.replyButtonMain2);
            upVoteCount = itemView.findViewById(R.id.upvoteCountMain2);
            upvote = itemView.findViewById(R.id.upvoteButtonMain2);
        }

        public void update(Post reply) {
            currentReply = (PostReply) reply;

            enrollmentsRepository.getStaffForCourse(courseID).observe((LifecycleOwner) itemView.getContext(), staff -> {
                boolean isStaffBool = staff.stream().anyMatch(enrollment -> enrollment.getUserId().equals(reply.getAuthorId()) && enrollment.getAccess() == EnrollmentAccess.STAFF);
                if (isStaffBool) {
                    isStaff.setVisibility(View.VISIBLE);
                }
                else {
                    isStaff.setVisibility(View.GONE);
                }
            });
            upVoteCount.setText(Long.toString(reply.getUpvoteCount())); // set upvote count element
            body.setText(reply.getBody()); // set body element
            userRepository.getUserByID(reply.getAuthorId()).observe((LifecycleOwner) itemView.getContext(), user -> {
                if (user != null) {
                    userText.setText(user.getName()); // set Username element
                    profile.setImageResource(user.getProfilePicture().getDrawable()); // set Profile picture element
                }
            });

            // Fetching the original reply and setting the author name and original comment (ONLY FOR REPLIES TO REPLIES)
            if (((PostReply) reply).getReplyToId() != null) {
                postsReplyRepository.getReplyByID(courseID, postID, currentReply.getReplyToId()).observe((LifecycleOwner) itemView.getContext(), originalReply -> {
                    if ((originalReply.getAuthorId() != null && originalReply.getBody() != null)) {
                        // set the original author name
                        userRepository.getUserByID(originalReply.getAuthorId()).observe((LifecycleOwner) itemView.getContext(), originalUser -> {
                            if (originalUser != null) {
                                originalName.setText(originalUser.getName()); // set replied author name (original post being replied to author name)
                            }
                        });
                        if (originalReply.getBody() != null) { // set replied author body (snapshot of original post being replied to body)
                            String[] words = originalReply.getBody().split("\\s+");
                            StringBuilder firstTenWords = new StringBuilder();
                            for (int i = 0; i < 10 && i < words.length; i++) {
                                firstTenWords.append(words[i]).append(" ");
                            }
                            // set the original comment
                            originalComment.setText(firstTenWords.toString().trim());
                            originalReplyLayout.setVisibility(View.VISIBLE);
                        }

                    }
                });

            }
            upvote.setOnClickListener(v -> { // Add or decrease upvote count functionality
                String userId = ActiveUser.getFirebaseID();
                if (currentReply.getUpvoters().contains(userId)) {
                    // The user has already upvoted this reply, so remove their upvote
                    postsReplyRepository.removeUpvoteFromReply(courseID, postID, currentReply.getId(), userId, success -> {
                        if (!success) {
                            Toast.makeText(context, "Failed to remove upvote", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // The user has not upvoted this reply yet, so add their upvote
                    postsReplyRepository.addUpvoteToReply(courseID, postID, currentReply.getId(), userId, success -> {
                        if (!success) {
                            Toast.makeText(context, "Failed to add upvote", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            refresh();
        }
        public void cleanUp() { // for updating time
            handler.removeCallbacks(updateTime); // Stop Handler when the item is recycled
        }

        private void refresh() {
            updateTime = () -> { // update date posted element
                created.setText(Thread.getTimeAgo(currentReply.getCreated().toInstant().toString()));
                // Repeat this runnable code block again every minute (update the time last posted)
                handler.postDelayed(updateTime, 60000);
            };
            handler.post(updateTime);
        }
    }
    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cleanUp(); // Clean up the handler when the ViewHolder is recycled
    }
}
