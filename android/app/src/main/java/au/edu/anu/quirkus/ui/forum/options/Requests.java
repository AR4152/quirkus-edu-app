package au.edu.anu.quirkus.ui.forum.options;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.ui.recyclerview.RequestsListAdapter;

public class Requests extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        setTitle("Requests");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        String courseID = getIntent().getStringExtra("COURSE_ID");

        RecyclerView requests = findViewById(R.id.enrolment_requests);

        RequestModel userRequests = new ViewModelProvider(this).get(RequestModel.class);
        userRequests.setCourseId(courseID);

        requests.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));
        RequestsListAdapter userList = new RequestsListAdapter(courseID);
        requests.setAdapter(userList);

        userRequests.getRequests().observe(this, list -> {
            userList.submitList(list);
        });

//        ImageButton searchButton = findViewById(R.id.imageButton);
//        searchButton.setOnClickListener(v -> {
//            Intent intent = new Intent(Enrollments.this, SearchEnrolments.class);
//            startActivity(intent);
//        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
