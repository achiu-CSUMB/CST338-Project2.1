/**
 * @author Dominic Casoli
 * <br>
 * created: 8/2/2026
 * @since '1.0-SNAPSHOT'
 * Description: Stores enrollment information that connects students to courses. Tracks the student, the course, and if the student is in a waitlist.
 */

public class Enrollment {

    private int enrollmentId;
    private int studentId;
    private int courseId;
    // Use this field when implementing waitlist enhancement.
    private boolean waitlisted;

    /**
     * Creates a new enrollment.
     */
    public Enrollment(int studentId, int courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.waitlisted = false;
    }

    /**
     * Creates an enrollment with all the information
     */
    public Enrollment(int enrollmentId, int studentId, int courseId) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.waitlisted = false;
    }

    // Getters

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public boolean isWaitlisted() {
        return waitlisted;
    }


    // Setters

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setWaitlisted(boolean waitlisted) {
        this.waitlisted = waitlisted;
    }


    // TODO: Write the header descriptions later.
}
