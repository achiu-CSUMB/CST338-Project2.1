package controller;

import dao.AssignmentDao;
import dao.CourseDao;
import factory.SceneFactory;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Assignment;
import model.Course;
import model.User;

import java.util.List;

public class AssignmentsController {

    private final AssignmentDao assignmentDao = new AssignmentDao();
    private final CourseDao courseDao = new CourseDao();
    private User currentUser;
    private Integer filterCourseId;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Scopes this screen to a single course (called from the course picker).
     * Locks the course ID field so new assignments default to this course,
     * and reloads the table to show only that course's assignments.
     */
    public void setCourseFilter(int courseId) {
        this.filterCourseId = courseId;

        courseIdField.setText(String.valueOf(courseId));
        courseIdField.setDisable(true);

        if (headerLabel != null) {
            Course course = courseDao.findById(courseId);
            String label = course != null
                    ? (course.getPrefix() != null ? course.getPrefix() + " " : "") + course.getCourseName()
                    : "Course " + courseId;
            headerLabel.setText("Assignments — " + label);
        }

        loadAssignments();
    }

    @FXML
    private Label headerLabel;

    @FXML
    private TableView<Assignment> assignmentTable;

    @FXML
    private TableColumn<Assignment, Integer> assignmentIdColumn;

    @FXML
    private TableColumn<Assignment, Integer> courseIdColumn;

    @FXML
    private TableColumn<Assignment, String> titleColumn;

    @FXML
    private TableColumn<Assignment, String> descriptionColumn;

    @FXML
    private TableColumn<Assignment, String> dueDateColumn;

    @FXML
    private TableColumn<Assignment, Double> maxPointsColumn;

    @FXML
    private TextField courseIdField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField dueDateField;

    @FXML
    private TextField maxPointsField;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        assignmentIdColumn.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getAssignmentId()
                ).asObject()
        );

        courseIdColumn.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getCourseId()
                ).asObject()
        );

        titleColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getTitle()
                )
        );

        descriptionColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getDescription()
                )
        );

        dueDateColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getDueDate()
                )
        );

        maxPointsColumn.setCellValueFactory(
                data -> new SimpleDoubleProperty(
                        data.getValue().getMaxPoints()
                ).asObject()
        );

        addButton.setOnAction(e -> addAssignment());
        updateButton.setOnAction(e -> updateAssignment());
        deleteButton.setOnAction(e -> deleteAssignment());

        assignmentTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldAssignment, newAssignment) -> {
                    if (newAssignment != null) {
                        courseIdField.setText(
                                String.valueOf(newAssignment.getCourseId())
                        );

                        titleField.setText(
                                newAssignment.getTitle()
                        );

                        descriptionField.setText(
                                newAssignment.getDescription()
                        );

                        dueDateField.setText(
                                newAssignment.getDueDate()
                        );

                        maxPointsField.setText(
                                String.valueOf(newAssignment.getMaxPoints())
                        );
                    }
                });

        loadAssignments();
    }

    private void loadAssignments() {
        List<Assignment> assignments = filterCourseId != null
                ? assignmentDao.findByCourseId(filterCourseId)
                : assignmentDao.findAll();

        assignmentTable.setItems(
                FXCollections.observableArrayList(assignments)
        );
    }

    private void addAssignment() {
        if (courseIdField.getText().isBlank()
                || titleField.getText().isBlank()
                || dueDateField.getText().isBlank()
                || maxPointsField.getText().isBlank()) {

            statusLabel.setText("Required fields cannot be empty.");
            return;
        }

        try {
            Assignment assignment = new Assignment(
                    Integer.parseInt(courseIdField.getText()),
                    titleField.getText(),
                    descriptionField.getText(),
                    dueDateField.getText(),
                    Double.parseDouble(maxPointsField.getText())
            );

            if (assignmentDao.insert(assignment)) {
                statusLabel.setText("Assignment added.");
                loadAssignments();
                clearFields();
            } else {
                statusLabel.setText("Could not add assignment.");
            }

        } catch (NumberFormatException e) {
            statusLabel.setText(
                    "Course ID and max points must be numbers."
            );
        }
    }

    private void updateAssignment() {
        Assignment selectedAssignment =
                assignmentTable.getSelectionModel().getSelectedItem();

        if (selectedAssignment == null) {
            statusLabel.setText("Select an assignment first.");
            return;
        }

        try {
            selectedAssignment.setCourseId(
                    Integer.parseInt(courseIdField.getText())
            );

            selectedAssignment.setTitle(
                    titleField.getText()
            );

            selectedAssignment.setDescription(
                    descriptionField.getText()
            );

            selectedAssignment.setDueDate(
                    dueDateField.getText()
            );

            selectedAssignment.setMaxPoints(
                    Double.parseDouble(maxPointsField.getText())
            );

            if (assignmentDao.update(selectedAssignment)) {
                statusLabel.setText("Assignment updated.");
                loadAssignments();
                clearFields();
            } else {
                statusLabel.setText("Could not update assignment.");
            }

        } catch (NumberFormatException e) {
            statusLabel.setText(
                    "Course ID and max points must be numbers."
            );
        }
    }

    private void deleteAssignment() {
        Assignment selectedAssignment =
                assignmentTable.getSelectionModel().getSelectedItem();

        if (selectedAssignment == null) {
            statusLabel.setText("Select an assignment first.");
            return;
        }

        if (assignmentDao.delete(
                selectedAssignment.getAssignmentId())) {

            statusLabel.setText("Assignment deleted.");
            loadAssignments();
            clearFields();

        } else {
            statusLabel.setText("Could not delete assignment.");
        }
    }

    @FXML
    private void goBackToMenu() {
        Stage stage =
                (Stage) assignmentTable.getScene().getWindow();

        SceneFactory sceneFactory =
                new SceneFactory(stage, currentUser);

        sceneFactory.showScene(
                SceneFactory.SceneType.MAIN_MENU
        );
    }

    private void clearFields() {
        courseIdField.clear();
        titleField.clear();
        descriptionField.clear();
        dueDateField.clear();
        maxPointsField.clear();
    }
}