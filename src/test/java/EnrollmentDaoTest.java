import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

class EnrollmentDaoTest {
    private Connection connection;
    private EnrollmentDao enrollmentDao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
        );

        String sql = """
                CREATE TABLE enrollments (
                enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
                student_id INT NOT NULL,
                course_id INT NOT NULL,
                waitlisted BOOLEAN NOT NULL DEFAULT FALSE
                );
                """;
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
        enrollmentDao = new EnrollmentDao(connection);
    }

    @AfterEach
    void tearDwon() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE enrollments");
        }
        connection.close();
    }

    @Test
    void insert() {
        Enrollment enrollment = new Enrollment(
                1,
                1
        );
        boolean inserted = enrollmentDao.insert(enrollment);

        assertTrue(inserted);
        assertTrue(enrollment.getEnrollmentId() > 0);
    }

    @Test
    void findById() {
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
    void update() {
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
    void delete() {
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
    void isStudentEnrolled() {
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
}