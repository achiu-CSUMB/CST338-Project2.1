import java.util.ArrayList;

/**
 * @author Dominic Casoli
 * <br>
 * created: 8/9/2026
 * @since '1.0-SNAPSHOT'
 * Description: Provides the enrollment business logic between EnrollmentController and EnrollmentDao.
 * Handles enrolling students, dropping enrollments, preventing duplicate enrollments.
 */

public class EnrollmentService {

    private EnrollmentDao enrollmentDao;

    /**
     * Creates an EnrollmentService object.
     */
    public EnrollmentService() {
        enrollmentDao = new EnrollmentDao();
    }

    /**
     * Creates an EnrollmentService object using a provided DAO, used for testing.
     */
    public EnrollmentService(EnrollmentDao enrollmentDao) {
        this.enrollmentDao = enrollmentDao;
    }

    /**
     * Enrolls a student into a course.
     * Prevents duplicate enrollments.
     */
    public boolean enrollStudent(int studentId, int courseId) {
        if(enrollmentDao.isStudentEnrolled(studentId, courseId)) {
            return false;
        }
        Enrollment enrollment = new Enrollment(studentId, courseId);
        return enrollmentDao.insert(enrollment);
    }

    /**
     * Drops a student from a course.
     */
    public boolean dropStudent(int enrollmentId) {
        return enrollmentDao.delete(enrollmentId);
    }

    /**
     * Retrieves all enrollments.
     */
    public ArrayList<Enrollment> getAllEnrollments() {
        return enrollmentDao.getAllEnrollments();
    }
}
