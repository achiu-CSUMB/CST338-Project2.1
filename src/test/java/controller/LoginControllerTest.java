package controller;

import dao.UserDao;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import model.User;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: John Ly
 * Date: 8/12/2026
 * Description:
 */
class LoginControllerTest extends ApplicationTest {

    private Connection connection;

    @Override
    public void start(Stage stage) throws Exception {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
        );

        try (Statement statement = connection.createStatement()) {
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

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/login-view.fxml")
        );

        Scene scene = new Scene(loader.load());
        LoginController controller = loader.getController();

        UserDao userDao = new UserDao(connection);
        controller.setUserDao(userDao);

        User user = new User("john338", "password123","student");

        userDao.insert(user);

        stage.setScene(scene);
        stage.show();
    }

    @Test
    void successfulLogin() {
        clickOn("#usernameField").write("john338");
        clickOn("#passwordField").write("password123");
        clickOn("#loginButton").clickOn();

        Label errorLabel = lookup("#errorLabel").query();
        assertEquals("Login successful.", errorLabel.getText());
    }

    @Test
    void incorrectPassword() {
        clickOn("#usernameField").write("john338");
        clickOn("#passwordField").write("wrongpassword");
        clickOn("#loginButton").clickOn();

        Label errorLabel = lookup("#errorLabel").query();
        assertEquals("Incorrect password.", errorLabel.getText());
    }

    @Test
    void userNotFound() {
        clickOn("#usernameField").write("unknownuser");
        clickOn("#passwordField").write("password123");
        clickOn("#loginButton").clickOn();

        Label errorLabel = lookup("#errorLabel").query();
        assertEquals("User not found", errorLabel.getText());
    }

    @Test
    void blankLoginField() {
        clickOn("#loginButton").clickOn();
        Label errorLabel = lookup("#errorLabel").query();
        assertEquals("Username and password required.", errorLabel.getText());
    }

    @Test
    void createAccountOpensRegistration() {
        clickOn("#createAccountButton").clickOn();

        assertNotNull(lookup("#usernameField").query());
    }
}