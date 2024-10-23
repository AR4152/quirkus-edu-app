package au.edu.anu.quirkus.ui.thread;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Date;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import au.edu.anu.quirkus.ActiveUser;
import au.edu.anu.quirkus.EnrollmentAccess;
import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.UserModelFactory;
import au.edu.anu.quirkus.data.CoursesRepository;
import au.edu.anu.quirkus.data.EnrollmentsRepository;
import au.edu.anu.quirkus.data.PostRoot;
import au.edu.anu.quirkus.data.PostsReplyRepository;
import au.edu.anu.quirkus.data.PostsRepository;
import au.edu.anu.quirkus.data.UserRepository;
import au.edu.anu.quirkus.ui.recyclerview.RepliesListAdapter;

public class Thread extends AppCompatActivity implements RepliesListAdapter.ReplyClickListener {
    private String courseID;
    private String postID;
    private String activeUserID;
    private TextView timeTextView;
    private TextView nameTextView;
    private TextView commentTextView;
    private TextView tagTextView;
    private TextView isStaff;
    private TextView upvoteCount;
    private TextView replyButton;
    private ImageButton upvote;
    private View replytextholder;
    private EditText replytext;
    private TextView replytext2;
    private Button replyButton3;
    private Button replyButton2;
    private ImageButton cancel;
    private ImageView profilePicture;
    private UserRepository userRepository = new UserRepository();

    private UserModelFactory userModelFactory = new UserModelFactory();
    private CoursesRepository coursesRepository = new CoursesRepository();
    private PostsRepository postsRepository = new PostsRepository();
    private String authorName;
    private PostsReplyRepository postsReplyRepository = new PostsReplyRepository();
    private EnrollmentsRepository enrollmentsRepository = new EnrollmentsRepository();
    private ThreadViewModel threadViewModel;
    private Toolbar toolbar;
    private Handler handler = new Handler();
    private Runnable updateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        courseID = getIntent().getStringExtra("COURSE_ID");
        postID = getIntent().getStringExtra("POST_ID");
        activeUserID = ActiveUser.getFirebaseID();

        // get all the text views
        timeTextView = findViewById(R.id.time_text_view);
        nameTextView = findViewById(R.id.name_text_view);
        commentTextView = findViewById(R.id.comment_text_view);
        profilePicture = findViewById(R.id.imageView);
        tagTextView = findViewById(R.id.tags);
        upvoteCount = findViewById(R.id.upvoteCountMain);
        isStaff = findViewById(R.id.isStaff);
        replyButton = findViewById(R.id.replyButtonMain);
        upvote = findViewById(R.id.upvoteButtonMain);

        replytextholder = findViewById(R.id.replytextholder);
        replytext = findViewById(R.id.replytext);
        replytext2 = findViewById(R.id.replyto2);
        replyButton2 = findViewById(R.id.replyButton2);
        replyButton3 = findViewById(R.id.replyButton3);
        cancel = findViewById(R.id.cancel);

        threadViewModel = new ViewModelProvider(this).get(ThreadViewModel.class);
        threadViewModel.setPostID(courseID, postID);

        if (courseID == null || postID == null) {
            throw new RuntimeException("Course and Post IDs expected");
        }

        // set the post reply button
        replyButton.setOnClickListener(v -> {
            showReplyMenu();
            showMainReplyButton();
            setVisibilityAnimated(cancel, View.VISIBLE);
        });
        // set the cancel button
        cancel.setOnClickListener(v -> {
            hideReplyMenu();
            hideMainReplyButton();
            hideSubReplyButton();
            hideKeyboard(v);
        });

        // sub buttons
        replyButton2.setOnClickListener(v2 -> {
            sendReply(replytext.getText().toString());
            hideReplyMenu();
            hideMainReplyButton();
            hideKeyboard(v2);
        });

