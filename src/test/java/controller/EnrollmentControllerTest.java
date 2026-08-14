package controller;

import dao.CourseDao;
import dao.EnrollmentDao;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Enrollment;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import javafx.scene.control.TableView;
import org.testfx.framework.junit5.ApplicationTest;
import service.EnrollmentService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

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
    private Connection connection;
    private User studentOne;
    private User studentTwo;
    private User studentThree;

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
                    VALUES ('Computer Science', 2, 'Dr.', 'John Ly');
                    """);
        }

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource("/views/enrollment-view.fxml")
                );

        Scene scene = new Scene(loader.load());

        controller = loader.getController();

        studentOne = new User(1, "student1", "password","STUDENT", null, null);

        studentTwo = new User(2, "student2", "password","STUDENT", null, null);

        studentThree = new User(3, "student3", "password","STUDENT", null, null);

        controller.setCurrentUser(studentOne);

        EnrollmentDao enrollmentDao = new EnrollmentDao(connection);

        courseDao = new CourseDao(connection);

        enrollmentService = new EnrollmentService(enrollmentDao, courseDao);

        controller.setEnrollmentService(enrollmentService);
        controller.setCourseDao(courseDao);

        controller.loadCourses();
        controller.loadEnrollments();

        stage.setScene(scene);
        stage.show();
}

@Test
void enrollButtonAddsEnrollment() {
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

        Enrollment enrollment = enrollmentTable.getItems().get(0);

        assertEquals(1, enrollment.getStudentId());

        assertEquals(1, enrollment.getCourseId());
    }

    // AI Drafted Test (I didn't know we could just make tests with the AI, so I guess I'm only doing two)
    // Model: ChatGPT
    // Prompt: Based on these rubrics, guidelines, and specific slice... and my current tests, what would be some good JUnit or TextFX tests for this application.
    // Accepted (confirms UI filtering works + proves Student 1 cannot see Student 2's enrollments; I have no issue with the generated test)
    @Test
    void studentOnlySeesTheirOwnEnrollments() {

        // Student one enrolls
        controller.setCurrentUser(studentOne);

        clickOn("#availableCoursesTable");
        clickOn("Computer Science");
        clickOn("#enrollButton");


        // Student two enrolls
        controller.setCurrentUser(studentTwo);

        clickOn("#availableCoursesTable");
        clickOn("Computer Science");
        clickOn("#enrollButton");


        // Reload table as student one
        controller.setCurrentUser(studentOne);
        controller.loadEnrollments();


        TableView<Enrollment> table =
                lookup("#enrollmentTable").query();


        assertEquals(1, table.getItems().size());

        assertEquals(
                1,
                table.getItems().get(0).getStudentId()
        );
    }


    @Test
    void thirdStudentArrivesWaitlistedInTable() {
        controller.setCurrentUser(studentOne);

        clickOn("#availableCoursesTable");
        clickOn("Computer Science");
        clickOn("#enrollButton");

        // Second
        controller.setCurrentUser(studentTwo);
        clickOn("#availableCoursesTable");
        clickOn("Computer Science");
        clickOn("#enrollButton");

        // Third
        controller.setCurrentUser(studentThree);
        clickOn("#availableCoursesTable");
        clickOn("Computer Science");
        clickOn("#enrollButton");

        // Switches back to see all database enrollments.
        ArrayList<Enrollment> enrollments = enrollmentService.getAllEnrollments();

        assertEquals(3, enrollments.size());

        Enrollment first = enrollments.get(0);
        Enrollment second = enrollments.get(1);
        Enrollment third = enrollments.get(2);

        assertEquals(1, first.getStudentId());
        assertFalse(first.isWaitlisted());

        assertEquals(2, second.getStudentId());
        assertFalse(second.isWaitlisted());

        assertEquals(3, third.getStudentId());
        assertTrue(third.isWaitlisted());

    }

    @Test
    void droppingStudentPromotesWaitlistedStudent() {
        controller.setCurrentUser(studentOne);

        clickOn("#availableCoursesTable");
        clickOn("Computer Science");
        clickOn("#enrollButton");

        // Second
        controller.setCurrentUser(studentTwo);
        clickOn("#availableCoursesTable");
        clickOn("Computer Science");
        clickOn("#enrollButton");

        // Third
        controller.setCurrentUser(studentThree);
        clickOn("#availableCoursesTable");
        clickOn("Computer Science");
        clickOn("#enrollButton");

        ArrayList<Enrollment> enrollments = enrollmentService.getAllEnrollments();

        assertEquals(3, enrollments.size());

        TableView<Enrollment> enrollmentTable = lookup("#enrollmentTable").query();

        Enrollment third = enrollmentTable.getItems().get(0);

        assertTrue(third.isWaitlisted());

        // Drop first student enrollment
        controller.setCurrentUser(studentOne);

        waitForFxEvents();

        enrollmentTable = lookup("#enrollmentTable").query();

        enrollmentTable.getSelectionModel().select(0);

        clickOn("#dropButton");

        ArrayList<Enrollment> updatedEnrollments = enrollmentService.getAllEnrollments();

        assertEquals(2, updatedEnrollments.size());

        Enrollment promoted = null;

        for (Enrollment enrollment : updatedEnrollments) {
            if (enrollment.getStudentId() == 3) {
                promoted = enrollment;
                break;
            }
        }
        assertNotNull(promoted);
        assertFalse(promoted.isWaitlisted());
    }
}
