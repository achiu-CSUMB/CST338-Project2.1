package controller;

import dao.UserDao;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import model.User;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: John Ly
 * Date: 8/12/2026
 * Description:
 */
class AccountsControllerTest extends ApplicationTest {

    private Connection connection;
    private UserDao userDao;

    @Override
    public void start(Stage stage) throws Exception {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:accountstest;DB_CLOSE_DELAY=-1"
        );

        try(Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS users");

            statement.execute("""
                    CREATE TABLE users (
                        user_id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(255) NOT NULL
                    );
                    """);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/views/accounts-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load());

        AccountsController controller = fxmlLoader.getController();

        userDao = new UserDao(connection);
        controller.setUserDao(userDao);

        stage.setScene(scene);
        stage.show();
    }

    @Test
    void loadExistingUser() {
        userDao.insert(new User("john338", "password123", "student"));

        clickOn("#usernameField").write("john338");
        clickOn("#loadUserButton").clickOn();

        Label usernameLabel = lookup("#statusLabel").query();
        assertEquals("User loaded", usernameLabel.getText());
    }

    @Test
    void updateUserRole() {
        userDao.insert(new User("john338", "password123", "student"));
        clickOn("#usernameField").write("john338");
        clickOn("#loadUserButton");

        ComboBox<String> roleComboBox = lookup("#roleComboBox").query();
        interact(() -> roleComboBox.setValue("admin"));

        clickOn("#updateUserButton");

        Label statusLabel = lookup("#statusLabel").query();
        assertEquals("User updated successfully.", statusLabel.getText());

        User updatedUser = userDao.findByUsername("john338");
        assertEquals("admin", updatedUser.getRole());
    }

    @Test
    void resetPassword() {
        userDao.insert(new User("john338", "password123", "student"));

        clickOn("#usernameField").write("john338");
        clickOn("#loadUserButton");

        clickOn("#passwordField").write("newpassword123");
        clickOn("#resetPasswordButton");

        Label statusLabel = lookup("#statusLabel").query();
        assertEquals("Password reset successfully.", statusLabel.getText());

        User updatedUser = userDao.findByUsername("john338");
        assertEquals("newpassword123", updatedUser.getPassword());
    }

    @Test
    void deleteUser() {
        userDao.insert(new User("john338", "password123", "student"));
        clickOn("#usernameField").write("john338");
        clickOn("#loadUserButton");

        clickOn("#deleteUserButton");
        Label statusLabel = lookup("#statusLabel").query();
        assertEquals("User deleted successfully.", statusLabel.getText());

        User deleteUser = userDao.findByUsername("john338");
        assertNull(deleteUser);
    }

    @Test
    void resetPasswordWithoutLoadingUser() {
        clickOn("#passwordField").write("newpassword123");
        clickOn("#resetPasswordButton");

        Label statusLabel = lookup("#statusLabel").query();
        assertEquals("Load a user first.", statusLabel.getText());
    }

    @Test
    void changeUsername() {
        userDao.insert(new User("john338", "password123", "student"));

        //Load existing user
        clickOn("#usernameField").write("john338");
        clickOn("#loadUserButton");

        clickOn("#newUsernameField").write("newUser");

        clickOn("#changeUsernameButton");

        Label statusLabel = lookup("#statusLabel").query();
        assertEquals("Username changed successfully.", statusLabel.getText());

        User oldUser = userDao.findByUsername("john338");
        assertEquals(oldUser);

        User updatedUser = userDao.findByUsername("newUser");
        assertEquals(updatedUser);
        assertEquals("newUser", updatedUser.getUsername());
    }
}