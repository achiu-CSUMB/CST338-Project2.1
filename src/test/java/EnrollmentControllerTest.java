import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.scene.control.TableView;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dominic Casoli
 * <br>
 * created: 8/10/2026
 * @since '1.0-SNAPSHOT'
 * Description: Tests Enrollment Management JavaFX interface using TestFX.
 * Checks that students enter an ID, select a course, and can enroll successfully.
 */

public class EnrollmentControllerTest extends ApplicationTest {
    private EnrollmentController controller;
    private EnrollmentService enrollmentService;
    private CourseDao courseDao;
    private Stage stage;
    private Connection connection;

    @Override
    public void start(Stage stage) throws Exception {

        connection = DriverManager.getConnection("jdbc:h2:mem:enrollmentcontroller;DB_CLOSE_DELAY=-1");

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE courses (
                        course_id INT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(255),
                        teacher_id INT
                    );
                    """);

            statement.execute("""
                    CREATE TABLE enrollments (
                        enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
                        student_id INT NOT NULL,
                        course_id INT NOT NULL,
                        waitlisted BOOLEAN NOT NULL DEFAULT FALSE   
                                             );
            """);

            statement.execute("""
                    INSERT INTO courses(title, teacher_id)
                    VALUES ('Computer Science', 1);
                    """);
        }

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource("/views/enrollment-view.fxml")
                );

        Scene scene = new Scene(loader.load());

        controller = loader.getController();

        EnrollmentDao enrollmentDao = new EnrollmentDao(connection);

        enrollmentService = new EnrollmentService(enrollmentDao);

        courseDao = new CourseDao(connection);

        controller.setEnrollmentService(enrollmentService);
        controller.setCourseDao(courseDao);

        controller.loadEnrollments();
        controller.loadCourses();

        stage.setScene(scene);
        stage.show();
}

@Test
void enrollButtonAddsEnrollment() {
        clickOn("#studentIdField")
                .write("1");

        clickOn("#availableCoursesTable");

        clickOn("Computer Science");

        clickOn("#enrollButton");

        TableView<Enrollment> enrollmentTable =
                lookup("#enrollmentTable")
                        .query();

        assertEquals(
                1,
                enrollmentTable.getItems().size()
        );
    }
}
