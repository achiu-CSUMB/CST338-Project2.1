import dao.AssignmentDao;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Assignment;

public class AssignmentsView extends VBox {

    private AssignmentDao assignmentDao;
    private TableView<Assignment> assignmentTable;

    private TextField courseIdField;
    private TextField titleField;
    private TextField descriptionField;
    private TextField dueDateField;
    private TextField maxPointsField;

    private Label statusLabel;

    public AssignmentsView() {

        assignmentDao = new AssignmentDao();

        Label titleLabel = new Label("Assignments");

        courseIdField = new TextField();
        courseIdField.setPromptText("Course ID");

        titleField = new TextField();
        titleField.setPromptText("Title");

        descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        dueDateField = new TextField();
        dueDateField.setPromptText("Due Date");

        maxPointsField = new TextField();
        maxPointsField.setPromptText("Max Points");

        assignmentTable = new TableView<>();

        TableColumn<Assignment, Integer> idColumn =
                new TableColumn<>("ID");
        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("assignmentId"));

        TableColumn<Assignment, Integer> courseColumn =
                new TableColumn<>("Course ID");
        courseColumn.setCellValueFactory(
                new PropertyValueFactory<>("courseId"));

        TableColumn<Assignment, String> titleColumn =
                new TableColumn<>("Title");
        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>("title"));

        TableColumn<Assignment, String> dueDateColumn =
                new TableColumn<>("Due Date");
        dueDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("dueDate"));

        TableColumn<Assignment, Double> pointsColumn =
                new TableColumn<>("Max Points");
        pointsColumn.setCellValueFactory(
                new PropertyValueFactory<>("maxPoints"));

        assignmentTable.getColumns().addAll(
                idColumn,
                courseColumn,
                titleColumn,
                dueDateColumn,
                pointsColumn
        );

        Button addButton = new Button("Add");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");

        addButton.setOnAction(e -> addAssignment());
        editButton.setOnAction(e -> editAssignment());
        deleteButton.setOnAction(e -> deleteAssignment());

        HBox buttons = new HBox(10);
        buttons.getChildren().addAll(
                addButton,
                editButton,
                deleteButton
        );

        statusLabel = new Label();

        setSpacing(10);

        getChildren().addAll(
                titleLabel,
                courseIdField,
                titleField,
                descriptionField,
                dueDateField,
                maxPointsField,
                buttons,
                assignmentTable,
                statusLabel
        );

        loadAssignments();
    }

    private void loadAssignments() {
        assignmentTable.setItems(
                FXCollections.observableArrayList(
                        assignmentDao.findAll()
                )
        );
    }

    private void addAssignment() {

        try {
            int courseId =
                    Integer.parseInt(courseIdField.getText());

            double maxPoints =
                    Double.parseDouble(maxPointsField.getText());

            if (titleField.getText().isBlank()) {
                statusLabel.setText("Enter a title.");
                return;
            }

            Assignment assignment = new Assignment(
                    courseId,
                    titleField.getText(),
                    descriptionField.getText(),
                    dueDateField.getText(),
                    maxPoints
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
                    "Course ID and Max Points must be numbers."
            );
        }
    }

    private void editAssignment() {

        Assignment assignment =
                assignmentTable.getSelectionModel().getSelectedItem();

        if (assignment == null) {
            statusLabel.setText("Select an assignment.");
            return;
        }

        try {
            assignment.setCourseId(
                    Integer.parseInt(courseIdField.getText()));

            assignment.setTitle(titleField.getText());
            assignment.setDescription(descriptionField.getText());
            assignment.setDueDate(dueDateField.getText());

            assignment.setMaxPoints(
                    Double.parseDouble(maxPointsField.getText()));

            if (assignmentDao.update(assignment)) {
                statusLabel.setText("Assignment updated.");
                loadAssignments();
                clearFields();
            }

        } catch (NumberFormatException e) {
            statusLabel.setText(
                    "Course ID and Max Points must be numbers."
            );
        }
    }

    private void deleteAssignment() {

        Assignment assignment =
                assignmentTable.getSelectionModel().getSelectedItem();

        if (assignment == null) {
            statusLabel.setText("Select an assignment.");
            return;
        }

        if (assignmentDao.delete(
                assignment.getAssignmentId())) {

            statusLabel.setText("Assignment deleted.");
            loadAssignments();
            clearFields();
        }
    }

    private void clearFields() {
        courseIdField.clear();
        titleField.clear();
        descriptionField.clear();
        dueDateField.clear();
        maxPointsField.clear();
    }
}