package au.edu.anu.quirkus.ui.forum.options;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import au.edu.anu.quirkus.UserModel;
import au.edu.anu.quirkus.UserModelFactory;
import au.edu.anu.quirkus.data.Enrollment;
import au.edu.anu.quirkus.data.EnrollmentsRepository;
import au.edu.anu.quirkus.data.User;
import au.edu.anu.quirkus.data.UserRepository;
import au.edu.anu.quirkus.searchTree.EnrolmentSearchTree;

public class EnrollmentsModel extends ViewModel {

    private MediatorLiveData<List<au.edu.anu.quirkus.UserModel>> usersEnrolledData;

    private final EnrollmentsRepository enrollmentsRepository = new EnrollmentsRepository();

    private final UserRepository userRepository = new UserRepository();

    private String courseID;

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();

    public LiveData<List<UserModel>> getEnrollments() {
        if (usersEnrolledData == null) {
            LiveData<List<Enrollment>> enrollments = enrollmentsRepository.getEnrollmentsForCourse(courseID);

            LiveData<List<User>> users = Transformations.switchMap(enrollments, enrollmentList -> {
                List<String> ids = enrollmentList.stream().map(e -> e.getUserId()).collect(Collectors.toList());
                return userRepository.getUsersById(ids);
            });

            LiveData<EnrolmentSearchTree> searchTree = Transformations.map(users, userList -> {
                EnrolmentSearchTree tree = new EnrolmentSearchTree();
                tree = tree.insert(tree, userList);
                return tree;
            });

            MediatorLiveData<List<UserModel>> usersEnrolledDataAll = new MediatorLiveData<>();

            usersEnrolledDataAll.addSource(enrollments, e -> usersEnrolledDataAll.postValue(UserModelFactory.createUserModels(users.getValue(), e)));
            usersEnrolledDataAll.addSource(users, u -> usersEnrolledDataAll.postValue(UserModelFactory.createUserModels(u, enrollments.getValue())));

            usersEnrolledData = new MediatorLiveData<>();

            usersEnrolledData.addSource(usersEnrolledDataAll, u -> usersEnrolledData.postValue(filterUsersBySearch(u, searchTree.getValue(), searchQuery.getValue())));
            usersEnrolledData.addSource(searchTree, s -> usersEnrolledData.postValue(filterUsersBySearch(usersEnrolledDataAll.getValue(), s, searchQuery.getValue())));
            usersEnrolledData.addSource(searchQuery, s -> usersEnrolledData.postValue(filterUsersBySearch(usersEnrolledDataAll.getValue(), searchTree.getValue(), s)));
        }
        return usersEnrolledData;
    }

    private List<UserModel> filterUsersBySearch(List<UserModel> models, EnrolmentSearchTree enrolmentSearchTree, String query) {
        if (query == null || query.isEmpty()) return models;

        List<User> users = enrolmentSearchTree.search2(query);
        return models.stream().filter(u -> users.contains(u.getUser())).collect(Collectors.toList());
    }

    public void promoteUserToStaff(String userId, Consumer<Boolean> callback) {
        enrollmentsRepository.setEnrollmentAccess(courseID, userId, "STAFF", callback);
    }

    public void demoteUserToStudent(String userId, Consumer<Boolean> callback) {
        enrollmentsRepository.setEnrollmentAccess(courseID, userId, "STUDENT", callback);
    }

    public void removeUser(String userId, Consumer<Boolean> callback) {
        enrollmentsRepository.leaveCourse(courseID, userId, callback);
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery.postValue(searchQuery);
    }
}
