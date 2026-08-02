package dao;

import database.DatabaseManager;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Author: John Ly
 * Date: 8/1/2026
 * Description: Handles CRUD operations for users in database
 */
public class UserDao {

    private final Connection connection;

    public UserDao() {
        connection = DatabaseManager.getInstance().getConnection();
    }

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    //Creates and insert users
    public boolean insert(User user) {
        String sql = """
                INSERT INTO users (username, password, role)
                VALUES (?, ?, ?);
                """;
        try (PreparedStatement statement = connection.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }

                return true;
            }
        } catch (SQLException e) {
            System.err.println("Could not insert user: " + e.getMessage());
        }

        return false;
    }


    // returns existing users or null if user ID doesnt exist
    public User findById(int userId) {
        String sql = """
                SELECT user_id, username, password, role
                FROM users
                WHERE user_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("role")
                    );
                }

            }
        } catch (SQLException e) {
            System.err.println("Could not find user by ID: " + e.getMessage());
        }

        return null;
    }

    //Same as above but for usernames
    public User findByUsername(String username) {
        String sql = """
                SELECT user_id, username, password, role
                FROM users
                WHERE username = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("role")
                    );
                }

            }
        } catch (SQLException e) {
            System.err.println("Could not find user by username: " + e.getMessage());
        }

        return null;
    }

    //Updates users ID row
    public boolean update(User user) {
        String sql = """
                UPDATE users
                SET username = ?, password = ?, role = ?
                WHERE user_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.setInt(4, user.getUserId());

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("Could not update user: " + e.getMessage());
        }

        return false;
    }

    //Deletes users ID
    public boolean delete(int userId) {
        String sql = """
                DELETE FROM users
                WHERE user_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.err.println("Could not delete user: " + e.getMessage());
        }

        return false;
    }

}
