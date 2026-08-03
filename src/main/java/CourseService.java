import java.util.ArrayList;

/**
 * @author Dominic Casoli
 * <br>
 * created: 8/2/2026
 * @since '1.0-SNAPSHOT'
 * Description: Handles the business logic for courses and enrollments.
 * The class communicates with the DAOs, as opposed to letting the user interface access the database directly.
 */
public class CourseService {
    //TODO: Store CourseDao.
    private CourseDao courseDao;

    // TODO: Store EnrollmentDao.
    private EnrollmentDao enrollmentDao;

    /**
     * Creates a CourseService object.
     */
    public CourseService() {
        // TODO: Initialize DAO objects.
    }

    /**
     * Enrolls a student into a course.
     */
    public boolean enrollStudent(int studentId, int courseId) {
        // TODO: Prevent duplicate enrollments.

        // TODO: Add student to the course.

        // TODO: Add student to the waitlist if necessary.

        return false;
    }

    /**
     * Removes a student from a course.
     */
    public boolean dropStudent(int studentId, int courseId) {
        // TODO: Remove the student.

        // TODO: Move the first waitlisted student into the course if one exists.

        return false;
    }

    /**
     * Creates a new course.
     */
    public boolean createCourse(Course course) {
        // TODO: Insert a new course.

        return false;
    }

    /**
     * Updates an existing course.
     */
    public boolean updateCourse(Course course) {
        // TODO: Update course information.

        return false;
    }

    /**
     * Deletes a course.
     */
    public boolean deleteCourse(int courseId) {
        // TODO: Delete the selected course.
        return false;
    }
    /**
     * Retrieves every course.
     */
    public ArrayList<Course> getAllCourses() {
        // TODO: Return every course.
        return null;
    }
    /**
     * Retrieves every enrollment for a student.
     */
    public ArrayList<Enrollment> getStudentEnrollments(int studentId) {
        // TODO: Return every enrollment for the selected student.

        return null;
    }
}