        threadViewModel.getPost().observe(this, post -> {
            upvote.setOnClickListener(v -> {
                if (post.getUpvoters().contains(activeUserID)) {
                    // The post has already been upvoted by the user, so remove the upvote.
                    postsRepository.removeUpvoteFromPost(courseID, postID, activeUserID, success -> {
                        if (!success) {
                            // The upvote was not removed successfully.
                            Toast.makeText(this, "Failed to remove upvote", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // The post has not been upvoted by the user, so add the upvote.
                    postsRepository.addUpvoteToPost(courseID, postID, activeUserID, success -> {
                        if (!success) {
                            // The upvote was not added successfully.
                            Toast.makeText(this, "Failed to add upvote", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
        // Observe post and set data to views
        threadViewModel.getPost().observe(this, post -> {
            PostRoot postRoot = (PostRoot) post;
            Date dateCreated = post.getCreated();
            timeTextView.setText(getTimeAgo(dateCreated.toInstant().toString()));
            commentTextView.setText(post.getBody());
            tagTextView.setText(postRoot.getPrintableTag());

            coursesRepository.getCourse(courseID).observe(this, course -> {
                if (course != null) {
                    if (course.getTagDefinitions().containsKey(postRoot.getTag())) {
                        String colour = course.getTagDefinitions().get(postRoot.getTag()).getColor();
                        // Parse the color string to an integer
                        int colorInt = Color.parseColor(colour);
                        // Set the color of the text
                        tagTextView.setTextColor(colorInt);
                    }
                }
            });
            upvoteCount.setText(Long.toString(post.getUpvoteCount())); // get the upvote counts from backend
            // check if the user is a staff
            enrollmentsRepository.getStaffForCourse(courseID).observe(this, staff -> {
                boolean isStaffBool = staff.stream().anyMatch(enrollment -> enrollment.getUserId().equals(activeUserID) && enrollment.getAccess() == EnrollmentAccess.STAFF);
                if (isStaffBool) {
                    isStaff.setVisibility(View.VISIBLE);
                }
                else {
                    isStaff.setVisibility(View.GONE);
                }
            });
            // Observe user and set user's name to the nameTextView
            if (post.getAuthorId() == null) {
                nameTextView.setText("Anonymous");
            }
            else {
                userRepository.getUserByID(post.getAuthorId()).observe(this, user -> {
                    if (user != null) {
                        nameTextView.setText(user.getName());
                        profilePicture.setImageResource(user.getProfilePicture().getDrawable());
                        authorName = user.getName();
                    }
                });
            }
        });

        // Set up RecyclerView for replies
        RecyclerView repliesRecyclerView = findViewById(R.id.recyclerView);
        repliesRecyclerView.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));
        RepliesListAdapter repliesListAdapter = new RepliesListAdapter(this, this, courseID, postID, this);
        repliesRecyclerView.setAdapter(repliesListAdapter);

        // Observe replies and update the list
        threadViewModel.getReplies().observe(this, list -> {
            repliesListAdapter.submitList(list);
        });

        setupToolBar(courseID, postID);
        refresh();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTime); // Stop Handler when the activity is not visible to stop the runnable from executing
    }

    private void sendReply(String reply) {
        postsReplyRepository.createNewReply(courseID, postID, ActiveUser.getFirebaseID(), postID, reply, success -> {
            if (success) {
                // The reply was created successfully.
                Toast.makeText(this, "Your reply has been posted!", Toast.LENGTH_SHORT).show();
            } else {
                // The reply was not created successfully.
                Toast.makeText(this, "Failed to create reply", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void sendReply2(String reply, String replyid) {
        postsReplyRepository.createNewReply(courseID, postID, ActiveUser.getFirebaseID(), replyid, reply, success -> {
            if (success) {
                // The reply was created successfully.
                Toast.makeText(this, "Reply created successfully", Toast.LENGTH_LONG).show();
            } else {
                // The reply was not created successfully.
                Toast.makeText(this, "Failed to create reply", Toast.LENGTH_LONG).show();
            }
        });
    }

    // thanks Arjun!! :))
    // np!!
    private void setupToolBar(String courseID, String postID){
        ThreadViewModel threadViewModel = new ViewModelProvider(this).get(ThreadViewModel.class);
        threadViewModel.setPostID(courseID, postID);

        threadViewModel.getPost().observe(this, post -> {
            toolbar = (Toolbar) findViewById(R.id.toolbar_threads);
            toolbar.setTitle(((PostRoot) post).getTitle());
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        });
    }
    private void refresh() {
        updateTime = () -> {
            threadViewModel.getPost().observe(Thread.this, post -> {
                Date dateCreated = post.getCreated();
                timeTextView.setText(getTimeAgo(dateCreated.toInstant().toString()));
            });
            // Repeat this runnable code block again every minute
            handler.postDelayed(updateTime, 60000);
        };
        handler.post(updateTime);
    }

    public static String getTimeAgo(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime dateTime = LocalDateTime.parse(timestamp, formatter);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        Duration duration = Duration.between(dateTime, now);
        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;

        if (seconds <= 0) {
            return "0 seconds ago";
        }
        else if (seconds < 60) {
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

    public void hideReplyMenu() {
        setVisibilityAnimated(replytextholder, View.GONE);
        setVisibilityAnimated(replytext, View.GONE);
        replytext.getText().clear();
        setVisibilityAnimated(replytext2, View.GONE);
        setVisibilityAnimated(replyButton2, View.GONE);
        setVisibilityAnimated(cancel, View.GONE);

    }

    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void showReplyMenu() {
        setVisibilityAnimated(replytextholder, View.VISIBLE);
        setVisibilityAnimated(replytext, View.VISIBLE);
        setVisibilityAnimated(replytext2, View.VISIBLE);
        setVisibilityAnimated(cancel, View.VISIBLE);
        replytext2.setText("Replying to " + authorName);
    }

    public void showMainReplyButton() {
        setVisibilityAnimated(replyButton2, View.VISIBLE);
    }

    public void showSubReplyButton() {
        setVisibilityAnimated(replyButton3, View.VISIBLE);
    }

    public void hideMainReplyButton() {
        setVisibilityAnimated(replyButton2, View.GONE);
    }

    public void hideSubReplyButton() {
        setVisibilityAnimated(replyButton3, View.GONE);
    }
    @Override
    public void onReplyClick(String replyId) {
        // Here, you can change the behavior of replyButton3 to reply to the specific reply
        replyButton3.setOnClickListener(v -> {
            sendReply2(replytext.getText().toString(), replyId);
            hideReplyMenu();
            hideSubReplyButton();
            hideKeyboard(v);
        });
    }
    private void setVisibilityAnimated(View v, final int visibility) {
        if (visibility == View.VISIBLE) {
            AnimationSet animationSet = new AnimationSet(true);
            AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(500);
            animationSet.addAnimation(animation);

            v.startAnimation(animationSet);
            v.setVisibility(View.VISIBLE);
        }
        else if (visibility == View.GONE) {
            AnimationSet animationSet = new AnimationSet(true);
            AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
            animation.setDuration(250);
            animationSet.addAnimation(animation);

            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            v.startAnimation(animationSet);
        }
    }
}