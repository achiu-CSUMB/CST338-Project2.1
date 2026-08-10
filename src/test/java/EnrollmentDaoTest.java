import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dominic Casoli
 * <br>
 * created: 8/4/2026
 * @since '1.0-SNAPSHOT'
 * Description: Tests the CRUD operations and database functionality of EnrollmentDao.
 */

class EnrollmentDaoTest {
    private Connection connection;
    private EnrollmentDao enrollmentDao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:enrollmentdao;DB_CLOSE_DELAY=-1"
        );

        String sql = """
                CREATE TABLE IF NOT EXISTS courses (
                course_id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255),
                teacher_id INT
                );
                
                CREATE TABLE IF NOT EXISTS enrollments (
                    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
                    student_id INT NOT NULL,
                    course_id INT NOT NULL,
                    waitlisted BOOLEAN NOT NULL DEFAULT FALSE
                );
                
                INSERT INTO courses(title, teacher_id)
                VALUES ('Computer Science', 1);
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
        enrollmentDao = new EnrollmentDao(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE enrollments");
            statement.execute("DROP TABLE courses");
        }
        connection.close();
    }

    @Test
    void insert() throws SQLException {
        Enrollment enrollment = new Enrollment(
                1,
                1
        );
        boolean inserted = enrollmentDao.insert(enrollment);

        assertTrue(inserted);
        assertTrue(enrollment.getEnrollmentId() > 0);
    }

    @Test
    void findById() throws SQLException {
        Enrollment enrollment = new Enrollment(
                1,
                1
        );
        enrollmentDao.insert(enrollment);
        Enrollment found = enrollmentDao.findById(
                enrollment.getEnrollmentId()
        );
        assertNotNull(found);

        assertEquals(
                1,
                found.getStudentId()
        );
        assertEquals(
                1,
                found.getCourseId()
        );
    }

    @Test
    void update() throws SQLException {
        Enrollment enrollment = new Enrollment(
                1,
                1
        );
        enrollmentDao.insert(enrollment);
        enrollment.setWaitlisted(true);
        boolean updated = enrollmentDao.update(enrollment);
        assertTrue(updated);

        Enrollment updateEnrollment = enrollmentDao.findById(
                enrollment.getEnrollmentId()
        );
        assertTrue(
                updateEnrollment.isWaitlisted()
        );
    }

    @Test
    void delete() throws SQLException {
        Enrollment enrollment = new Enrollment(
                1,
                1
        );
        enrollmentDao.insert(enrollment);
        boolean deleted = enrollmentDao.delete(
                enrollment.getEnrollmentId()
        );

        assertTrue(deleted);

        assertNull(
                enrollmentDao.findById(
                        enrollment.getEnrollmentId()
                )
        );
    }

    @Test
    void isStudentEnrolled() throws SQLException {
        Enrollment enrollment = new Enrollment(
                1,
                1
        );
        enrollmentDao.insert(enrollment);
        assertTrue(
                enrollmentDao.isStudentEnrolled(1, 1)
        );
        assertFalse(
                enrollmentDao.isStudentEnrolled(2, 1)
        );
    }

    @Test
    void getWaitlistedStudents() throws SQLException {
        Enrollment normalEnrollment = new Enrollment(
                1,
                1
        );

        Enrollment waitlistedEnrollment = new Enrollment(
                2,
                1
        );

        waitlistedEnrollment.setWaitlisted(true);

        enrollmentDao.insert(normalEnrollment);
        enrollmentDao.insert(waitlistedEnrollment);

        ArrayList<Enrollment> waitlisted =
                enrollmentDao.getWaitlistedStudents(1);

        assertEquals(1, waitlisted.size());

        assertEquals(
                2,
                waitlisted.get(0).getStudentId()
        );

        assertEquals(
                1,
                waitlisted.get(0).getCourseId()
        );

        assertTrue(
                waitlisted.get(0).isWaitlisted()
        );
    }

    @Test
    void getAllEnrollments() throws SQLException {
        Enrollment enrollment = new Enrollment(
                1,
                1
        );
        enrollmentDao.insert(enrollment);
        ArrayList<Enrollment> enrollments =
                enrollmentDao.getAllEnrollments();

        assertEquals(
                1,
                enrollments.size()
        );
        assertEquals(
                1,
                enrollments.get(0).getStudentId()
        );
    }

    @Test
    void hasCourseName() throws SQLException {
        Enrollment enrollment =
                new Enrollment(
                        1,
                        1
                );
        enrollmentDao.insert(enrollment);

        ArrayList<Enrollment> enrollments =
                enrollmentDao.getAllEnrollments();

        assertEquals("Computer Science", enrollments.get(0).getCourseName());
    }
}