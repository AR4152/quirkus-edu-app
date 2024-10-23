package au.edu.anu.quirkus.ui.addpost;

import android.content.Context;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import au.edu.anu.quirkus.ActiveUser;
import au.edu.anu.quirkus.PostType;
import au.edu.anu.quirkus.data.Course;
import au.edu.anu.quirkus.data.CoursesRepository;
import au.edu.anu.quirkus.data.PostsRepository;
import au.edu.anu.quirkus.data.TagDefinition;

/**
 * @author Arjun Raj (u7526852)
 * <p>
 * Manages data of AddPost.java class
 * Also helps communicate data between the Tag fragment and Sub Tag fragment
 */
public class AddPostModel extends ViewModel {
    private PostType postType;
    private String title;
    private MutableLiveData<RadioButton> category = new MutableLiveData<RadioButton>();
    private MutableLiveData<RadioButton> subCategory = new MutableLiveData<RadioButton>();
    private String text;
    private boolean isPrivate;
    private boolean isAnon;
    private final CoursesRepository coursesRepository = new CoursesRepository();

    private String courseID;

    public String getCourseID() {
        return courseID;
    }

    private LiveData<Course> course;
    private Course courseInit;

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void resetSelection() {
        category = new MutableLiveData<RadioButton>();
        subCategory = new MutableLiveData<RadioButton>();
    }

    public void selectCategory(RadioButton item) {
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

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public void setAnon(boolean anon) {
        isAnon = anon;
    }

    public boolean isPostCompleted(){

        // Category not selected
        if (category.getValue() == null)
            return false;

        Set<String> subTags = courseInit.getTagDefinitions().get(category.getValue().getText().toString()).getSubTags();

        boolean isSubCategoryAvailable = !(subTags == null || subTags.isEmpty());

        // Sub Category not selected
        if (isSubCategoryAvailable && subCategory.getValue() == null)
            return false;

        return  title != null && text != null && postType != null
                && !title.equals("") && !text.equals("");
    }

    public void createPost(Context context){
        String subTag = null;

        String tag = category.getValue().getText().toString();
        if (subCategory.getValue() != null)
             subTag = subCategory.getValue().getText().toString();

        PostsRepository postsRepository = new PostsRepository();
        postsRepository.createNewPost(
                courseID,
                ActiveUser.getFirebaseID(),
                postType.toString(),
                title,
                text,
                tag,
                subTag,
                !isPrivate,
                isAnon,
                result -> {
                    if (result) {
                        Toast.makeText(context, "Post Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Post Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
