package controller;

import dao.GradeDao;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Grade;
import model.User;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import service.GradeService;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A genuine TestFX UI test. Unlike GradeControllerTest (which builds the
 * controller and its FXML fields directly via reflection, bypassing the
 * FXML loader), this test loads the real grades-view.fxml and drives it
 * with FxRobot mouse clicks the way a person actually would — so it also
 * exercises the FXML wiring itself (fx:id bindings, the addGradeButton's
 * onAction, Alert popups) rather than just the controller's Java methods.
 *
 * It still avoids the real database by supplying the GradeController
 * (GradeService) testing constructor through a controller factory.
 *
 * Requires the testfx-core and testfx-junit5 dependencies on the test
 * classpath, and grades-view.fxml available as a resource (it already is,
 * under main/resources/views).
 */
class GradeControllerUiTest extends ApplicationTest {

    private GradeController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/grades-view.fxml"));
        loader.setControllerFactory(type -> new GradeController(new GradeService(new NoOpGradeDao())));

        Parent root = loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    @Test
    void addGradeButton_visibleForTeacher_hiddenForStudent() {
        interact(() -> controller.setCurrentUser(
                new User(1, "ms_frizzle", "pw", "TEACHER", null, null)));

        Button addGradeButton = lookup("#addGradeButton").query();
        assertTrue(addGradeButton.isVisible());

        interact(() -> controller.setCurrentUser(
                new User(2, "arnold", "pw", "STUDENT", null, null)));

        assertFalse(addGradeButton.isVisible());
    }

    @Test
    void clickingAddGrade_withNoAssignmentSelected_showsInformationAlertThatCanBeDismissed() {
        interact(() -> controller.setCurrentUser(
                new User(1, "ms_frizzle", "pw", "TEACHER", null, null)));

        // A real mouse click on the real button, wired through the real
        // FXML — this is the part a reflection-based test can't cover.
        clickOn("#addGradeButton");
        WaitForAsyncUtils.waitForFxEvents();

        Stage alert = findWindowByTitle("Select an Assignment");
        assertNotNull(alert, "Expected the 'Select an Assignment' info alert to be showing");

        clickOn("OK");
        WaitForAsyncUtils.waitForFxEvents();

        assertNull(findWindowByTitle("Select an Assignment"),
                "Alert should be dismissed after clicking OK");
    }

    private Stage findWindowByTitle(String title) {
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage stage && title.equals(stage.getTitle())) {
                return stage;
            }
        }
        return null;
    }

    /**
     * Same no-op fake used in GradeControllerTest: these two scenarios
     * never need the DAO to actually return data, so any real call is
     * treated as a test bug rather than silently hitting the shared
     * application database.
     */
    private static class NoOpGradeDao extends GradeDao {
        NoOpGradeDao() {
            super(null);
        }

        private static SQLException unexpectedCall() {
            return new SQLException("GradeDao should not be called in this test");
        }

        @Override
        public void insert(Grade grade) throws SQLException {
            throw unexpectedCall();
        }

        @Override
        public void update(Grade grade) throws SQLException {
            throw unexpectedCall();
        }

        @Override
        public Grade find(String courseId, String studentId, String assignmentId) throws SQLException {
            throw unexpectedCall();
        }

        @Override
        public Grade find(String courseId, String studentId) throws SQLException {
            throw unexpectedCall();
        }

        @Override
        public List<Grade> findByCourseId(String courseId) throws SQLException {
            throw unexpectedCall();
        }

        @Override
        public List<Grade> findByCourseIdAndAssignmentId(String courseId, String assignmentId) throws SQLException {
            throw unexpectedCall();
        }

        @Override
        public List<Grade> findByCourseIdAndStudentId(String courseId, String studentId) throws SQLException {
            throw unexpectedCall();
        }
    }
}
