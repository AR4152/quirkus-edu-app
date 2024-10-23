package au.edu.anu.quirkus.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

import au.edu.anu.quirkus.ui.search.course.SearchC;
import au.edu.anu.quirkus.ui.settings.SettingsActivity;


import au.edu.anu.quirkus.ActiveUser;
import au.edu.anu.quirkus.data.Course;
import au.edu.anu.quirkus.ui.recyclerview.CoursesListAdapter;
import au.edu.anu.quirkus.ui.forum.Forum;
import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.ui.loginSignup.LoginActivity;

public class Home extends AppCompatActivity {
    String[] classesVisible = {"Current Classes", "Archived Classes"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        RecyclerView courses = findViewById(R.id.courses);
        Button btn = findViewById(R.id.signout);
        ImageButton settingsBtn = findViewById(R.id.settings);
        TextView defaultClasses = findViewById(R.id.loading_default);

        if (!ActiveUser.isLoggedIn()) {
            launchMain();
        }

        courses.setHasFixedSize(true);

        HomeModel enrolments = new ViewModelProvider(this).get(HomeModel.class);

        courses.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));
        CoursesListAdapter c = new CoursesListAdapter(e -> launchCourse(e));
        courses.setAdapter(c);

        final Boolean[] viewingArchived = {false};

        AutoCompleteTextView dropDownMenu = findViewById(R.id.dropdown_items);
        ArrayAdapter<String> dropDownAdapter = new ArrayAdapter<String>(this, R.layout.view_courses_dropdown, classesVisible);

        dropDownMenu.setAdapter(dropDownAdapter);
        dropDownMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                defaultClasses.setText("");
                String item = adapterView.getItemAtPosition(i).toString();
                viewingArchived[0] = item.equals("Archived Classes");
                loadCourses(viewingArchived[0], enrolments, c);
            }
        });

        loadCourses(viewingArchived[0], enrolments, c);

        settingsBtn.setOnClickListener(e -> {
            launchSettings();
        });

        btn.setOnClickListener(e -> {
            ActiveUser.logout();
            launchMain();
        });

        // Add course listener
        findViewById(R.id.add_course).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SearchC.class);
                startActivity(intent);
            }
        });
    }

    protected void launchMain() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    protected void loadCourses(Boolean viewArchive, HomeModel enrolments, CoursesListAdapter c) {
        enrolments.getEnrolledClasses(ActiveUser.getFirebaseID()).observe(this, list -> {
            List<Course> viewingList = new ArrayList<>();

            if (viewArchive) {
                for (Course course : list) {
                    if (course.isArchived()) {
                        viewingList.add(course);
                    }
                }
            }
            else {
                for (Course course : list) {
                    if (!course.isArchived()) {
                        viewingList.add(course);
                    }
                }
            }
            c.submitList(viewingList);
            Log.d("Home", "Got new data");
            for (Course course : viewingList) {
                Log.d("Course", course.getDescription());
            }
        });
    }
    protected void launchCourse(String courseID) {
        Intent i = new Intent(this, Forum.class);
        i.putExtra("COURSE_ID", courseID);
        startActivity(i);
    }

    protected  void launchSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
    @Override
    protected void onStop() {
        Log.d("Stopping", "Stop");
        super.onStop();
    }

}