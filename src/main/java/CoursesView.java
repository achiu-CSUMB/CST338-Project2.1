import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

/**
 * @author Dominic Casoli
 * <br>
 * created: 8/2/2026
 * @since '1.0-SNAPSHOT'
 * Description: Displays Course Management interface. Allows teachers to create, edit, and remove courses...
 * ...while displaying the current course list to users.
 */
public class CoursesView extends VBox {

    private TableView<Course> courseTable;


    private Button addButton;
    private Button editButton;
    private Button deleteButton;


    private Label titleLabel;

    /**
     * Creates the course management view.
     */
    public CoursesView() {

        titleLabel = new Label("Course Management");
        courseTable = new TableView<>();
        addButton = new Button("Add Course");
        editButton = new Button("Edit Course");
        deleteButton = new Button("Delete Course");


        getChildren().addAll(
                titleLabel,
                courseTable,
                addButton,
                editButton,
                deleteButton
        );


        addButton.setOnAction(e -> {
            System.out.println("Add course clicked.");
        });

        editButton.setOnAction(e -> {
            System.out.println("Edit course clicked.");
        });

        deleteButton.setOnAction(e -> {
            System.out.println("Delete course clicked.");
        });

        setSpacing(10);
        setPadding(new Insets(20));
    }
}
