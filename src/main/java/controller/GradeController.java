package controller;


import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import factory.SceneFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import model.Assignment;
import model.Enrollment;
import model.Grade;
import model.User;
import dao.CourseDao;
import dao.EnrollmentDao;
import dao.UserDao;
import model.Course;
import service.GradeService;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
/**
 * Author: Alvin Chiu
 * Created: 8/1/2026
 * Current version: V2.0 - 8/4/2026
 * Description: Controller for grades-view.fxml. Displays the grades of
 * students for a given course in a TableView.
 *
 *
 */
public class GradeController implements Initializable {

    @FXML private TableView<Grade> gradesTable;
    @FXML private Label headerLabel;
    @FXML private Label maxScoreLabel;
    @FXML private Button viewStatisticsButton;
    @FXML private Button addGradeButton;
    @FXML private TableColumn<Grade, String> studentColumn;
    @FXML private TableColumn<Grade, String> statusColumn;
    @FXML private TableColumn<Grade, Double> scoreColumn;
    @FXML
    private void goBackToLogin(ActionEvent event) {
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();

        SceneFactory sceneFactory = new SceneFactory(stage, currentUser);

        sceneFactory.showScene(SceneFactory.SceneType.MAIN_MENU);
    }

    private final CourseDao courseDao = new CourseDao();
    private final EnrollmentDao enrollmentDao = new EnrollmentDao();
    private final UserDao userDao = new UserDao();
    private final GradeService gradeService = new GradeService();
    private User currentUser;
    private final List<Grade> grades;
    private Assignment currentAssignment;

    public GradeController() {
        this.grades = new ArrayList<>();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        applyRoleView();
    }

    /**
     * Scopes this screen's header to a single assignment (called from the
     * assignment picker). Optional — if never called, the header falls
     * back to showing just the course, as before.
     */
    public void setAssignment(Assignment assignment) {
        this.currentAssignment = assignment;
        updateHeader();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(studentColumn != null) {
            studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        }
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        scoreColumn.setOnEditCommit(this::handleScoreEditCommit);

        statusColumn.setCellValueFactory(cellData -> {
            double score = cellData.getValue().getScore();
            return new SimpleStringProperty(gradeService.calculateLetterGrade(score));
        });

        if (viewStatisticsButton != null) {
            viewStatisticsButton.setOnAction(e -> openStatisticsView());
        }

        if (addGradeButton != null) {
            addGradeButton.setOnAction(e -> openAddGradeDialog());
        }

        refreshTable();
    }

