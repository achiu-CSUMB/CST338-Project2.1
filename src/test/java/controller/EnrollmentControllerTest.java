package controller;

import dao.CourseDao;
import dao.EnrollmentDao;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Enrollment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import javafx.scene.control.TableView;
import org.testfx.framework.junit5.ApplicationTest;
import service.EnrollmentService;

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
 * Description: Tests model.Enrollment Management JavaFX interface using TestFX.
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
                    CREATE TABLE IF NOT EXISTS courses (
                        course_id INT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(255),                 
                        capacity INT NOT NULL DEFAULT 2,
                        prefix VARCHAR(255),
                        teacher_name VARCHAR(255)
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS enrollments (
                        enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
                        student_id INT NOT NULL,
                        course_id INT NOT NULL,
                        waitlisted BOOLEAN NOT NULL DEFAULT FALSE   
                                             );
            """);

            statement.execute("DELETE FROM enrollments;");
            statement.execute("DELETE FROM courses;");

            statement.execute("""
                    INSERT INTO courses(title, capacity, prefix, teacher_name)
                    VALUES ('Computer Science', 2, 'CST', 'John Ly');
                    """);
        }

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource("/views/enrollment-view.fxml")
                );

        Scene scene = new Scene(loader.load());

        controller = loader.getController();

        EnrollmentDao enrollmentDao = new EnrollmentDao(connection);

        courseDao = new CourseDao(connection);

        enrollmentService = new EnrollmentService(enrollmentDao, courseDao);

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

    @Test
    void thirdStudentArrivesWaitlistedInTable() {
        clickOn("#studentIdField").write("1");

        clickOn("#availableCoursesTable");
        clickOn("Computer Science");

        clickOn("#enrollButton");

        // Second
        doubleClickOn("#studentIdField");
        eraseText(10);
        write("2");

        clickOn("#availableCoursesTable");
        clickOn("Computer Science");

        clickOn("#enrollButton");

        // Third
        doubleClickOn("#studentIdField");
        eraseText(10);
        write("3");

        clickOn("#availableCoursesTable");
        clickOn("Computer Science");

        clickOn("#enrollButton");

        TableView<Enrollment> enrollmentTable = lookup("#enrollmentTable").query();

        assertEquals(3, enrollmentTable.getItems().size());

        Enrollment first = enrollmentTable.getItems().get(0);

        Enrollment second = enrollmentTable.getItems().get(1);

        Enrollment third = enrollmentTable.getItems().get(2);

        Assertions.assertEquals(1, first.getStudentId());

        Assertions.assertFalse(first.isWaitlisted());

        Assertions.assertEquals(2, second.getStudentId());

        Assertions.assertFalse(second.isWaitlisted());

        Assertions.assertEquals(3, third.getStudentId());

        Assertions.assertTrue(third.isWaitlisted());

    }

    @Test
    void droppingStudentPromotesWaitlistedStudent() {
        // First
        clickOn("#studentIdField").write("1");
        clickOn("#availableCoursesTable");
        clickOn("Computer Science");
        clickOn("#enrollButton");

        // Second
        doubleClickOn("#studentIdField");
        eraseText(10);
        write("2");
        clickOn("#availableCoursesTable");
        clickOn("Computer Science");
        clickOn("#enrollButton");

        // Third
        doubleClickOn("#studentIdField");
        eraseText(10);
        write("3");
        clickOn("#availableCoursesTable");
        clickOn("Computer Science");
        clickOn("#enrollButton");

        TableView<Enrollment> enrollmentTable = lookup("#enrollmentTable").query();

        assertEquals(3, enrollmentTable.getItems().size());

        Enrollment third = enrollmentTable.getItems().get(2);

        Assertions.assertTrue(third.isWaitlisted());

        enrollmentTable.getSelectionModel().select(0);

        clickOn("#dropButton");

        enrollmentTable = lookup("#enrollmentTable").query();

        assertEquals(2, enrollmentTable.getItems().size());

        Enrollment promoted = null;

        for (Enrollment enrollment : enrollmentTable.getItems()) {
            if (enrollment.getStudentId() == 3) {
                promoted = enrollment;
                break;
            }
        }
        assertNotNull(promoted);
        Assertions.assertFalse(promoted.isWaitlisted());
    }
}
