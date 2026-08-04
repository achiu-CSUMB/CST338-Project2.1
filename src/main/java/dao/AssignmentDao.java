package dao;

import database.DatabaseManager;
import model.Assignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Author: Oswald Perales
 * Date: 8/3/2026
 * Description: Handles data for assignments
 */
public class AssignmentDao {

    private final Connection connection;

    public AssignmentDao() {
        connection = DatabaseManager.getInstance().getConnection();
    }

    public AssignmentDao(Connection connection) {
        this.connection = connection;
    }

    // New assignment insert
    public boolean insert(Assignment assignment) {
        String sql = """
                INSERT INTO assignments
                (course_id, title, description, due_date, max_points)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (PreparedStatement statement = connection.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, assignment.getCourseId());
            statement.setString(2, assignment.getTitle());
            statement.setString(3, assignment.getDescription());
            statement.setString(4, assignment.getDueDate());
            statement.setDouble(5, assignment.getMaxPoints());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        assignment.setAssignmentId(generatedKeys.getInt(1));
                    }
                }

                return true;
            }

        } catch (SQLException e) {
            System.err.println("Could not insert assignment: "
                    + e.getMessage());
        }

        return false;
    }

    // Finds an assignment by ID
    public Assignment findById(int assignmentId) {
        String sql = """
                SELECT assignment_id, course_id, title,
                       description, due_date, max_points
                FROM assignments
                WHERE assignment_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, assignmentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Assignment(
                            resultSet.getInt("assignment_id"),
                            resultSet.getInt("course_id"),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            resultSet.getString("due_date"),
                            resultSet.getDouble("max_points")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Could not find assignment by ID: "
                    + e.getMessage());
        }

        return null;
    }

    // Updates an assignment
    public boolean update(Assignment assignment) {
        String sql = """
                UPDATE assignments
                SET course_id = ?, title = ?, description = ?,
                    due_date = ?, max_points = ?
                WHERE assignment_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, assignment.getCourseId());
            statement.setString(2, assignment.getTitle());
            statement.setString(3, assignment.getDescription());
            statement.setString(4, assignment.getDueDate());
            statement.setDouble(5, assignment.getMaxPoints());
            statement.setInt(6, assignment.getAssignmentId());

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("Could not update assignment: "
                    + e.getMessage());
        }

        return false;
    }

    // Deletes an assignment
    public boolean delete(int assignmentId) {
        String sql = """
                DELETE FROM assignments
                WHERE assignment_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, assignmentId);

            int rowsDeleted = statement.executeUpdate();

            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.err.println("Could not delete assignment: "
                    + e.getMessage());
        }

        return false;
    }
}