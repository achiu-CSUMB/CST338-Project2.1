package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import factory.SceneFactory;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Grade;
import model.User;

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

    @FXML
    private TableView<Grade> gradesTable;
    private User currentUser;

    @FXML private Label headerLabel;
    @FXML private Label maxScoreLabel;
    @FXML private Button viewStatisticsButton;
    @FXML private TableColumn<Grade, String> studentColumn;
    @FXML private TableColumn<Grade, String> statusColumn;
    @FXML private Button backButton;

    @FXML
    private TableColumn<Grade, Double> scoreColumn;


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

        // No "status" field on Grade, so this needs a computed value, not PropertyValueFactory
        statusColumn.setCellValueFactory(cellData -> {
            double score = cellData.getValue().getScore();
            return new javafx.beans.property.SimpleStringProperty(score >= 60 ? "Pass" : "Fail");
        });


        refreshTable();
    }

    private void applyRoleView() {
        if (currentUser == null) return;

        boolean isTeacher = currentUser.getRole().equalsIgnoreCase("TEACHER");

        if (studentColumn != null) {
            studentColumn.setVisible(isTeacher);
        }
    }

    @FXML
    private void goBackToLogin(ActionEvent event) {
        Stage stage = (Stage) backButton.getScene().getWindow();

        SceneFactory sceneFactory = new SceneFactory(stage, currentUser);

        sceneFactory.showScene(SceneFactory.SceneType.MAIN_MENU);
    }

    /**
     * Replaces the currently displayed grades and refreshes the table.
     * Call this after loading the FXML (e.g. loader.<GradesView>getController().setGrades(...)).
     */
    public void setGrades(List<Grade> newGrades) {
        grades.clear();
        grades.addAll(newGrades);
        refreshTable();
    }

    private void refreshTable() {
        if (gradesTable != null) {
            gradesTable.setItems(FXCollections.observableArrayList(grades));
        }
    }
}