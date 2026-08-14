package dao;

import model.Grade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for GradeDao. Runs against a fresh in-memory SQLite
 * database (created and torn down per test) instead of the shared
 * application database, using the GradeDao(Connection) constructor added
 * for testing.
 */
class GradeDaoTest {

    private Connection connection;
    private GradeDao gradeDao;

    // IDs of rows seeded before each test.
    private static final int TEACHER_ID = 1;
    private static final int STUDENT_ID = 2;
    private static final int OTHER_STUDENT_ID = 3;
    private static final int COURSE_ID = 100;
    private static final int ASSIGNMENT_ID = 200;
    private static final int OTHER_ASSIGNMENT_ID = 201;

    @BeforeEach
    void setUp() throws SQLException {
        // A fresh in-memory database per test, isolated from any other
        // test and from the real grade_tracker.db file.
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON;");

            statement.execute("""
                    CREATE TABLE users (
                        user_id INTEGER PRIMARY KEY,
                        username TEXT NOT NULL UNIQUE,
                        password TEXT NOT NULL,
                        role TEXT NOT NULL,
                        prefix TEXT,
                        teacher_name TEXT
                    );
                    """);

            statement.execute("""
                    CREATE TABLE courses (
                        course_id INTEGER PRIMARY KEY,
                        title TEXT NOT NULL,
                        capacity INTEGER NOT NULL DEFAULT 2,
                        prefix TEXT,
                        teacher_name TEXT
                    );
                    """);

            statement.execute("""
                    CREATE TABLE assignments (
                        assignment_id INTEGER PRIMARY KEY,
                        course_id INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT,
                        due_date TEXT NOT NULL,
                        max_points REAL NOT NULL
                    );
                    """);

            statement.execute("""
                    CREATE TABLE grades (
                        grade_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        student_id INTEGER NOT NULL,
                        course_id INTEGER NOT NULL,
                        assignment_id INTEGER,
                        score REAL NOT NULL DEFAULT 0,
                        entry_date TEXT NOT NULL,
                        FOREIGN KEY(course_id) REFERENCES courses(course_id),
                        FOREIGN KEY(student_id) REFERENCES users(user_id),
                        FOREIGN KEY(assignment_id) REFERENCES assignments(assignment_id)
                    );
                    """);

            statement.executeUpdate("""
                    INSERT INTO users (user_id, username, password, role)
                    VALUES (%d, 'ms_frizzle', 'pw', 'TEACHER');
                    """.formatted(TEACHER_ID));

            statement.executeUpdate("""
                    INSERT INTO users (user_id, username, password, role)
                    VALUES (%d, 'arnold', 'pw', 'STUDENT');
                    """.formatted(STUDENT_ID));

            statement.executeUpdate("""
                    INSERT INTO users (user_id, username, password, role)
                    VALUES (%d, 'dorothy_ann', 'pw', 'STUDENT');
                    """.formatted(OTHER_STUDENT_ID));

            statement.executeUpdate("""
                    INSERT INTO courses (course_id, title, capacity)
                    VALUES (%d, 'Earth Science', 30);
                    """.formatted(COURSE_ID));

            statement.executeUpdate("""
                    INSERT INTO assignments (assignment_id, course_id, title, due_date, max_points)
                    VALUES (%d, %d, 'Rock Cycle Lab', '2026-09-01', 100);
                    """.formatted(ASSIGNMENT_ID, COURSE_ID));

            statement.executeUpdate("""
                    INSERT INTO assignments (assignment_id, course_id, title, due_date, max_points)
                    VALUES (%d, %d, 'Volcano Diagram', '2026-09-08', 50);
                    """.formatted(OTHER_ASSIGNMENT_ID, COURSE_ID));
        }

