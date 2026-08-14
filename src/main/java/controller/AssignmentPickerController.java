package controller;

import java.util.List;

import dao.AssignmentDao;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Assignment;
import model.Course;
import model.Grade;
import model.User;
import service.GradeService;

/**
 * Description: Controller for assignment-picker-view.fxml. Shown after a
 * teacher selects a course from the course picker (Grades flow), so they
 * can choose which assignment's grades they want to view before landing
 * on grades-view.fxml.
 */
public class AssignmentPickerController {

    @FXML private Label headerLabel;
    @FXML private ListView<Assignment> assignmentListView;
    @FXML private Button selectAssignmentButton;
    @FXML private Button backButton;

    private final AssignmentDao assignmentDao = new AssignmentDao();
    private final GradeService gradeService = new GradeService();

    private User currentUser;
    private Course course;

    @FXML
    public void initialize() {
        assignmentListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Assignment assignment, boolean empty) {
                super.updateItem(assignment, empty);
                if (empty || assignment == null) {
                    setText(null);
                } else {
                    setText(assignment.getTitle() + " (Due " + assignment.getDueDate() + ")");
                }
            }
        });
    }

    /**
     * Call this before, or after, setCourse — order doesn't matter.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Sets which course's assignments to list, then loads them.
     */
    public void setCourse(Course course) {
        this.course = course;
        loadAssignments();
    }

    private void loadAssignments() {
        if (course == null) {
            return;
        }

        String prefix = course.getPrefix() != null ? course.getPrefix() + " " : "";
        headerLabel.setText("Select an Assignment — " + prefix + course.getCourseName());

        List<Assignment> assignments = assignmentDao.findByCourseId(course.getCourseId());
        assignmentListView.setItems(FXCollections.observableArrayList(assignments));
    }

    @FXML
    private void handleSelectAssignment(ActionEvent event) {
        Assignment selectedAssignment = assignmentListView.getSelectionModel().getSelectedItem();

        if (selectedAssignment == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Assignment Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an assignment first.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/grades-view.fxml"));
            Parent root = loader.load();

            GradeController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setAssignment(selectedAssignment);
            controller.setGrades(fetchGrades(selectedAssignment));

            Stage stage = (Stage) selectAssignmentButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Grade> fetchGrades(Assignment assignment) {
        String courseId = String.valueOf(assignment.getCourseId());
        String assignmentId = String.valueOf(assignment.getAssignmentId());
        return gradeService.getGradesForAssignment(courseId, assignmentId);
    }

    @FXML
    private void goBackToCoursePicker(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/course-picker-view.fxml"));
            Parent root = loader.load();

            CoursePickerController controller = loader.getController();
            controller.setMode(CoursePickerController.Mode.GRADES);
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
