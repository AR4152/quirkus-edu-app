package au.edu.anu.quirkus.ui.forum;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import au.edu.anu.quirkus.ActiveUser;
import au.edu.anu.quirkus.AuthorPostModel;
import au.edu.anu.quirkus.AuthorPostModelFactory;
import au.edu.anu.quirkus.UserModel;
import au.edu.anu.quirkus.UserModelFactory;
import au.edu.anu.quirkus.data.Course;
import au.edu.anu.quirkus.data.CoursesRepository;
import au.edu.anu.quirkus.data.Enrollment;
import au.edu.anu.quirkus.data.EnrollmentsRepository;
import au.edu.anu.quirkus.data.Post;
import au.edu.anu.quirkus.data.PostRoot;
import au.edu.anu.quirkus.data.PostsReplyRepository;
import au.edu.anu.quirkus.data.PostsRepository;
import au.edu.anu.quirkus.data.User;
import au.edu.anu.quirkus.data.UserRepository;
import au.edu.anu.quirkus.postSearch.Parser;

public class ForumModel extends ViewModel {
    private final CoursesRepository coursesRepository = new CoursesRepository();
    private final PostsRepository postsRepository = new PostsRepository();
    private final EnrollmentsRepository enrollmentsRepository = new EnrollmentsRepository();
    private final UserRepository userRepository = new UserRepository();

    private String courseID;

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }
    private LiveData<Course> course;

    public LiveData<Course> getCourse() {
        if (course == null) {
            course = coursesRepository.getCourse(courseID);
        }
        return course;
    }
    private MediatorLiveData<List<AuthorPostModel>> posts;

    private MutableLiveData<String> searchQuery = new MutableLiveData<>();

    private MutableLiveData<String> searchQueryError = new MediatorLiveData<>();

    public LiveData<List<AuthorPostModel>> getPosts() {
        if (posts == null) {
            // Get raw for userModel
            LiveData<List<Enrollment>> staffEnrollment = enrollmentsRepository.getStaffForCourse(courseID);
            LiveData<List<User>> users = userRepository.getAllUsers();

            // Is the current user staff?
            LiveData<Boolean> isUserStaff = Transformations.map(staffEnrollment, enrollments -> enrollments.stream().filter(e -> e.getUserId().equals(ActiveUser.getFirebaseID())).count() > 0);

            MediatorLiveData<Pair<String, Boolean>> searchStaff = new MediatorLiveData<>();
            searchStaff.addSource(searchQuery, q -> searchStaff.postValue(new Pair<>(q, isUserStaff.getValue())));
            searchStaff.addSource(isUserStaff, s -> searchStaff.postValue(new Pair<>(searchQuery.getValue(), s)));

            LiveData<List<Post>> rawPosts = Transformations.switchMap(searchStaff, stuff -> {
                if (stuff.first == null) {
                    return postsRepository.getPostsInCourse(courseID, stuff.second);
                } else {
                    try {
                        Parser p = new Parser(stuff.first);
                        p.parse();
                        LiveData<List<Post>> posts = postsRepository.getPostsInCourseSearch(courseID, p, stuff.second);
                        LiveData<List<Post>> replies = new PostsReplyRepository().getPostsInCourseSearch(courseID, p);

                        MediatorLiveData<List<Post>> res = new MediatorLiveData<>();
                        res.addSource(posts, _p -> {
                            List<Post> _r = replies.getValue();
                            if (_r == null) {res.postValue(_p); return;}
                            if (_p == null) {res.postValue(_r); return;}
                            _p.addAll(_r);
                            res.postValue(_p);
                        });
                        res.addSource(replies, _r -> {
                            List<Post> _p = posts.getValue();
                            if (_r == null) {res.postValue(_p); return;}
                            if (_p == null) {res.postValue(_r); return;}
                            _p.addAll(_r);
                            res.postValue(_p);
                        });
                        return res;
                    } catch (RuntimeException e) {
                        searchQueryError.postValue(e.getMessage());
                        // Empty stuff
                        MutableLiveData<List<Post>> t = new MutableLiveData<>();
                        t.postValue(List.of());
                        return t;
                    }
                }
            });

            // Create UserModel to determine staff status
            MediatorLiveData<List<UserModel>> userModel = new MediatorLiveData<>();
            userModel.addSource(users, u -> userModel.postValue(UserModelFactory.createUserModels(u, staffEnrollment.getValue())));
            userModel.addSource(staffEnrollment, s -> userModel.postValue(UserModelFactory.createUserModels(users.getValue(), s)));

            // Create posts based on posts and user data
            posts = new MediatorLiveData<>();
            posts.addSource(rawPosts, post -> posts.postValue(AuthorPostModelFactory.createPostModels(post, userModel.getValue(), isUserStaff.getValue())));
            posts.addSource(userModel, user -> posts.postValue(AuthorPostModelFactory.createPostModels(rawPosts.getValue(), user, isUserStaff.getValue())));
            posts.addSource(isUserStaff, staff -> posts.postValue(AuthorPostModelFactory.createPostModels(rawPosts.getValue(), userModel.getValue(), staff)));
        }
        return posts;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery.postValue(searchQuery);
    }

    public LiveData<String> getSearchQueryError() {
        return searchQueryError;
    }

    public void leaveCourse(Consumer<Boolean> callback) {
        enrollmentsRepository.leaveCourse(courseID, ActiveUser.getFirebaseID(), callback);
    }

    public void archiveCourse(Consumer<Boolean> callback) {
        coursesRepository.setCourseArchiveStatus(courseID, true, callback);
    }

    public LiveData<Boolean> getActiveUserAccess(String courseID) {
        LiveData<List<Enrollment>> staffEnrollment = enrollmentsRepository.getStaffForCourse(courseID);
        LiveData<List<User>> users = userRepository.getAllUsers();

        // Is the current user staff?
        return Transformations.map(staffEnrollment, enrollments -> enrollments.stream().filter(e -> e.getUserId().equals(ActiveUser.getFirebaseID())).count() > 0);
    }
}
