package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Grade;

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

    @FXML
    private TableColumn<Grade, String> courseColumn;

    @FXML
    private TableColumn<Grade, String> assignmentColumn;

    @FXML
    private TableColumn<Grade, Double> scoreColumn;


    private final List<Grade> grades;

    public GradeController() {
        this.grades = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        assignmentColumn.setCellValueFactory(new PropertyValueFactory<>("assignmentName"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        refreshTable();
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