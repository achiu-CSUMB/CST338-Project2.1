package controller;

import dao.UserDao;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
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
 * Description: Tests account registration using TestFX and an in-memory H2 database.
 * Verifies successful registration, duplicate usernames, password mismatch,
 * and required-field validation.
 * AI-assisted: ChatGPT helped draft portions of the registration test cases,
 * which were reviewed, corrected, and curated by the author.
 */
class RegisterControllerTest extends ApplicationTest {

    private Connection connection;
    private UserDao userDao;


    @Override
    public void start(Stage stage) throws Exception {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
        );

        try(Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS users");

            statement.execute("""
                    CREATE TABLE users (
                        user_id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(255) NOT NULL,
                        prefix VARCHAR(50),
                        teacher_name VARCHAR(255)
                    );
                    """);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/views/register-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load());

        RegisterController controller = fxmlLoader.getController();

        userDao = new UserDao(connection);
        controller.setUserDao(userDao);

        stage.setScene(scene);
        stage.show();
    }

    @Test
    void successfulRegistration() {
        clickOn("#usernameField").write("newuser");
        clickOn("#passwordField").write("password123");
        clickOn("#confirmPasswordField").write("password123");

        ComboBox<String> roleComboBox = (ComboBox<String>) lookup("#roleComboBox").query();
        interact(() -> roleComboBox.setValue("Student"));
        clickOn("#registerButton");

        Label errorLabel = lookup("#errorLabel").query();

        assertEquals("Account Created Successfully! Please log in.",
                errorLabel.getText()
        );

        User createduser = userDao.findByUsername("newuser");
        assertNotNull(createduser);
        assertEquals("newuser", createduser.getUsername());
        assertEquals("Student", createduser.getRole());
    }
    @Test
    void passwordMismatch() {
        clickOn("#usernameField").write("newuser");
        clickOn("#passwordField").write("password123");
        clickOn("#confirmPasswordField").write("different123");

        ComboBox<String> roleComboBox = lookup("#roleComboBox").query();
        interact(() -> roleComboBox.setValue("Student"));

        clickOn("#registerButton");

        Label errorLabel = lookup("#errorLabel").query();

        assertEquals("Passwords do not match.", errorLabel.getText());
    }

    @Test
    void blankRequiredFields() {
        clickOn("#registerButton");

        Label errorLabel = lookup("#errorLabel").query();

        assertEquals("All fields are required.", errorLabel.getText());
    }

    @Test
    void duplicateUsername() {
        userDao = new UserDao(connection);
        userDao.insert(new User("john338", "password123", "Student"));

        clickOn("#usernameField").write("john338");
        clickOn("#passwordField").write("password123");
        clickOn("#confirmPasswordField").write("password123");

        ComboBox<String> roleComboBox = lookup("#roleComboBox").query();
        interact(() -> roleComboBox.setValue("Student"));

        clickOn("#registerButton");

        Label errorLabel = lookup("#errorLabel").query();

        assertEquals("Username already exists.", errorLabel.getText());
    }

    @Test
    void successfulTeacherRegistration() {
        clickOn("#usernameField").write("teacher1");
        clickOn("#passwordField").write("password123");
        clickOn("#confirmPasswordField").write("password123");

        ComboBox<String> roleComboBox = lookup("#roleComboBox").query();
        interact(() -> roleComboBox.setValue("Teacher"));

        ComboBox<String> prefixComboBox = lookup("#prefixComboBox").query();
        interact(() -> prefixComboBox.setValue("Prof."));

        clickOn("#teacherNameField").write("Test Teacher");

        clickOn("#registerButton");

        Label errorLabel = lookup("#errorLabel").query();

        assertEquals("Account Created Successfully! Please log in.",
                errorLabel.getText()
        );

        User createdUser = userDao.findByUsername("teacher1");
        assertNotNull(createdUser);
        assertEquals("teacher1", createdUser.getUsername());
        assertEquals("Teacher", createdUser.getRole());
        assertEquals("Prof.", createdUser.getPrefix());
        assertEquals("Test Teacher", createdUser.getTeacherName());
    }
}