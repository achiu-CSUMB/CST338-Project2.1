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
 * Verifies enrollment creation, prevents duplicate enrollment, waitlist behavior, promotion when student drops a course, dropping students from courses.
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
                        teacher_id INT,
                        capacity INT NOT NULL DEFAULT 2
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

            CourseDao courseDao = new CourseDao(connection);

            enrollmentService = new EnrollmentService(enrollmentDao, courseDao);
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

    assertFalse(enrollmentService.getAllEnrollments().get(0).isWaitlisted());

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

    @Test
    void thirdStudentIsWaitlisted() {
        enrollmentService.enrollStudent(1,1);
        enrollmentService.enrollStudent(2,1);
        enrollmentService.enrollStudent(3,1);

        Enrollment first = enrollmentService.getAllEnrollments().get(0);
        Enrollment second = enrollmentService.getAllEnrollments().get(1);
        Enrollment third = enrollmentService.getAllEnrollments().get(2);

        assertFalse(first.isWaitlisted());

        assertFalse(second.isWaitlisted());

        assertEquals(3, third.getStudentId());

        assertTrue(third.isWaitlisted());
    }

    @Test
    void waitlistSpotPromotedAfterDropping() {
        enrollmentService.enrollStudent(1,1);
        enrollmentService.enrollStudent(2,1);
        enrollmentService.enrollStudent(3,1);

        Enrollment firstEnrollment = enrollmentService.getAllEnrollments().get(0);

        enrollmentService.dropStudent(firstEnrollment.getEnrollmentId());

        Enrollment promoted = null;

        for (Enrollment enrollment : enrollmentService.getAllEnrollments()) {
            if (enrollment.getStudentId() == 3) {
                promoted = enrollment;
                break;
            }
        }
        assertNotNull(promoted);

        assertEquals(3, promoted.getStudentId());

        assertFalse(promoted.isWaitlisted());
    }

    // Was having a spot promotion error before.
    @Test
    void droppingWaitlistedStudentDoesNotPromoteOthers() {
        enrollmentService.enrollStudent(1,1);
        enrollmentService.enrollStudent(2,1);
        enrollmentService.enrollStudent(3,1);
        enrollmentService.enrollStudent(4,1);

        Enrollment waitlisted = enrollmentService.getAllEnrollments().get(2);

        enrollmentService.dropStudent(waitlisted.getEnrollmentId());

        Enrollment fourth = null;

        for (Enrollment enrollment : enrollmentService.getAllEnrollments()) {
            if (enrollment.getStudentId() == 4) {
                fourth = enrollment;
                break;
            }
        }
        assertNotNull(fourth);
        assertTrue(fourth.isWaitlisted());
    }

    @Test
    void dropForNonexistingEnrollmentReturnsFalse() {
        assertFalse(enrollmentService.dropStudent(999));
    }

    @Test
    void capacityControlWaitlist() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                            UPDATE courses
                            SET capacity = 1
                            WHERE course_id = 1;
                            """);
        }
        assertTrue(enrollmentService.enrollStudent(1,1));
        assertTrue(enrollmentService.enrollStudent(2,1));

        Enrollment first = enrollmentService.getAllEnrollments().get(0);

        Enrollment second = enrollmentService.getAllEnrollments().get(1);

        assertFalse(first.isWaitlisted());
        assertTrue(second.isWaitlisted());
    }
}