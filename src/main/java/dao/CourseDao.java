package dao;

import database.DatabaseManager;
import model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * @author Dominic Casoli
 * <br>
 * created: 8/2/2026
 * @since '1.0-SNAPSHOT'
 * Description: Handles CRUD operations for the model.Course objects in the SQLite database.
 * Provides methods to create, retrieve, update, delete, and list courses.
 */
public class CourseDao {

    // Stores the database connection.
    private Connection connection;

    /**
     * Creates a dao.CourseDao object.
     */
    public CourseDao() {
        connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Creates a dao.CourseDao object using the connection.
     * Used for testing database.
     */
    public CourseDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserts a course to the database.
     */
    public boolean insert(Course course) {
        String sql = """
                INSERT INTO courses (title, capacity, prefix, teacher_name)
                VALUES (?, ?, ?, ?);
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, course.getCourseName());
            statement.setInt(2, course.getCapacity());
            statement.setString(3, course.getPrefix());
            statement.setString(4, course.getTeacherName());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        course.setCourseId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Could not insert course: " + e.getMessage());
        }
        return false;
    }

    /**
     * Finds a course through its ID.
     */
    public Course findById(int courseId) {
        String sql = """
                SELECT course_id, title, capacity, prefix, teacher_name
                FROM courses
                WHERE course_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Course(
                            resultSet.getInt("course_id"),
                            resultSet.getString("title"),
                            resultSet.getInt("capacity"),
                            resultSet.getString("prefix"),
                            resultSet.getString("teacher_name")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Could not find course: " + e.getMessage());
        }

        return null;
    }

    /**
     * Updates an existing course.
     */
    public boolean update(Course course) {
        String sql = """
                UPDATE courses
                SET title = ?, capacity = ?, prefix = ?, teacher_name = ?
                WHERE course_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, course.getCourseName());
            statement.setInt(2, course.getCapacity());
            statement.setString(3, course.getPrefix());
            statement.setString(4, course.getTeacherName());
            statement.setInt(5, course.getCourseId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Could not update course: " + e.getMessage());
        }

        return false;
    }

    /**
     * Deletes a course.
     */
    public boolean delete(int courseId) {
        String sql = """
                DELETE FROM courses
                WHERE course_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Could not delete course: " + e.getMessage());
        }

        return false;
    }

    /**
     * Returns all the courses.
     */
    public ArrayList<Course> getAllCourses() {
        ArrayList<Course> courses = new ArrayList<>();

        String sql = """
                SELECT *
                FROM courses;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                courses.add(
                        new Course(
                                result.getInt("course_id"),
                                result.getString("title"),
                                result.getInt("capacity"),
                                result.getString("prefix"),
                                result.getString("teacher_name")
                        )
                );
            }
        } catch (SQLException e) {
            System.err.println("Could not retrieve courses: " + e.getMessage());
        }
        return courses;

    }
}
