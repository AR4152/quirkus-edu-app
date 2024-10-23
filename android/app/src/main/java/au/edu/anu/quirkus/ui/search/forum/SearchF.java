package au.edu.anu.quirkus.ui.search.forum;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.data.PostsRepository;
import au.edu.anu.quirkus.ui.recyclerview.EnrollmentsListAdapter;
import au.edu.anu.quirkus.ui.recyclerview.RequestsListAdapter;

public class SearchF extends AppCompatActivity {
    private EditText searchBar;
    private Button cancelBut;
    private RecyclerView recyclerView;
    private PostsRepository postsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search_forum);

//        searchBar = findViewById(R.id.search);
//        cancelBut = findViewById(R.id.cancel);
        recyclerView = findViewById(R.id.recycleview);

//        RequestsListAdapter enrolmentSearchAdapter = new RequestsListAdapter();
//        recyclerView.setAdapter(enrolmentSearchAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                searchPosts(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        cancelBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.setText("");
            }
        });
    }

//    private void searchPosts(String query) {
//        Parser parser = new Parser(query);
//        parser.parse();
//        List<UserModel> searchResult = postsRepository.getPostsInCourseSearch(courseID, parser, staff);
//        enrolmentSearchAdapter.setUsers(searchResult);
//    }
}
