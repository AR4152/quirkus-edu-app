package au.edu.anu.quirkus.ui.addtags;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.data.TagDefinition;
import au.edu.anu.quirkus.data.Course;
import au.edu.anu.quirkus.ui.addpost.AddPostModel;

/**
 * @author Arjun Raj (u7526852)
 * <p>
 * Activity class for Add Tags page.
 * Contains setup of all UI components and workflow of processes
 */
public class AddTagsActivity extends AppCompatActivity {
    String courseID;
    Course course;
    Button doneButton;
    RadioGroup categoryGroup;
    RadioGroup subCategoryGroup;
    AddTagsModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tags);

        initialiseFields();
        setUpButtonListeners();
        setUpRadioGroup();

        model.getCourse().observe(this, course1 -> {
            model.currentCategories = course1.getTagDefinitions();
            model.setCourseInit(course1);
            course = course1;

            setupToolbar();
            initialisePage();
        });
    }

    private void initialisePage(){
        // Model instance set up
        updateCategoryButtons(model.currentCategories);

        // Hide Components
        setSubCategoryVisibility(false);

        model.getSelectedCategory().observe(this, item -> {
            // Show Sub category
            setSubCategoryVisibility(true);

            var selectedTag = model.currentCategories.get(item.getText());
            if (selectedTag.getSubTags() != null)
                updateSubCategoryButtons(selectedTag.getSubTags(), selectedTag.getColor());
        });
    }


    private void updateCategoryButtons(Map<String, TagDefinition> currentCategories) {

        categoryGroup.removeAllViews();
        for (Map.Entry<String, TagDefinition> entry : currentCategories.entrySet()) {
            String categoryName = entry.getKey();
            String colour = entry.getValue().getColor();

            // Adding Category Button
            RadioButton toAdd = new RadioButton(getApplicationContext());
            toAdd.setText(categoryName);
            styleButton(toAdd, colour);
            categoryGroup.addView(toAdd);
        }
    }

    private void addCategory(String name, String colour) {
        // Adding Category Button
        RadioButton toAdd = new RadioButton(getApplicationContext());
        toAdd.setText(name);
        styleButton(toAdd, colour);

        categoryGroup.addView(toAdd);
        model.updateCategory(name, colour);
    }

    private void updateSubCategoryButtons(Set<String> names, String colour) {
        subCategoryGroup.removeAllViews();
        for (var name : names) {
            // Adding Sub Category Button
            RadioButton toAdd = new RadioButton(getApplicationContext());
            toAdd.setText(name);
            styleButton(toAdd, colour);
            subCategoryGroup.addView(toAdd);
        }
    }

    private void addSubCategory(String name) {
        boolean exists = model.updateSubCategory(name);
        if (!exists) {
            // Adding Category Button
            RadioButton toAdd = new RadioButton(getApplicationContext());
            toAdd.setText(name);
            styleButton(toAdd, model.colour);

            subCategoryGroup.addView(toAdd);
        }
    }


    private void setUpRadioGroup(){
        categoryGroup.setOrientation(LinearLayout.HORIZONTAL);
        categoryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            model.selectCategory(selectedRadioButton);
          });

        subCategoryGroup.setOrientation(LinearLayout.HORIZONTAL);
        subCategoryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            model.selectSubCategory(selectedRadioButton);
        });
    }


    private void styleButton(RadioButton button, String colour){
        // Styling
        button.setButtonDrawable(null); // Remove circle
        button.setPadding(40, 10, 40, 10); // Text Padding
        button.setAlpha(0.6f); // Opacity

        // Set radius and colour to buttons
        Drawable backgroundDrawable = new GradientDrawable();
        ((GradientDrawable) backgroundDrawable).setColor(Color.parseColor(colour));
        ((GradientDrawable) backgroundDrawable).setCornerRadius(50);

        button.setBackground(backgroundDrawable);

        // Margin between buttons
        ViewGroup.MarginLayoutParams marginLayoutParams =
                new ViewGroup.MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        marginLayoutParams.setMargins(0, 0, 40, 0);
        button.setLayoutParams(marginLayoutParams);

        // Listener to change appearance of button when clicked
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // Clicked
                if (b){
                    compoundButton.setTranslationZ(30);
                    compoundButton.setElevation(20);
                    compoundButton.setAlpha(1f);
                }else {
                    compoundButton.setTranslationZ(0);
                    compoundButton.setElevation(0);
                    compoundButton.setAlpha(0.6f);
                }
            }
        });
    }

    private void initialiseFields(){
        courseID = getIntent().getStringExtra("COURSE_ID");

        if (courseID == null)
            throw new RuntimeException("Course expected");

        model = new ViewModelProvider(this).get(AddTagsModel.class);
        model.setCourseID(courseID);

        doneButton = (Button) findViewById(R.id.done_button);
        categoryGroup = (RadioGroup) findViewById(R.id.category_radio_group);
        subCategoryGroup = (RadioGroup) findViewById(R.id.sub_category_radio_group);
    }

    private void setUpButtonListeners(){

        // Category Add Button
        Button addCategory = (Button) findViewById(R.id.add_category_button);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.dialog_add_modify_tag_color_);
                EditText tagNameEditText = dialog.findViewById(R.id.tag_name_text2);
                TextView titleText = dialog.findViewById(R.id.dialog_title2);
                RadioGroup colourGroup = dialog.findViewById(R.id.colour_radio_group);

                // Set the author's name to the post title
                titleText.setText("Add Tag");

                Button doneButton = dialog.findViewById(R.id.complete_button2);
                doneButton.setOnClickListener(v1 -> {
                    String name = tagNameEditText.getText().toString();
                    int colourIndex = colourGroup.getCheckedRadioButtonId();

                    if (name.equals("") || colourIndex == -1)
                        Toast.makeText(AddTagsActivity.this, "Selection not completed", Toast.LENGTH_SHORT).show();
                    else {
                        addCategory(name, getColourFromIndex(colourIndex));
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        // Sub Category Add Button
        Button addSubCategory = (Button) findViewById(R.id.add_sub_category_button);
        addSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.dialog_add_modify_tag);
                EditText tagNameEditText = dialog.findViewById(R.id.tag_name_text);
                TextView titleText = dialog.findViewById(R.id.dialog_title);

                // Set the author's name to the post title
                titleText.setText("Add Sub Tag");

                Button doneButton = dialog.findViewById(R.id.complete_button2);
                doneButton.setOnClickListener(v1 -> {
                    String name = tagNameEditText.getText().toString();
                    addSubCategory(name);
                    dialog.dismiss();
                });
                dialog.show();
            }
        });

        // Complete Button Listener
        doneButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setCancelable(true);
            builder.setTitle("Confirm Changes");
            builder.setMessage(
                    "Any changes made will be updated. " +
                            "Are you sure you would like to update the tags?");

            builder.setPositiveButton("Confirm", (dialog, which) -> {
                model.pushTags();
                finish();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {});

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private String getColourFromIndex(int i){
        switch (i){
            case R.id.blue_colour -> {
                return "#0000FF";
            }
            case R.id.red_colour -> {
                return "#FF0000";
            }
            case R.id.green_colour -> {
                return "#00FF00";
            }
            case R.id.yellow_colour -> {
                return "#FFFF00";
            }
            case R.id.lightBlue_colour -> {
                return "#ADD8E6";
            }
            default -> {
                return "#FFFFFF";
            }
        }
    }

    private void setSubCategoryVisibility(boolean isVisible){
        int visibility;

        if (isVisible)
            visibility = View.VISIBLE;
        else
            visibility = View.GONE;

        findViewById(R.id.sub_category_text_view).setVisibility(visibility);
        findViewById(R.id.sub_category_buttons).setVisibility(visibility);
        findViewById(R.id.add_sub_category_button).setVisibility(visibility);
    }



    private void setupToolbar(){

        // Tool bar setup and back button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_tags);
        toolbar.setTitle("Manage Tags - " + course.getName());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setCancelable(true);
            builder.setTitle("Leave page");
            builder.setMessage(
                    "Any changes made will not be saved. " +
                            "Are you sure you would like to leave this page?");

            builder.setPositiveButton("Leave", (dialog, which) -> finish());
            builder.setNegativeButton("Stay", (dialog, which) -> {});

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }
}