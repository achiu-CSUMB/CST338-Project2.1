package service;

import dao.CourseDao;
import model.Course;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Dominic Casoli
 * <br>
 * created: 8/12/2026
 * @since '1.0-SNAPSHOT'
 * Description:
 */

public class CourseServiceTest {
    private Connection connection;
    private CourseService courseService;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:courseservice;DB_CLOSE_DELAY=-1");

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                        CREATE TABLE courses (
                            course_id INT AUTO_INCREMENT PRIMARY KEY,
                            title VARCHAR(255) NOT NULL,          
                            capacity INT NOT NULL DEFAULT 2,
                            prefix VARCHAR(20),
                            teacher_name VARCHAR(255)
                        );
                        """);
        }
        CourseDao courseDao = new CourseDao(connection);

        courseService = new CourseService(courseDao);
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE courses");
        }
        connection.close();
    }
    @Test
    void createsCourse() {
        Course course = new Course(0, "Computer Science", 2, "Dr.", "Smith");

        boolean created = courseService.createCourse(course);

        assertTrue(created);

        assertTrue(course.getCourseId() > 0);
    }

    @Test
    void retrieveCourses() {
        Course course = new Course(0, "Computer Science", 2, "Dr.", "Smith");

        courseService.createCourse(course);

        Assertions.assertEquals(1,courseService.getAllCourses().size());

        Assertions.assertEquals("Dr.", courseService.getAllCourses().get(0).getPrefix());

        Assertions.assertEquals("Smith", courseService.getAllCourses().get(0).getTeacherName());
    }

    @Test
    void updateCourse() {
        Course course = new Course(0, "Computer Science", 2, "Dr.", "Smith");

        courseService.createCourse(course);

        course.setCapacity(4);

        course.setPrefix("Prof.");

        course.setTeacherName("Jones");

        boolean updated = courseService.updateCourse(course);

        assertTrue(updated);

        Course updatedCourse = courseService.getAllCourses().get(0);

        Assertions.assertEquals(4, updatedCourse.getCapacity());

        Assertions.assertEquals("Prof.", updatedCourse.getPrefix());
        Assertions.assertEquals("Jones", updatedCourse.getTeacherName());
    }

    @Test
    void deleteCourse() {
        Course course = new Course(0, "Computer Science", 2, "Dr.", "Smith");

        courseService.createCourse(course);

        boolean deleted = courseService.deleteCourse(course.getCourseId());

        assertTrue(deleted);

        Assertions.assertEquals(0, courseService.getAllCourses().size());
    }
}
