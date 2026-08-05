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
 * Description: Handles CRUD operations for student enrollments. Provides methods for managing the enrollments, retrieving students in courses...
 * ....retrieving a student's courses, and checking for duplicate enrollment.
 */
public class EnrollmentDao {
    private Connection connection;

    /**
     * Creates an EnrollmentDao object.
     */
    public EnrollmentDao() {
        connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Creates a EnrollmentDao object using the connection.
     * Used for testing database.
     */
    public EnrollmentDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserts a new enrollment.
     */
    public boolean insert(Enrollment enrollment) {
        String sql = """
                INSERT INTO enrollments (student_id, course_id, waitlisted)
                VALUES (?,?,?);
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, enrollment.getStudentId());
            statement.setInt(2, enrollment.getCourseId());
            statement.setBoolean(3, enrollment.isWaitlisted());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        enrollment.setEnrollmentId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Could not insert enrollment: " + e.getMessage());
        }

        return false;
    }

    /**
     * Finds an enrollment by its ID.
     */
    public Enrollment findById(int enrollmentId) {
        String sql = """
                SELECT enrollment_id, student_id, course_id, waitlisted
                FROM enrollments
                WHERE enrollment_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, enrollmentId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {

                    Enrollment enrollment = new Enrollment(
                            result.getInt("enrollment_id"),
                            result.getInt("student_id"),
                            result.getInt("course_id")
                    );

                    enrollment.setWaitlisted(
                            result.getBoolean("waitlisted")
                    );
                    return enrollment;
                }
            }
        } catch (SQLException e) {
            System.err.println("Could not find enrollment: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates an existing enrollment.
     */
    public boolean update(Enrollment enrollment) {
        String sql = """
                UPDATE enrollments
                SET student_id = ?, course_id = ?, waitlisted = ?
                WHERE enrollment_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, enrollment.getStudentId());
            statement.setInt(2, enrollment.getCourseId());
            statement.setBoolean(3, enrollment.isWaitlisted());
            statement.setInt(4, enrollment.getEnrollmentId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Could not update enrollment: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes an enrollment.
     */
    public boolean delete(int enrollmentId) {
        String sql = """
                DELETE FROM enrollments
                WHERE enrollment_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, enrollmentId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Could not delete enrollment: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves every enrollment.
     */
    public ArrayList<Enrollment> getAllEnrollments() {
        ArrayList<Enrollment> enrollments = new ArrayList<>();

        String sql = """
                SELECT enrollment_id, student_id, course_id, waitlisted
                FROM enrollments;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                Enrollment enrollment = new Enrollment(
                        result.getInt("enrollment_id"),
                        result.getInt("student_id"),
                        result.getInt("course_id")
                );

                enrollment.setWaitlisted(
                        result.getBoolean("waitlisted")
                );

                enrollments.add(enrollment);
            }
        } catch (SQLException e) {
            System.err.println("Could not retrieve enrollments: " + e.getMessage());
        }
        return enrollments;
    }

    /**
     * Retrieves every enrollment for a student.
     */
    public ArrayList<Enrollment> getStudentEnrollments(int studentId) {
        ArrayList<Enrollment> enrollments = new ArrayList<>();

        String sql = """
                
                SELECT enrollment_id, student_id, course_id, waitlisted
                FROM enrollments
                WHERE student_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Enrollment enrollment = new Enrollment(
                            result.getInt("enrollment_id"),
                            result.getInt("student_id"),
                            result.getInt("course_id")
                    );
                    enrollment.setWaitlisted(
                            result.getBoolean("waitlisted")
                    );
                    enrollments.add(enrollment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Could not retrieve student enrollments: " + e.getMessage());
        }
        return enrollments;
    }

    /**
     * Retrieves every enrollment for a course.
     */
    public ArrayList<Enrollment> getCourseEnrollments(int courseId) {
        ArrayList<Enrollment> enrollments = new ArrayList<>();

        String sql = """
                
                SELECT enrollment_id, student_id, course_id, waitlisted
                FROM enrollments
                WHERE course_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Enrollment enrollment = new Enrollment(
                            result.getInt("enrollment_id"),
                            result.getInt("student_id"),
                            result.getInt("course_id")
                    );

                    enrollment.setWaitlisted(
                            result.getBoolean("waitlisted")
                    );
                    enrollments.add(enrollment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Could not retrieve course enrollments: " + e.getMessage());
        }
        return enrollments;
    }

    /**
     * Checks whether a student is already enrolled in a course or not.
     */
    public boolean isStudentEnrolled(int studentId, int courseId) {
        String sql = """
                
                SELECT *
                FROM enrollments
                WHERE student_id = ?
                AND course_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        } catch (SQLException e) {
            System.err.println("Could not check enrollment: " +  e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves every waitlisted student for a course.
     */
    public ArrayList<Enrollment> getWaitlistedStudents(int courseId) {
        ArrayList<Enrollment> enrollments = new ArrayList<>();

        String sql = """
                SELECT enrollment_id, student_id, course_id, waitlisted
                FROM enrollments
                WHERE course_id = ?
                AND waitlisted = TRUE;
                """;
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Enrollment enrollment = new Enrollment(
                            result.getInt("enrollment_id"),
                            result.getInt("student_id"),
                            result.getInt("course_id")
                    );

                    enrollment.setWaitlisted(
                            result.getBoolean("waitlisted")
                    );

                    enrollments.add(enrollment);
                }
            }
        } catch (SQLException e) {
            System.err.println(
                    "Could not retrieve waitlisted students: " + e.getMessage()
            );
        }
        return enrollments;
    }
}
