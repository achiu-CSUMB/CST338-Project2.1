import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Author: Alvin Chiu
 * Created: 8/4/2026
 * Current version: V1.0 - 8/4/2026
 * Description: JavaFX view showing a course/assignment
 * header with max score, a "View Statistics" button, and a table of
 * student grades with a graded/ungraded status indicator.
 *
 **/
public class GradesView extends VBox {

    public static final double UNGRADED = -1;

    private final Label headerLabel;
    private final Label maxScoreLabel;
    private final Button viewStatisticsButton;
    private final TableView<Grade> gradesTable;

    private double maxScore = 0;
    private Map<String, String> studentNames = new HashMap<>();

    public GradesView() {
        setSpacing(15);
        setPadding(new Insets(20));

        headerLabel = new Label();
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        maxScoreLabel = new Label();
        maxScoreLabel.setStyle("-fx-text-fill: gray;");

        VBox headerText = new VBox(2, headerLabel, maxScoreLabel);

        viewStatisticsButton = new Button("View Statistics");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(headerText, spacer, viewStatisticsButton);
        topBar.setAlignment(Pos.TOP_LEFT);

        gradesTable = buildGradesTable();
        VBox.setVgrow(gradesTable, Priority.ALWAYS);

        getChildren().addAll(topBar, gradesTable);
    }

    private TableView<Grade> buildGradesTable() {
        TableView<Grade> table = new TableView<>();

        TableColumn<Grade, String> studentColumn = new TableColumn<>("Student");
        studentColumn.setCellValueFactory(data -> {
            String id = data.getValue().getStudentId();
            String name = studentNames.getOrDefault(id, id);
            return new ReadOnlyStringWrapper(name);
        });
        studentColumn.setPrefWidth(220);

        TableColumn<Grade, String> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setCellValueFactory(data -> {
            double score = data.getValue().getScore();
            String scoreText = (score == UNGRADED) ? "--" : formatScore(score);
            return new ReadOnlyStringWrapper(scoreText + "/" + formatScore(maxScore));
        });
        scoreColumn.setPrefWidth(100);

        TableColumn<Grade, Void> statusColumn = new TableColumn<>("");
        statusColumn.setPrefWidth(40);
        statusColumn.setSortable(false);
        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Grade grade = getTableView().getItems().get(getIndex());
                Label icon = new Label(grade.getScore() == UNGRADED ? "\u2717" : "\u2713");
                icon.setStyle(grade.getScore() == UNGRADED
                        ? "-fx-text-fill: red; -fx-font-weight: bold;"
                        : "-fx-text-fill: green; -fx-font-weight: bold;");
                setGraphic(icon);
            }
        });

        table.getColumns().add(studentColumn);
        table.getColumns().add(scoreColumn);
        table.getColumns().add(statusColumn);

        return table;
    }

    private String formatScore(double score) {
        if (score == Math.floor(score)) {
            return String.valueOf((int) score);
        }
        return String.valueOf(score);
    }

    /**
     * Sets the header text and max score shown above the table, e.g.
     * setHeader("CST 338 - Project 2 Part 1", 100).
     */
    public void setHeader(String courseAndAssignmentTitle, double maxScore) {
        this.maxScore = maxScore;
        headerLabel.setText(courseAndAssignmentTitle);
        maxScoreLabel.setText("Max Score: " + formatScore(maxScore));
        gradesTable.refresh();
    }

    /**
     * Supplies a lookup from studentId to display name, used by the
     * Student column. IDs without an entry fall back to showing the raw id.
     */
    public void setStudentNames(Map<String, String> studentNames) {
        this.studentNames = studentNames;
        gradesTable.refresh();
    }

    /**
     * Populates the table with the given grades.
     */
    public void setGrades(List<Grade> grades) {
        gradesTable.getItems().setAll(grades);
    }

    /**
     * Hooks up the "View Statistics" button's action.
     */
    public void setOnViewStatistics(Runnable action) {
        viewStatisticsButton.setOnAction(e -> action.run());
    }
}