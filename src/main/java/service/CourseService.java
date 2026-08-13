package service;

import dao.CourseDao;
import model.Course;

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

    private CourseDao courseDao;

    /**
     * Creates a service.CourseService object.
     */
    public CourseService() {
        courseDao = new CourseDao();
    }

    /**
     * Creates service.CourseService using provided DAO.
     * Testing.
     */
    public CourseService(CourseDao courseDao) {
        this.courseDao = courseDao;
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
}
