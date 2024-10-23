package au.edu.anu.quirkus.ui.addpost;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputEditText;

import au.edu.anu.quirkus.PostType;
import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.data.Course;

/**
 * @author Arjun Raj (u7526852)
 * <p>
 * Activity class for Add Post page.
 * Contains setup of all components and workflow of processes
 */

public class AddPost extends AppCompatActivity {

    // Instance Variables
    AddPostModel addPostModel;
    TextInputEditText contentEditText;
    Button postButton;
    EditText titleEditText;
    CheckBox privateCheckBox;
    CheckBox anonymousCheckBox;
    FrameLayout categoryFragmentLayout;
    FrameLayout subCategoryFragmentLayout;
    String courseID;
    Course course;

    Button questionButton;
    Button announceButton;
    Button assessmentButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        initialiseFields();
        setupToolbar();

        setupSelectionListeners();
        addPostButtonListener();

        setUpPostTypeButtonListeners();
        setBottomComponents(false);

        addPostModel.getCourse().observe(this, c -> {
            addPostModel.setCourseInit(c);
            course = c;

            addPostModel.resetSelection();
            initialisePage();
       });
    }

    private void setUpPostTypeButtonListeners(){

        unselectType(questionButton);
        unselectType(announceButton);
        unselectType(assessmentButton);

        questionButton.setOnClickListener(view -> {
            selectType(questionButton);
            unselectType(announceButton);
            unselectType(assessmentButton);
        });

        announceButton.setOnClickListener(view -> {
            selectType(announceButton);
            unselectType(questionButton);
            unselectType(assessmentButton);
        });

        assessmentButton.setOnClickListener(view -> {
            selectType(assessmentButton);
            unselectType(questionButton);
            unselectType(announceButton);
        });
    }

    private void selectType(Button button){
        button.setBackgroundColor(Color.parseColor("#0D3B66"));
        button.setTextColor(Color.parseColor("#FFFFFFFF"));
        addPostModel.setPostType(PostType.valueOf(button.getText().toString().toUpperCase()));
    }

    private void unselectType(Button button){
        button.setBackgroundColor(Color.parseColor("#D3D3D3"));
        button.setTextColor(Color.parseColor("#FF000000"));
    }

    private void addPostButtonListener(){
        postButton.setOnClickListener(view -> {
            if (!addPostModel.isPostCompleted()){
                Toast.makeText(this, "Post Not Completed", Toast.LENGTH_SHORT).show();
            }
            else {
                addPostModel.createPost(this);
                finish();
            }
        });
    }

    private void initialiseFields(){

        // Model instance set up
        courseID = getIntent().getStringExtra("COURSE_ID");

        if (courseID == null)
            throw new RuntimeException("Course expected");

        addPostModel = new ViewModelProvider(this).get(AddPostModel.class);
        addPostModel.setCourseID(courseID);

        // UI Components set up
        postButton = (Button) findViewById(R.id.post_button);
        titleEditText = (EditText) findViewById(R.id.title_edit_text);
        contentEditText = (TextInputEditText) findViewById(R.id.post_edit_text);
        privateCheckBox = (CheckBox) findViewById(R.id.private_checkBox);
        anonymousCheckBox = (CheckBox) findViewById(R.id.anon_check_box);
        questionButton = (Button) findViewById(R.id.question_button);
        announceButton = (Button) findViewById(R.id.announce_button);
        assessmentButton = (Button) findViewById(R.id.assessment_button);

        // Layouts/Fragments
        categoryFragmentLayout = (FrameLayout) findViewById(R.id.category_button_layout);
        subCategoryFragmentLayout = (FrameLayout) findViewById(R.id.sub_category_button_layout);
    }

    private void setupSelectionListeners(){
        // Title Edit text
        titleEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable mEdit)
            {
                String title = mEdit.toString();
                addPostModel.setTitle(title);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        // Body
        contentEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable mEdit)
            {
                String content = mEdit.toString();
                addPostModel.setText(content);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        // Private Checkbox
        privateCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> addPostModel.setPrivate(isChecked));

        // IsAnon Checkbox
        anonymousCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> addPostModel.setAnon(isChecked));

    }

    private void setupToolbar(){

        // Tool bar setup and back button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_addpost);
        toolbar.setTitle("Add Post");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void removeFragments(){
        // sub tags
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.sub_category_button_layout);
        if (fragment != null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commit();

        // tags
        fragment = getSupportFragmentManager().findFragmentById(R.id.category_button_layout);
        if (fragment != null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commit();
    }

    private void initialisePage(){

        addPostModel.getCourse().observe(this, c->{
            addPostModel.setCourseInit(c);
        });

        // Hide components below Category
        setBottomComponents(false);

        // Remove any existing fragments
        removeFragments();

        // Add category buttons
        String categories = String.join(";", course.getTagDefinitions().keySet());

        String colours = "";
        for (var category : course.getTagDefinitions().values()) {
            colours += category.getColor() + ";";
        }

        HorizontalButtonFragment categoryFrag =
                HorizontalButtonFragment.newInstance("Tags", categories, colours);
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.category_button_layout, categoryFrag, null)
                .commit();

        // Add sub category buttons when category is selected
        addPostModel.getSelectedCategory().observe(this, item -> {

            // Perform an action with the latest item data.
            var category = course.getTagDefinitions().get(item.getText().toString());
            Toast.makeText(this, item.getText(), Toast.LENGTH_SHORT).show();

            // No sub categories
            if (category.getSubTags() == null || category.getSubTags().size() == 0){
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.sub_category_button_layout);
                if (fragment != null)
                    getSupportFragmentManager()
                            .beginTransaction()
    //                        .setReorderingAllowed(true)
                            .remove(fragment)
                            .commit();
            }
            else {
                String subCategories = String.join(";", category.getSubTags());
                String coloursSub = (category.getColor() + ";").repeat(category.getSubTags().size());

                HorizontalButtonFragment subCategoryFrag =
                        HorizontalButtonFragment.newInstance("Sub-Tags", subCategories, coloursSub);

                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.sub_category_button_layout, subCategoryFrag, null)
                        .commit();
            }

            setBottomComponents(true);
        });
    }

    private void setBottomComponents(boolean isVisible){
        int visibility;

        if (isVisible)
            visibility = View.VISIBLE;
        else
            visibility = View.GONE;

        contentEditText.setVisibility(visibility);
        privateCheckBox.setVisibility(visibility);
        anonymousCheckBox.setVisibility(visibility);
    }
}