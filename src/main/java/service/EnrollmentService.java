package service;

import dao.CourseDao;
import dao.EnrollmentDao;
import model.Course;
import model.Enrollment;

import java.util.ArrayList;

/**
 * @author Dominic Casoli
 * <br>
 * created: 8/9/2026
 * @since '1.0-SNAPSHOT'
 * Description: Provides the enrollment business logic between controller.EnrollmentController and dao.EnrollmentDao.
 * Handles enrolling students, dropping enrollments, preventing duplicate enrollments, managing waitlist, automatically promote waitlist students when spot available.
 */

public class EnrollmentService {

    private EnrollmentDao enrollmentDao;
    private CourseDao courseDao;

    /**
     * Creates an service.EnrollmentService object.
     */
    public EnrollmentService() {
        enrollmentDao = new EnrollmentDao();
        courseDao = new CourseDao();
    }

    /**
     * Creates an service.EnrollmentService object using a provided DAO, used for testing.
     */
    public EnrollmentService(EnrollmentDao enrollmentDao, CourseDao courseDao) {
        this.enrollmentDao = enrollmentDao;
        this.courseDao = courseDao;
    }

    /**
     * Enrolls a student into a course.
     * Prevents duplicate enrollments.
     * Puts students on the waitlist when course reaches capacity.
     */
    public boolean enrollStudent(int studentId, int courseId) {
        if(enrollmentDao.isStudentEnrolled(studentId, courseId)) {
            return false;
        }

        Course course = courseDao.findById(courseId);

        if (course == null) {
            return false;
        }

        Enrollment enrollment = new Enrollment(studentId, courseId);

        if (enrollmentDao.getEnrollmentCount(courseId) >= course.getCapacity()) {
            enrollment.setWaitlisted(true);
        }
        return enrollmentDao.insert(enrollment);
    }

    /**
     * Drops a student from a course.
     * Auto promotes first waitlisted if one exists.
     */
    public boolean dropStudent(int enrollmentId) {
        Enrollment enrollment = enrollmentDao.findById(enrollmentId);

        if (enrollment == null) {
            return false;
        }

        boolean deleted = enrollmentDao.delete(enrollmentId);

        if (!deleted) {
            return false;
        }

        // Promotes only if ENROLLED student dropped.
        Enrollment promoted = null;
        if (!enrollment.isWaitlisted()) {
            promoted = enrollmentDao.getFirstWaitlistedStudent(enrollment.getCourseId()
            );
        }

        if (promoted != null) {
            promoted.setWaitlisted(false);
            enrollmentDao.update(promoted);
        }
        return true;
    }

    /**
     * Return student position in waitlist.
     * 0 - Will be next on the waitlist.
     * -1 - Is not waitlisted.
     */
    public int getWaitlistPosition(Enrollment enrollment) {
        if(!enrollment.isWaitlisted()) {
            return -1;
        }
        ArrayList<Enrollment> waitlisted = enrollmentDao.getWaitlistedStudents(enrollment.getCourseId());

        for (int i = 0; i < waitlisted.size(); i++) {
            if (waitlisted.get(i).getEnrollmentId() == enrollment.getEnrollmentId()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Fixes issue of Enrollments of all students showing up on same account.
     * @param studentId of the students account.
     * @return a list of course enrollments for the specified student.
     */
    public ArrayList<Enrollment> getStudentEnrollments(int studentId) {
        return enrollmentDao.getStudentEnrollments(studentId);
    }

    /**
     * Retrieves all enrollments.
     */
    public ArrayList<Enrollment> getAllEnrollments() {
        return enrollmentDao.getAllEnrollments();
    }
}
