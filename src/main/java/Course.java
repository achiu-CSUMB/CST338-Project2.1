/**
 * @author Dominic Casoli
 * <br>
 * created: 8/2/2026
 * @since '1.0-SNAPSHOT'
 * Description: Stores info about a course. A course will have an ID, name, and teacher associated with it.
 */

public class Course {

    private int courseId;
    private String courseName;
    private int teacherId;


    /**
     * Creates a new course.
     */
    public Course(String courseName, int teacherId) {

        this.courseName = courseName;
        this.teacherId = teacherId;

    }

    /**
     * Creates a course with all the information.
     */
    public Course(int courseId, String courseName, int teacherId) {

        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherId = teacherId;

    }

    // Getters

    /**
     * @return the course ID.
     */
    public int getCourseId() {
        return courseId;
    }

    /**
     * @return the course name.
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * @return the teacher ID.
     */
    public int getTeacherId() {
        return teacherId;
    }

    // Setters

    /**
     * Updates the course name.
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * Updates the teacher ID.
     */
    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    /**
     * Updates the course ID.
     */
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }


    // TODO: Add any validation for course info.


}
