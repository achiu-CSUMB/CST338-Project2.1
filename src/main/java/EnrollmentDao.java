import java.sql.Connection;
import java.util.ArrayList;

/**
 * @author Dominic Casoli
 * <br>
 * created: 8/2/2026
 * @since '1.0-SNAPSHOT'
 * Description: Handles CRUD operations for student enrollments. Provides methods for managing the enrollments, retrieving students in courses...
 * ....retrieving a student's courses, and checking for duplicate enrollment.
 */
public class EnrollmentDao {
    // TODO: Store the database connection.
    private Connection connection;

    /**
     * Creates an EnrollmentDao object.
     */
    public EnrollmentDao() {
        // TODO: Connect to the project database.
    }

    /**
     * Inserts a new enrollment.
     */
    public boolean insert(Enrollment enrollment) {
        // TODO: Insert the enrollment into the database.

        return false;
    }

    /**
     * Finds an enrollment by its ID.
     */
    public Enrollment findById(int enrollmentId) {
        // TODO: Retrieve enrollment by ID.
        return null;
    }

    /**
     * Updates an existing enrollment.
     */
    public boolean update(Enrollment enrollment) {
        // TODO: Update the selected enrollment.
        return false;
    }
    /**
     * Deletes an enrollment.
     */
    public boolean delete(int enrollmentId) {
        // TODO: Delete the selected enrollment.
        return false;
    }

    /**
     * Retrieves every enrollment.
     */
    public ArrayList<Enrollment> getAllEnrollments() {
        // TODO: Retrieve all enrollments.
        return null;
    }

    /**
     * Retrieves every enrollment for a student.
     */
    public ArrayList<Enrollment> getStudentEnrollments(int studentId) {
        // TODO: Find every enrollment for a student.

        return null;

    }

    /**
     * Retrieves every enrollment for a course.
     */
    public ArrayList<Enrollment> getCourseEnrollments(int courseId) {
        // TODO: Find every enrollment for a course.
        return null;
    }

    /**
     * Checks whether a student is already enrolled in a course or not.
     */
    public boolean isStudentEnrolled(int studentId, int courseId) {
        // TODO: Prevent duplicate enrollment.
        return false;
    }

    /**
     * Retrieves every waitlisted student for a course.
     */
    public ArrayList<Enrollment> getWaitlistedStudents(int courseId) {
        // TODO: Retrieve all waitlisted students for a course.
        return null;
    }
}
