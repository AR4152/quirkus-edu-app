package au.edu.anu.quirkus.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.stream.Collectors;

import au.edu.anu.quirkus.data.Course;
import au.edu.anu.quirkus.data.CoursesRepository;
import au.edu.anu.quirkus.data.EnrollmentsRepository;

public class HomeModel extends ViewModel {
    private LiveData<List<Course>> enrolledClassesData;
    private final CoursesRepository coursesRepository = new CoursesRepository();
    private final EnrollmentsRepository enrollmentsRepository = new EnrollmentsRepository();


    public LiveData<List<Course>> getEnrolledClasses(String userID) {
        if (enrolledClassesData == null) {
            enrolledClassesData = new MutableLiveData<>();

            enrolledClassesData = Transformations.switchMap(enrollmentsRepository.getUserEnrolledCourses(userID), enrolment -> {
                List<String> ids = enrolment.stream().map(e -> e.getCourseId()).collect(Collectors.toList());

                LiveData<List<Course>> courseRepo = coursesRepository.getCourses(ids);
                Log.d("Model", "Updating courses");
                return courseRepo;
            });
        }

        return enrolledClassesData;
    }

    @Override
    protected void onCleared() {
        Log.d("Clear", "Stop");
        super.onCleared();
    }
}
