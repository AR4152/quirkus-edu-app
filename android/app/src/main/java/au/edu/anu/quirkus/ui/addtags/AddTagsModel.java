package au.edu.anu.quirkus.ui.addtags;

import android.content.Context;
import android.util.ArraySet;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import au.edu.anu.quirkus.ActiveUser;
import au.edu.anu.quirkus.PostType;
import au.edu.anu.quirkus.data.TagDefinition;
import au.edu.anu.quirkus.data.Course;
import au.edu.anu.quirkus.data.CoursesRepository;
import au.edu.anu.quirkus.data.PostsRepository;

/**
 * @author Arjun Raj (u7526852)
 * <p>
 * Manages data of AddTagsActivity.java class
 * Also helps communicate data between the Add Tag dialog and Add Sub Tag dialog
 */
public class AddTagsModel extends ViewModel {
    private MutableLiveData<RadioButton> category = new MutableLiveData<RadioButton>();
    private MutableLiveData<RadioButton> subCategory = new MutableLiveData<RadioButton>();
    private final CoursesRepository coursesRepository = new CoursesRepository();
    Map<String, TagDefinition> currentCategories;
    private String courseID;
    String colour;
    public String getCourseID() {
        return courseID;
    }
    private LiveData<Course> course;
    private Course courseInit;

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void selectCategory(RadioButton item) {
        String tagName = item.getText().toString();
        colour = currentCategories.get(tagName).getColor();
        category.setValue(item);
    }

    public LiveData<RadioButton> getSelectedCategory() {
        return category;
    }

    public void selectSubCategory(RadioButton item) {
        subCategory.setValue(item);
    }

    public LiveData<Course> getCourse() {
        if (course == null) {
            course = coursesRepository.getCourse(courseID);
        }
        return course;
    }

    public void setCourseInit(Course courseInit) {
        this.courseInit = courseInit;
    }

    public void updateCategory(String name, String colour){
        currentCategories.put(name, new TagDefinition(name, colour, new ArraySet<>()));
    }

    public boolean updateSubCategory(String name){
        String selectedCategoryName = (String) category.getValue().getText();
        var categoryInstance = currentCategories.get(selectedCategoryName);

        return !categoryInstance.getSubTags().add(name);
    }

    public void pushTags(){
        coursesRepository.setCourseTags(courseID, currentCategories);
    }
}
