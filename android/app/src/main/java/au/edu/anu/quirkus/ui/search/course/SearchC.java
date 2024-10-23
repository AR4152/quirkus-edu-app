package au.edu.anu.quirkus.ui.search.course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.data.Course;
import au.edu.anu.quirkus.data.CoursesRepository;
import au.edu.anu.quirkus.ui.recyclerview.CoursesListAdapter;

/**
 * @author Arjun Raj (u7526852)
 * <p>
 * Activity class for Searching Courses and requestion for enrolment.
 * Contains setup of all UI components and workflow of processes
 */
public class SearchC extends AppCompatActivity {

    private EditText searchBar;
    private RecyclerView recyclerView;
    private CoursesListAdapter coursesListAdapter;
    private ImageButton cancelBut;
    private CoursesRepository coursesRepository;
    private List<Course> allCourseList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_course);

        initialiseFields();
        setUpToolbar();

        coursesRepository.getAllUnarchivedCourses().observe(this, courses -> {
            allCourseList = courses;
            setUpListeners();
            updateUI(allCourseList);
        });
    }

    private void initialiseFields(){
        searchBar = findViewById(R.id.search);
        recyclerView = findViewById(R.id.recycleview);
        cancelBut = findViewById(R.id.cancelBut);
        toolbar = findViewById(R.id.toolbar4);

        coursesRepository = new CoursesRepository();

        coursesListAdapter = new CoursesListAdapter(result ->{}, 1);
        recyclerView.setAdapter(coursesListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUpToolbar(){
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }


    private void updateUI(List<Course> list){
        coursesListAdapter.submitList(list);
    }

    private List<Course> search(String searchText){
        ArrayList<Course> toReturn = new ArrayList<>();

        for (var course : allCourseList)
            if (course.getName().toLowerCase().startsWith(searchText.toLowerCase()))
                toReturn.add(course);

        return toReturn;
    }

    private void setUpListeners(){
        // Search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<Course> searchResult = search(charSequence.toString());
                updateUI(searchResult);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Cancel Button
        cancelBut.setOnClickListener(v -> searchBar.setText(""));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}