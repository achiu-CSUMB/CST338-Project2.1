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
 * created: 8/10/2026
 * @since '1.0-SNAPSHOT'
 * Description: Tests the business logic for student enrollment.
 * Verifies enrollment creation, prevents duplicate enrollment, dropping students from courses.
 */

public class EnrollmentServiceTest {
    private Connection connection;
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:enrollmentservice;DB_CLOSE_DELAY=-1");

        try (Statement statement = connection.createStatement()) {

            statement.execute("""
                    CREATE TABLE courses (
                        course_id INT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(255),
                        teacher_id INT
                    );
                    """);


            statement.execute("""
                    CREATE TABLE enrollments (
                    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
                    student_id INT NOT NULL,
                    course_id INT NOT NULL,
                    waitlisted BOOLEAN NOT NULL DEFAULT FALSE
                    );
                    """);

            statement.execute("""
                    INSERT INTO courses(title, teacher_id)
                    VALUES ('Computer Science',1);
                    """);

            EnrollmentDao enrollmentDao = new EnrollmentDao(connection);

            enrollmentService = new EnrollmentService(enrollmentDao);
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS enrollments");
            statement.execute("DROP TABLE IF EXISTS courses");
        }
        connection.close();
    }

    @Test
    void preventsDuplicateEnrollment() {
        boolean first = enrollmentService.enrollStudent(1,1);

        boolean second = enrollmentService.enrollStudent(1,1);

        assertTrue(first);

        assertFalse(second);
    }

    @Test
    void enrollStudentSuccessfully() {
        boolean result = enrollmentService.enrollStudent(5,1);
    assertTrue(result);

    assertEquals(
            1,
            enrollmentService.getAllEnrollments().size()
    );

    assertEquals(
            5,
            enrollmentService.getAllEnrollments()
                    .get(0)
                    .getStudentId()
    );

    }

    @Test
    void dropStudentSuccessfully() {
        enrollmentService.enrollStudent(1,1);

        Enrollment enrollment = enrollmentService.getAllEnrollments().get(0);

        boolean deleted = enrollmentService.dropStudent(
                enrollment.getEnrollmentId()
        );

        assertTrue(deleted);

        assertEquals(
                0,
                enrollmentService.getAllEnrollments().size()
        );
    }
}
