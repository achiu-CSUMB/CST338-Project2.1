import org.junit.jupiter.api.AfterEach;
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
                            capacity INT NOT NULL DEFAULT 2
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
        Course course = new Course("Computer Science", 2);

        boolean created = courseService.createCourse(course);

        assertTrue(created);

        assertTrue(course.getCourseId() > 0);
    }

    @Test
    void retrieveCourses() {
        Course course = new Course("Computer Science", 2);

        courseService.createCourse(course);

        assertEquals(1,courseService.getAllCourses().size());
    }

    @Test
    void updateCourse() {
        Course course = new Course("Computer Science", 2);

        courseService.createCourse(course);

        course.setCapacity(4);

        boolean updated = courseService.updateCourse(course);

        assertTrue(updated);

        Course updatedCourse = courseService.getAllCourses().get(0);

        assertEquals(4, updatedCourse.getCapacity());
    }

    @Test
    void deleteCourse() {
        Course course = new Course("Computer Science", 2);

        courseService.createCourse(course);

        boolean deleted = courseService.deleteCourse(course.getCourseId());

        assertTrue(deleted);

        assertEquals(0, courseService.getAllCourses().size());
    }
}