        gradeDao = new GradeDao(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void insert_thenFind_returnsTheSameGrade() throws SQLException {
        Grade grade = new Grade(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(ASSIGNMENT_ID), 92.5);

        gradeDao.insert(grade);

        Grade found = gradeDao.find(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(ASSIGNMENT_ID));

        assertNotNull(found);
        assertEquals(92.5, found.getScore());
        assertEquals(String.valueOf(COURSE_ID), found.getCourseId());
        assertEquals(String.valueOf(STUDENT_ID), found.getStudentId());
        assertEquals(String.valueOf(ASSIGNMENT_ID), found.getAssignmentId());
    }

    @Test
    void find_scopedToAssignment_returnsNullForDifferentAssignment() throws SQLException {
        Grade grade = new Grade(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(ASSIGNMENT_ID), 80.0);
        gradeDao.insert(grade);

        Grade found = gradeDao.find(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(OTHER_ASSIGNMENT_ID));

        assertNull(found);
    }

    @Test
    void find_returnsNull_whenNoMatchingGradeExists() throws SQLException {
        Grade found = gradeDao.find(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(ASSIGNMENT_ID));

        assertNull(found);
    }

    @Test
    void update_changesScoreAndEntryDate() throws SQLException {
        Grade grade = new Grade(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(ASSIGNMENT_ID), 70.0);
        gradeDao.insert(grade);

        grade.setScore(88.0);
        grade.setDate(LocalDate.of(2026, 9, 5));
        gradeDao.update(grade);

        Grade updated = gradeDao.find(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(ASSIGNMENT_ID));

        assertNotNull(updated);
        assertEquals(88.0, updated.getScore());
        assertEquals(LocalDate.of(2026, 9, 5), updated.getDate());
    }

    @Test
    void findByCourseIdAndAssignmentId_returnsOnlyGradesForThatAssignment() throws SQLException {
        gradeDao.insert(new Grade(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(ASSIGNMENT_ID), 90.0));
        gradeDao.insert(new Grade(String.valueOf(COURSE_ID), String.valueOf(OTHER_STUDENT_ID),
                String.valueOf(ASSIGNMENT_ID), 75.0));
        gradeDao.insert(new Grade(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(OTHER_ASSIGNMENT_ID), 60.0));

        List<Grade> results = gradeDao.findByCourseIdAndAssignmentId(
                String.valueOf(COURSE_ID), String.valueOf(ASSIGNMENT_ID));

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(g -> g.getAssignmentId().equals(String.valueOf(ASSIGNMENT_ID))));
    }

    @Test
    void findByCourseIdAndStudentId_returnsAllAssignmentsForThatStudent() throws SQLException {
        gradeDao.insert(new Grade(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(ASSIGNMENT_ID), 90.0));
        gradeDao.insert(new Grade(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(OTHER_ASSIGNMENT_ID), 60.0));
        gradeDao.insert(new Grade(String.valueOf(COURSE_ID), String.valueOf(OTHER_STUDENT_ID),
                String.valueOf(ASSIGNMENT_ID), 75.0));

        List<Grade> results = gradeDao.findByCourseIdAndStudentId(
                String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID));

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(g -> g.getStudentId().equals(String.valueOf(STUDENT_ID))));
    }

    @Test
    void mapRow_populatesStudentNameAndAssignmentDetails() throws SQLException {
        gradeDao.insert(new Grade(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(ASSIGNMENT_ID), 95.0));

        Grade found = gradeDao.find(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(ASSIGNMENT_ID));

        assertNotNull(found);
        assertEquals("arnold", found.getStudentName());
        assertEquals("Rock Cycle Lab", found.getAssignmentTitle());
        assertEquals(100.0, found.getMaxPoints());
    }

    @Test
    void delete_removesTheGrade() throws SQLException {
        gradeDao.insert(new Grade(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(ASSIGNMENT_ID), 90.0));

        gradeDao.delete(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID));

        List<Grade> remaining = gradeDao.findByCourseId(String.valueOf(COURSE_ID));
        assertTrue(remaining.isEmpty());
    }

    @Test
    void entryDate_roundTripsThroughStringStorage() throws SQLException {
        // Regression test: entry_date is stored/read as a plain ISO string
        // (see GradeDao.insert/update/mapRow) rather than via
        // PreparedStatement#setDate / ResultSet#getDate, which previously
        // caused a "Error parsing time stamp" SQLException when reading
        // grades back for the student view.
        Grade grade = new Grade(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(ASSIGNMENT_ID), 100.0);
        grade.setDate(LocalDate.of(2026, 8, 14));

        gradeDao.insert(grade);

        Grade found = gradeDao.find(String.valueOf(COURSE_ID), String.valueOf(STUDENT_ID),
                String.valueOf(ASSIGNMENT_ID));

        assertNotNull(found);
        assertEquals(LocalDate.of(2026, 8, 14), found.getDate());
    }
}
