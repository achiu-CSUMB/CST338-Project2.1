package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Author: John Ly
 * Date: 8/1/2026
 * Description: Manages app's SQLite database connection and creates user table
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:grade_tracker.db";
    private static DatabaseManager instance;

    private Connection connection;

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);

            // Review together and make sure this enables foreign keys for later.
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON;");
            }

            createTables();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void createTables() {
        String userSql = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    prefix TEXT,
                    teacher_name TEXT
                );
                """;

        String courseSql = """
                CREATE TABLE IF NOT EXISTS courses (
                    course_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    teacher_id INTEGER NOT NULL,
                    FOREIGN KEY (teacher_id) REFERENCES users(user_id)
            );
        """;

        String enrollmentSql = """
                CREATE TABLE IF NOT EXISTS enrollments (
                    enrollment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id INTEGER NOT NULL,
                    course_id INTEGER NOT NULL,
                    waitlisted BOOLEAN NOT NULL DEFAULT FALSE,
                    FOREIGN KEY (student_id) REFERENCES users(user_id),
                    FOREIGN KEY (course_id) REFERENCES courses(course_id)
            );
        """;

        String assignmentSql = """
                CREATE TABLE IF NOT EXISTS assignments (
                    assignment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    course_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT,
                    due_date TEXT NOT NULL,
                    max_points REAL NOT NULL
                );
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(userSql);
            statement.execute(courseSql);
            statement.execute(enrollmentSql);
            statement.execute(assignmentSql);
        } catch (SQLException e) {
            System.err.println("Could not create database table: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Could not close database: " + e.getMessage());
        }

        instance = null;
    }
}