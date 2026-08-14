package controller;


import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

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
import model.Grade;
import model.User;
import dao.CourseDao;
import model.Course;
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
    @FXML private Button backButton;
    @FXML private TableColumn<Grade, Double> scoreColumn;
    @FXML
    private void goBackToLogin(ActionEvent event) {
        Stage stage = (Stage) backButton.getScene().getWindow();

        SceneFactory sceneFactory = new SceneFactory(stage, currentUser);

        sceneFactory.showScene(SceneFactory.SceneType.MAIN_MENU);
    }

    private final CourseDao courseDao = new CourseDao();
    private User currentUser;
    private final List<Grade> grades;

    public GradeController() {
        this.grades = new ArrayList<>();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        applyRoleView();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(studentColumn != null) {
            studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        }
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        statusColumn.setCellValueFactory(cellData -> {
            double score = cellData.getValue().getScore();
            return new SimpleStringProperty(calculateLetterGrade(score));
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


    private String calculateLetterGrade(double score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    private double calculateMedian(List<Grade> gradeList) {
        List<Double> scores = gradeList.stream()
                .map(Grade::getScore)
                .sorted()
                .collect(Collectors.toList());

        int size = scores.size();
        int mid = size / 2;

        if (size % 2 == 0) {
            return (scores.get(mid - 1) + scores.get(mid)) / 2.0;
        } else {
            return scores.get(mid);
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
            maxScoreLabel.setText("Max Score: " + Grade.MAX_GRADE);
        }

        if (headerLabel == null) {
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