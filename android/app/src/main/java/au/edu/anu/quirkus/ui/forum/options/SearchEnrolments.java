package au.edu.anu.quirkus.ui.forum.options;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.data.User;
import au.edu.anu.quirkus.searchTree.EnrolmentSearchTree;
import au.edu.anu.quirkus.ui.recyclerview.EnrollmentsListAdapter;

public class SearchEnrolments extends AppCompatActivity {

    private EditText searchBar;
    private RecyclerView recyclerView;
    private String courseID;
    private EnrollmentsModel usersEnrolled;
    private ImageButton cancelBut;
    private EnrolmentSearchTree searchTree;
    private List<User> userList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_enrolments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        setTitle("");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        searchBar = findViewById(R.id.search);
        recyclerView = findViewById(R.id.recycleview); // Assuming you have a RecyclerView with this id in your layout
        searchTree = new EnrolmentSearchTree();
        EnrollmentsListAdapter enrolmentSearchAdapter = new EnrollmentsListAdapter(courseID);
        recyclerView.setAdapter(enrolmentSearchAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cancelBut = findViewById(R.id.cancelBut);

        courseID = getIntent().getStringExtra("COURSE_ID");
        if (courseID == null)
            throw new RuntimeException("Course ID expected");

        usersEnrolled = new ViewModelProvider(this).get(EnrollmentsModel.class);
        usersEnrolled.setCourseID(courseID);

        usersEnrolled.getEnrollments().observe(this, userModels -> {
            enrolmentSearchAdapter.submitList(userModels);
        });

        cancelBut.setOnClickListener(v -> searchBar.setText(""));
        setUpSearchListener();
    }
    private void setUpSearchListener(){
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usersEnrolled.setSearchQuery(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}