/**
 * @author Dominic Casoli
 * <br>
 * created: 8/2/2026
 * @since '1.0-SNAPSHOT'
 * Description: Stores info about a course. A course will have an ID, class name.
 */

// Note: Due to complications with merge, I had to grab the code from before merge and port it manually myself.
public class Course {

    private int courseId;
    private String courseName;
    private int capacity;


    /**
     * Creates a new course.
     */
    public Course(String courseName,  int capacity) {

        this.courseName = courseName;
        this.capacity = capacity;

    }

    /**
     * Creates a course with all the information.
     */
    public Course(int courseId, String courseName, int capacity) {

        this.courseId = courseId;
        this.courseName = courseName;
        this.capacity = capacity;

    }

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
     * @return The max number of enrolled students.
     */
    public int getCapacity() {
        return capacity;
    }

    // Setters

    /**
     * Updates the course name.
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * Updates course capacity.
     * @param capacity student size for a course.
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Updates the course ID.
     */
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }


}