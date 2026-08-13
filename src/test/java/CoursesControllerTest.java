import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

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

    @Override
    public void start(Stage stage) throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:coursescontroller;DB_CLOSE_DELAY=-1");

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
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/courses-view.fxml"));

        Scene scene = new Scene(loader.load());

        controller = loader.getController();

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

        clickOn("#prefixField").write("Dr.");
        clickOn("#teacherNameField").write("Smith");

        clickOn("#addButton");

        TableView<Course> table = lookup("#courseTable").query();

        assertEquals(1,table.getItems().size());

        assertEquals("Computer Science", table.getItems().get(0).getCourseName());

        assertEquals("Dr.", table.getItems().get(0).getPrefix());

        assertEquals("Smith", table.getItems().get(0).getTeacherName());
    }
}
