package au.edu.anu.quirkus.ui.forum.options;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import au.edu.anu.quirkus.data.Enrollment;
import au.edu.anu.quirkus.data.EnrollmentsRepository;
import au.edu.anu.quirkus.data.User;
import au.edu.anu.quirkus.data.UserRepository;

public class RequestModel extends ViewModel {
    private LiveData<List<User>> requestData;

    private final EnrollmentsRepository enrollmentsRepository = new EnrollmentsRepository();

    private final UserRepository userRepository = new UserRepository();
    private String courseId;

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public LiveData<List<User>> getRequests() {
        if (requestData == null) {
            LiveData<List<Enrollment>> enrollments = enrollmentsRepository.getPendingRequests(courseId);

            requestData = Transformations.switchMap(enrollments, enrollmentList -> {
                List<String> ids = enrollmentList.stream().map(e -> e.getUserId()).collect(Collectors.toList());
                return userRepository.getUsersById(ids);
            });
        }
        return requestData;
    }

    public void acceptRequest(String userId, Consumer<Boolean> callback) {
        enrollmentsRepository.setEnrollmentAccess(courseId, userId, "STUDENT", callback);
    }

    public void declineRequest(String userId, Consumer<Boolean> callback) {
        enrollmentsRepository.setEnrollmentAccess(courseId, userId, "NO", callback);
    }
}
