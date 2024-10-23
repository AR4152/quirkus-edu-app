package au.edu.anu.quirkus.ui.forum;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;
import java.util.List;

import au.edu.anu.quirkus.ActiveUser;
import au.edu.anu.quirkus.AuthorPostModel;
import au.edu.anu.quirkus.AuthorPostModelFactory;
import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.UserModel;
import au.edu.anu.quirkus.UserModelFactory;
import au.edu.anu.quirkus.data.Enrollment;
import au.edu.anu.quirkus.data.EnrollmentsRepository;
import au.edu.anu.quirkus.data.Post;
import au.edu.anu.quirkus.data.PostsRepository;
import au.edu.anu.quirkus.data.User;
import au.edu.anu.quirkus.data.UserRepository;
import au.edu.anu.quirkus.postSearch.Parser;
import au.edu.anu.quirkus.ui.addpost.AddPost;
import au.edu.anu.quirkus.ui.addtags.AddTagsActivity;
import au.edu.anu.quirkus.ui.forum.options.Enrollments;
//import au.edu.anu.quirkus.ui.forum.options.Requests;
import au.edu.anu.quirkus.ui.forum.options.Requests;
import au.edu.anu.quirkus.ui.recyclerview.PostsListAdapter;
import android.content.Intent;

import au.edu.anu.quirkus.ui.thread.Thread;


public class Forum extends AppCompatActivity {
    String courseID;

    private static String TAG = "ui.forum.Forum";
    private EditText searchBar;
    private ImageButton cancelBut;
    private ForumModel forumModel;
    private PostsRepository postsRepository = new PostsRepository();
    private EnrollmentsRepository enrollmentsRepository = new EnrollmentsRepository();
    private UserRepository userRepository = new UserRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        courseID = getIntent().getStringExtra("COURSE_ID");

        if (courseID == null) {
            throw new RuntimeException("Course expected");
        }


        forumModel = new ViewModelProvider(this).get(ForumModel.class);
        forumModel.setCourseID(courseID);

        forumModel.getCourse().observe(this, c -> {
            String name = c.getName();
            if (c.isArchived()) {
                name += " (Archived)";
            }
            setTitle(name);
        });

        RecyclerView posts = findViewById(R.id.posts);
        posts.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));
        PostsListAdapter postsListAdapter = new PostsListAdapter(e -> {
            Intent intent = new Intent(Forum.this, Thread.class);
            intent.putExtra("COURSE_ID", courseID);
            intent.putExtra("POST_ID", e);
            startActivity(intent);
        }, courseID);

        posts.setAdapter(postsListAdapter);

        forumModel.getPosts().observe(this, list -> {
            postsListAdapter.submitList(list);
        });

        searchBar = findViewById(R.id.search);
        searchBar.setVisibility(View.GONE);
        cancelBut = findViewById(R.id.cancelBut);
        cancelBut.setVisibility(View.GONE);

        cancelBut.setOnClickListener(v -> {
            searchBar.setText("");
            searchBar.setVisibility(View.GONE);
            cancelBut.setVisibility(View.GONE);
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchQuery = s.toString().toLowerCase();
                forumModel.setSearchQuery(searchQuery);
            }
        });

        forumModel.getSearchQueryError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_course, menu);
        MenuItem searchPostItem = menu.findItem(R.id.search_post);
        searchPostItem.setOnMenuItemClickListener(item -> {
            searchBar.setVisibility(View.VISIBLE);
            cancelBut.setVisibility(View.VISIBLE);
            return true;
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem enrollments = menu.findItem(R.id.current_enrolments);
        MenuItem requests = menu.findItem(R.id.enrolment_requests);
        MenuItem archive = menu.findItem(R.id.archive_class);
        MenuItem post = menu.findItem(R.id.add_post);

        forumModel.getCourse().observe(this, course -> {
            post.setVisible(!course.isArchived());
        });

        forumModel.getActiveUserAccess(courseID).observe(this, isStaff -> {
            enrollments.setVisible(isStaff);
            requests.setVisible(isStaff);
            archive.setVisible(isStaff);
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) finish();
        if (id == R.id.add_post) {
            // TODO: Create intent to add new post
            Intent addPostIntent = new Intent(getApplicationContext(), AddPost.class);
            addPostIntent.putExtra("COURSE_ID", courseID);
            startActivity(addPostIntent);
            return true;
        } else if (id == R.id.current_enrolments) {
            Intent i = new Intent(this, Enrollments.class);
            i.putExtra("COURSE_ID", getIntent().getStringExtra("COURSE_ID"));
            startActivity(i);
            return true;
        } else if (id == R.id.enrolment_requests) {
            // TODO: Create intent to enrolment requests activity
            Intent i = new Intent(this, Requests.class);
            i.putExtra("COURSE_ID", getIntent().getStringExtra("COURSE_ID"));
            startActivity(i);
            return true;
        } else if (id == R.id.archive_class) {
            // TODO: Add functionality to archive course
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Archive Course");
            builder.setMessage("Are you sure you would like to archive " + getTitle() + " class?");
            builder.setPositiveButton("Archive",
                    (dialog, which) -> {
                        forumModel.archiveCourse(success -> {
                            if (success) {
                                Toast.makeText(this, "Archived the class.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Failed to archive the class, please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {});

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (id == R.id.leave_class) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Leave Course");
            builder.setMessage("Are you sure you would like to leave " + getTitle() + " class?");
            builder.setPositiveButton("Leave",
                    (dialog, which) -> {
                        forumModel.leaveCourse(success -> {
                            if (success) {
                                finish();
                            } else {
                                Toast.makeText(this, "Failed to leave class, try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (id == R.id.manage_tags) {
            Intent addPostIntent = new Intent(getApplicationContext(), AddTagsActivity.class);
            addPostIntent.putExtra("COURSE_ID", courseID);
            startActivity(addPostIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
