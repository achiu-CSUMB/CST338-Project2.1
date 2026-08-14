package dao;

import model.Assignment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: Oswald Perales
 * Date: 8/4/2026
 * Description: Tests AssignmentDao insert, read, update, and delete
 */

class AssignmentDaoTest {

    private Connection connection;
    private AssignmentDao assignmentDao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:assignmenttest;DB_CLOSE_DELAY=-1"
        );

        String sql = """
                CREATE TABLE assignments (
                    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
                    course_id INT NOT NULL,
                    title VARCHAR(255) NOT NULL,
                    description VARCHAR(255),
                    due_date VARCHAR(255) NOT NULL,
                    max_points DOUBLE NOT NULL
                );
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }

        assignmentDao = new AssignmentDao(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE assignments");
        }

        connection.close();
    }

    @Test
    void assignmentCrudTest() {
        Assignment assignment = new Assignment(
                1,
                "Java Quiz",
                "Quiz about Java",
                "2026-08-05",
                100.0
        );

        // Insert
        assertTrue(assignmentDao.insert(assignment));
        assertTrue(assignment.getAssignmentId() > 0);

        // Read
        Assignment foundAssignment =
                assignmentDao.findById(assignment.getAssignmentId());

        assertNotNull(foundAssignment);
        assertEquals("Java Quiz", foundAssignment.getTitle());

        // Update
        assignment.setTitle("Updated Java Quiz");
        assertTrue(assignmentDao.update(assignment));

        Assignment updatedAssignment =
                assignmentDao.findById(assignment.getAssignmentId());

        assertEquals("Updated Java Quiz", updatedAssignment.getTitle());

        // Delete
        assertTrue(assignmentDao.delete(assignment.getAssignmentId()));
        assertNull(assignmentDao.findById(assignment.getAssignmentId()));
    }

    @Test
    void findMissingAssignmentReturnsNull() {
        Assignment assignment = assignmentDao.findById(999);

        assertNull(assignment);
    }
}