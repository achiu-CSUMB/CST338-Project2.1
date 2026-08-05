import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class AssignmentsView extends VBox {

    private TableView<Object> assignmentTable;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    private Label titleLabel;

    public AssignmentsView() {
        titleLabel = new Label("Assignments");
        assignmentTable = new TableView<>();

        addButton = new Button("Add");
        editButton = new Button("Edit");
        deleteButton = new Button("Delete");

        getChildren().addAll(
                titleLabel,
                assignmentTable,
                addButton,
                editButton,
                deleteButton
        );
    }
}