import database.DatabaseManager;
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
 * Description: Handles CRUD operations for the Course objects in the SQLite database.
 * Provides methods to create, retrieve, update, delete, and list courses.
 */
public class CourseDao {

    // Stores the database connection.
    private Connection connection;

    /**
     * Creates a CourseDao object.
     */
    public CourseDao() {
        connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Inserts a course to the database.
     */
    public boolean insert(Course course) {
        // TODO: Insert the course into the database.
        String sql = """
                INSERT INTO courses (title, teacher_id)
                VALUES (?, ?);
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, course.getCourseName());
            statement.setInt(2, course.getTeacherId());

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
        // TODO: Retrieve course via the given ID.

        return null;
    }

    /**
     * Updates an existing course.
     */
    public boolean update(Course course) {
        // TODO: Update the selected course.

        return false;
    }

    /**
     * Deletes a course.
     */
    public boolean delete(int courseId) {
        // TODO: Delete the selected course.

        return false;
    }

    /**
     * Returns all the courses.
     */
    public ArrayList<Course> getAllCourses() {
        // TODO: Retrieve every course from database.
        return null;

    }
}
