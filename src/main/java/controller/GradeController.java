package controller;


import java.net.URL;
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
import javafx.stage.Stage;
import model.Assignment;
import model.Grade;
import model.User;
import dao.CourseDao;
import model.Course;
import service.GradeService;
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

        statusColumn.setCellValueFactory(cellData -> {
            double score = cellData.getValue().getScore();
            return new SimpleStringProperty(gradeService.calculateLetterGrade(score));
        });

        if (viewStatisticsButton != null) {
            viewStatisticsButton.setOnAction(e -> openStatisticsView());
        }

        refreshTable();
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


    private void applyRoleView() {
        if (currentUser == null) return;

        boolean isTeacher = currentUser.getRole().equalsIgnoreCase("TEACHER");

        if (studentColumn != null) {
            studentColumn.setVisible(isTeacher);
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