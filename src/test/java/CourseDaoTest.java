import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dominic Casoli
 * <br>
 * created: 8/4/2026
 * @since '1.0-SNAPSHOT'
 * Description: Tests the CRUD operations and database functionality of CourseDao.
 */

class CourseDaoTest {
    private Connection connection;
    private CourseDao courseDao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
        );

        String sql = """
                CREATE TABLE courses (
                course_id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                capacity INT NOT NULL DEFAULT 2,
                prefix VARCHAR(20),
                teacher_name VARCHAR(255)
                );
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
        courseDao = new CourseDao(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE courses");
        }
        connection.close();
    }

    @Test
    void insert() {
        Course course = new Course(0,
                "Computer Science",
                2,
                "Dr.",
                "Smith"
        );
        boolean inserted = courseDao.insert(course);
        assertTrue(inserted);
        assertTrue(course.getCourseId() > 0);
    }

    @Test
    void findById() {
        Course course = new Course(0,
                "Computer Science",
                2,
                "Dr.",
                "Smith"
        );
        courseDao.insert(course);
        Course found = courseDao.findById(
                course.getCourseId()
        );

        assertNotNull(found);

        assertEquals(
                "Computer Science",
                found.getCourseName()
        );
        assertEquals("Dr.", found.getPrefix());
        assertEquals("Smith", found.getTeacherName());
    }

    @Test
    void update() {
        Course course = new Course(0,
                "Computer Science",
                2,
                "Dr.",
                "Smith"
        );

        courseDao.insert(course);
        course.setCourseName(
                "Software Engineering"
        );
        course.setPrefix("Prof.");
        course.setTeacherName("Jones");
        boolean updated = courseDao.update(course);
        assertTrue(updated);
        Course updatedCourse = courseDao.findById(
                course.getCourseId()
        );
        assertEquals(
                "Software Engineering",
                updatedCourse.getCourseName()
        );
        assertEquals("Prof.", updatedCourse.getPrefix());
        assertEquals("Jones", updatedCourse.getTeacherName());
    }

    @Test
    void delete() {
        Course course = new Course(0,
                "Computer Science",
                2,
                "Dr.",
                "Smith"
        );

        courseDao.insert(course);
        boolean deleted = courseDao.delete(
                course.getCourseId()
        );
        assertTrue(deleted);

        assertNull(
                courseDao.findById(course.getCourseId())
        );
    }

}