    /**
     * Fired when a teacher commits an edit to a score cell. Validates the
     * new value, saves it (insert or update, whichever applies), and
     * refreshes the table so the letter-grade column stays in sync.
     */
    private void handleScoreEditCommit(TableColumn.CellEditEvent<Grade, Double> event) {
        Grade grade = event.getRowValue();
        Double newScore = event.getNewValue();

        if (newScore == null || newScore < Grade.MIN_GRADE || newScore > Grade.MAX_GRADE) {
            showAlert(Alert.AlertType.ERROR, "Invalid Score",
                    "Score must be between " + Grade.MIN_GRADE + " and " + Grade.MAX_GRADE + ".");
            refreshTable();
            return;
        }

        grade.setScore(newScore);
        grade.setDate(LocalDate.now());

        boolean saved = gradeService.saveGrade(grade);

        if (!saved) {
            showAlert(Alert.AlertType.ERROR, "Save Failed",
                    "Could not save the updated score. Please try again.");
        }

        refreshTable();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void openStatisticsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/statistics-view.fxml"));
            Parent root = loader.load();

            StatisticsController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setGrades(grades);

            Stage stage = (Stage) viewStatisticsButton.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Opens a dialog letting a teacher add a grade for a student enrolled in
     * the current course who doesn't already have a grade for the current
     * assignment. Only available when this screen is scoped to a single
     * assignment (i.e. reached via the assignment picker).
     */
    private void openAddGradeDialog() {
        if (currentAssignment == null) {
            showAlert(Alert.AlertType.INFORMATION, "Select an Assignment",
                    "Add Grade is only available when viewing grades for a specific assignment.");
            return;
        }

        String courseId = String.valueOf(currentAssignment.getCourseId());
        List<User> availableStudents = getStudentsWithoutGrade(currentAssignment.getCourseId());

        if (availableStudents.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Students Available",
                    "Every enrolled student already has a grade for this assignment.");
            return;
        }

        Dialog<Grade> dialog = new Dialog<>();
        dialog.setTitle("Add Grade");
        dialog.setHeaderText("Add a grade for " + currentAssignment.getTitle());

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        ComboBox<User> studentComboBox = new ComboBox<>(FXCollections.observableArrayList(availableStudents));
        studentComboBox.setPromptText("Select a student");
        studentComboBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty || user == null ? null : user.getUsername());
            }
        });
        studentComboBox.setButtonCell(studentComboBox.getCellFactory().call(null));

        TextField scoreField = new TextField();
        scoreField.setPromptText(Grade.MIN_GRADE + " - " + Grade.MAX_GRADE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
        grid.add(new Label("Student:"), 0, 0);
        grid.add(studentComboBox, 1, 0);
        grid.add(new Label("Score:"), 0, 1);
        grid.add(scoreField, 1, 1);
        dialog.getDialogPane().setContent(grid);

        // Validate before letting the "Add" button close the dialog, so a
        // bad entry shows an error instead of silently discarding input.
        Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (studentComboBox.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Missing Student", "Please select a student.");
                event.consume();
            } else if (parseScore(scoreField.getText()) == null) {
                showAlert(Alert.AlertType.ERROR, "Invalid Score",
                        "Score must be a number between " + Grade.MIN_GRADE + " and " + Grade.MAX_GRADE + ".");
                event.consume();
            }
        });

        dialog.setResultConverter(buttonType -> {
            if (buttonType != addButtonType) {
                return null;
            }

            User selectedStudent = studentComboBox.getValue();
            Double score = parseScore(scoreField.getText());

            return new Grade(courseId, String.valueOf(selectedStudent.getUserId()),
                    String.valueOf(currentAssignment.getAssignmentId()), score);
        });

        dialog.showAndWait().ifPresent(this::addGrade);
    }

    /**
     * Saves a newly created grade and, on success, adds it to the table
     * without needing a full re-fetch from the database.
     */
    private void addGrade(Grade grade) {
        boolean saved = gradeService.saveGrade(grade);

        if (!saved) {
            showAlert(Alert.AlertType.ERROR, "Save Failed",
                    "Could not save the new grade. Please try again.");
            return;
        }

        User student = userDao.findById(Integer.parseInt(grade.getStudentId()));
        if (student != null) {
            grade.setStudentName(student.getUsername());
        }

        grades.add(grade);
        refreshTable();
    }

    /**
     * Returns null if the text isn't a valid number in range, rather than
     * throwing, so the dialog can show a friendly error instead of crashing.
     */
    private Double parseScore(String text) {
        try {
            double score = Double.parseDouble(text.trim());
            if (score < Grade.MIN_GRADE || score > Grade.MAX_GRADE) {
                return null;
            }
            return score;
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Students enrolled in the assignment's course who don't already have a
     * grade recorded for that assignment (i.e. valid candidates to add).
     */
    private List<User> getStudentsWithoutGrade(int courseId) {
        Set<String> studentIdsWithGrade = new HashSet<>();
        for (Grade grade : grades) {
            studentIdsWithGrade.add(grade.getStudentId());
        }

        List<User> result = new ArrayList<>();
        for (Enrollment enrollment : enrollmentDao.getCourseEnrollments(courseId)) {
            if (enrollment.isWaitlisted()) {
                continue;
            }
            String studentId = String.valueOf(enrollment.getStudentId());
            if (studentIdsWithGrade.contains(studentId)) {
                continue;
            }
            User student = userDao.findById(enrollment.getStudentId());
            if (student != null) {
                result.add(student);
            }
        }
        return result;
    }

    private void applyRoleView() {
        if (currentUser == null) return;

        boolean isTeacher = currentUser.getRole().equalsIgnoreCase("TEACHER");

        if (studentColumn != null) {
            studentColumn.setVisible(isTeacher);
        }

        // Only teachers can edit scores; students see a read-only table.
        if (gradesTable != null) {
            gradesTable.setEditable(isTeacher);
        }
        scoreColumn.setEditable(isTeacher);

        // Only teachers can add new grades; students don't get this button.
        if (addGradeButton != null) {
            addGradeButton.setVisible(isTeacher);
            addGradeButton.setManaged(isTeacher);
        }
    }



    /**
     * Replaces the currently displayed grades and refreshes the table.
     * Call this after loading the FXML (e.g. loader.<GradesView>getController().setGrades(...)).
     */

    private void refreshTable() {
        if (gradesTable != null) {
            gradesTable.setItems(FXCollections.observableArrayList(grades));
        }
    }

    public void setGrades(List<Grade> newGrades) {
        grades.clear();
        grades.addAll(newGrades);
        refreshTable();
        updateHeader();
    }

    private void updateHeader() {
        if (maxScoreLabel != null) {
            double maxScore = currentAssignment != null ? currentAssignment.getMaxPoints() : Grade.MAX_GRADE;
            maxScoreLabel.setText("Max Score: " + maxScore);
        }

        if (headerLabel == null) {
            return;
        }

        // When scoped to an assignment (via the assignment picker), show
        // the course + assignment title regardless of whether any grades
        // have been entered yet.
        if (currentAssignment != null) {
            Course course = courseDao.findById(currentAssignment.getCourseId());
            String courseName = course != null ? course.getCourseName() : "Course " + currentAssignment.getCourseId();
            headerLabel.setText(courseName + " — " + currentAssignment.getTitle());
            return;
        }

        if (grades.isEmpty()) {
            headerLabel.setText("No Grades");
            return;
        }

        String courseId = grades.get(0).getCourseId();
        Course course = courseDao.findById(Integer.parseInt(courseId));
        headerLabel.setText(course != null ? course.getCourseName() : "Course " + courseId);
    }
}