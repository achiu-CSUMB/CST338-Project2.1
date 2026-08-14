package controller;

import factory.SceneFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Grade;
import model.User;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for statistics-view.fxml. Displays summary statistics
 * (average, median, high, low) and a letter-grade breakdown for a
 * given list of grades.
 */
public class StatisticsController {

    @FXML private Button backButton;
    @FXML private Label countLabel;
    @FXML private Label averageLabel;
    @FXML private Label medianLabel;
    @FXML private Label highLabel;
    @FXML private Label lowLabel;
    @FXML private GridPane breakdownGrid;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setGrades(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            countLabel.setText("0");
            averageLabel.setText("-");
            medianLabel.setText("-");
            highLabel.setText("-");
            lowLabel.setText("-");
            return;
        }

        DoubleSummaryStatistics summary = grades.stream()
                .mapToDouble(Grade::getScore)
                .summaryStatistics();

        countLabel.setText(String.valueOf(grades.size()));
        averageLabel.setText(String.format("%.1f", summary.getAverage()));
        medianLabel.setText(String.format("%.1f", calculateMedian(grades)));
        highLabel.setText(String.format("%.1f", summary.getMax()));
        lowLabel.setText(String.format("%.1f", summary.getMin()));

        populateBreakdown(grades);
    }

    private void populateBreakdown(List<Grade> grades) {
        breakdownGrid.getChildren().clear();
        Map<String, Long> letterCounts = grades.stream()
                .map(g -> calculateLetterGrade(g.getScore()))
                .collect(Collectors.groupingBy(letter -> letter, Collectors.counting()));

        List<String> letters = List.of("A", "B", "C", "D", "F");
        for (int i = 0; i < letters.size(); i++) {
            String letter = letters.get(i);
            long count = letterCounts.getOrDefault(letter, 0L);

            breakdownGrid.add(new Label(letter + ":"), 0, i);
            breakdownGrid.add(new Label(String.valueOf(count)), 1, i);
        }
    }

    private String calculateLetterGrade(double score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    private double calculateMedian(List<Grade> grades) {
        List<Double> scores = grades.stream()
                .map(Grade::getScore)
                .sorted()
                .collect(Collectors.toList());

        int size = scores.size();
        int mid = size / 2;

        return (size % 2 == 0)
                ? (scores.get(mid - 1) + scores.get(mid)) / 2.0
                : scores.get(mid);
    }

    @FXML
    private void goBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneFactory sceneFactory = new SceneFactory(stage, currentUser);
        sceneFactory.showScene(SceneFactory.SceneType.MAIN_MENU);
    }
}