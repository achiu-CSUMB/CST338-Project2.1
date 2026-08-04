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

    private final CourseDao courseDao;
    private final EnrollmentDao enrollmentDao;

    /**
     * Creates a CourseService object.
     */
    public CourseService() {
        courseDao = new CourseDao();
        enrollmentDao = new EnrollmentDao();
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
        return courseDao.insert(course);
    }

    /**
     * Updates an existing course.
     */
    public boolean updateCourse(Course course) {
        return courseDao.update(course);
    }

    /**
     * Deletes a course.
     */
    public boolean deleteCourse(int courseId) {
        return courseDao.delete(courseId);
    }

    /**
     * Retrieves every course.
     */
    public ArrayList<Course> getAllCourses() {
        return courseDao.getAllCourses();
    }

    /**
     * Retrieves every enrollment for a student.
     */
    public ArrayList<Enrollment> getStudentEnrollments(int studentId) {
        return enrollmentDao.getStudentEnrollments(studentId);
    }
}
