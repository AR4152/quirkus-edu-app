package au.edu.anu.quirkus.ui.forum.options;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.ui.recyclerview.EnrollmentsListAdapter;

public class Enrollments extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        setTitle("Enrollments");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        String courseID = getIntent().getStringExtra("COURSE_ID");

        RecyclerView enrollments = findViewById(R.id.enrollments);

        EnrollmentsModel usersEnrolled = new ViewModelProvider(this).get(EnrollmentsModel.class);
        usersEnrolled.setCourseID(courseID);

        enrollments.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));
        EnrollmentsListAdapter userList = new EnrollmentsListAdapter(courseID);
        enrollments.setAdapter(userList);

        usersEnrolled.getEnrollments().observe(this, list -> {
            userList.submitList(list);
        });

        ImageButton searchButton = findViewById(R.id.imageButton3);
        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(Enrollments.this, SearchEnrolments.class);
            intent.putExtra("COURSE_ID", courseID);
            startActivity(intent);
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}