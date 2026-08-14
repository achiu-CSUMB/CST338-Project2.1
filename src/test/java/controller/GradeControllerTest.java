package controller;

import dao.GradeDao;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Grade;
import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GradeService;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GradeController, using the GradeController(GradeService)
 * constructor added for testing (so no real database is touched) and
 * reflection to inject the @FXML fields that would normally be supplied
 * by FXMLLoader. This covers the controller's public setters and the
 * role-based view logic they trigger; it does not exercise UI callbacks
 * that also depend on the hardcoded CourseDao/EnrollmentDao/UserDao fields
 * (e.g. openAddGradeDialog), since those aren't currently injectable.
 */
class GradeControllerTest {

    private GradeController controller;
    private TableView<Grade> gradesTable;
    private Label headerLabel;
    private Label maxScoreLabel;
    private Button addGradeButton;
    private TableColumn<Grade, String> studentColumn;
    private TableColumn<Grade, Double> scoreColumn;
    private TableColumn<Grade, String> statusColumn;

    @BeforeAll
    static void initJavaFxToolkit() {
        // Starting the JavaFX toolkit is required before constructing
        // JavaFX controls (TableView, Button, etc.) outside of an
        // Application. Safe to call once for the whole test class.
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new GradeController(new GradeService(new NoOpGradeDao()));

        gradesTable = new TableView<>();
        headerLabel = new Label();
        maxScoreLabel = new Label();
        addGradeButton = new Button();
        studentColumn = new TableColumn<>("Student");
        scoreColumn = new TableColumn<>("Score");
        statusColumn = new TableColumn<>("Grade");

        setPrivateField("gradesTable", gradesTable);
        setPrivateField("headerLabel", headerLabel);
        setPrivateField("maxScoreLabel", maxScoreLabel);
        setPrivateField("addGradeButton", addGradeButton);
        setPrivateField("studentColumn", studentColumn);
        setPrivateField("scoreColumn", scoreColumn);
        setPrivateField("statusColumn", statusColumn);
        // viewStatisticsButton, assignmentColumn, maxScoreColumn are left
        // null here on purpose: the controller null-checks them, mirroring
        // grades-view.fxml, which doesn't declare assignmentColumn/
        // maxScoreColumn either.

        controller.initialize(null, null);
    }

    private void setPrivateField(String name, Object value) throws Exception {
        Field field = GradeController.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(controller, value);
    }

    private User teacher() {
        return new User(1, "ms_frizzle", "pw", "TEACHER", null, null);
    }

    private User student() {
        return new User(2, "arnold", "pw", "STUDENT", null, null);
    }

    @Test
    void setGrades_populatesTheTable() {
        List<Grade> grades = List.of(
                new Grade("1", "2", "3", 88.0),
                new Grade("1", "5", "3", 70.0)
        );

        controller.setGrades(grades);

        assertEquals(2, gradesTable.getItems().size());
    }

    @Test
    void setGrades_withNoGrades_setsNoGradesHeader() {
        controller.setGrades(new ArrayList<>());

        assertEquals("No Grades", headerLabel.getText());
    }

    @Test
    void setCurrentUser_teacher_makesTableEditableAndShowsAddGradeButton() {
        controller.setCurrentUser(teacher());

        assertTrue(gradesTable.isEditable());
        assertTrue(scoreColumn.isEditable());
        assertTrue(addGradeButton.isVisible());
        assertTrue(studentColumn.isVisible());
    }

    @Test
    void setCurrentUser_student_makesTableReadOnlyAndHidesAddGradeButtonAndStudentColumn() {
        controller.setCurrentUser(student());

        assertFalse(gradesTable.isEditable());
        assertFalse(scoreColumn.isEditable());
        assertFalse(addGradeButton.isVisible());
        assertFalse(studentColumn.isVisible());
    }

    /**
     * A GradeDao that never gets exercised in these tests (GradeService's
     * calls in this test class only hit the "No Grades" / setGrades paths,
     * which don't call the dao), but must still exist so GradeService has
     * something non-null to hold. Every method throws if actually called,
     * so an accidental real-database hit would fail loudly instead of
     * silently touching the shared application database.
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
