package controller;

import dao.CourseDao;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.Course;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import service.CourseService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Dominic Casoli
 * <br>
 * created: 8/12/2026
 * @since '1.0-SNAPSHOT'
 * Description:
 */

public class CoursesControllerTest extends ApplicationTest {

    private Connection connection;
    private CoursesController controller;
    private User testTeacher;

    @Override
    public void start(Stage stage) throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:coursescontroller;DB_CLOSE_DELAY=1");

        try(Statement statement = connection.createStatement()) {
            statement.execute("""
                            CREATE TABLE courses(
                                course_id INT AUTO_INCREMENT PRIMARY KEY,
                                title VARCHAR(255),
                                capacity INT NOT NULL DEFAULT 2,
                                prefix VARCHAR(20),
                                teacher_name VARCHAR(255)
                            );
                            """);
            statement.execute("DELETE FROM courses;");
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/courses-view.fxml"));

        Scene scene = new Scene(loader.load());

        controller = loader.getController();

        testTeacher = new User(
                1,
                "teacher1",
                "password",
                "TEACHER",
                "Dr.",
                "Smith"
        );

        controller.setCurrentUser(testTeacher);

        CourseDao dao = new CourseDao(connection);

        CourseService service = new CourseService(dao);

        controller.setCourseService(service);

        stage.setScene(scene);

        stage.show();
    }

    @Test
    void addCourseAddsToTable() {
        clickOn("#courseNameField").write("Computer Science");

        clickOn("#capacityField").write("2");

        clickOn("#addButton");

        TableView<Course> table = lookup("#courseTable").query();

        assertEquals(1,table.getItems().size());

        Assertions.assertEquals("Computer Science", table.getItems().get(0).getCourseName());

        Assertions.assertEquals(2,table.getItems().get(0).getCapacity());

        Assertions.assertEquals("Dr.",table.getItems().get(0).getPrefix());

        Assertions.assertEquals("Smith",table.getItems().get(0).getTeacherName());
    }
}
