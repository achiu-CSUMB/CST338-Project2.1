import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

/**
 * @author Dominic Casoli
 * <br>
 * created: 8/2/2026
 * @since '1.0-SNAPSHOT'
 * Description: Displays enrollment interface. Lets students enroll in available courses, drop courses, and view their currently enrolled courses.
 */
public class EnrollmentView extends VBox {

    // Table displaying student enrollments.
    private TableView<Enrollment> enrollmentTable = new TableView<>();

    // Table columns.
    private TableColumn<Enrollment, Integer> enrollmentIdColumn;
    private TableColumn<Enrollment, Integer> studentIdColumn;
    private TableColumn<Enrollment, Integer> courseIdColumn;
    private TableColumn<Enrollment, Boolean> waitlistedColumn;

    // Buttons for enrollment actions.
    private Button enrollButton = new Button("Enroll");
    private Button dropButton = new Button("Drop");

    // Page title.
    private Label titleLabel = new Label("Enrollment Management");

    /**
     * Creates the enrollment view.
     */
    public EnrollmentView() {
        // Create JavaFX controls.
        enrollmentIdColumn = new TableColumn<>("Enrollment ID");
        studentIdColumn = new TableColumn<>("Student ID");
        courseIdColumn = new TableColumn<>("Course ID");
        waitlistedColumn = new TableColumn<>("Waitlisted");

        enrollmentTable.getColumns().addAll(
                enrollmentIdColumn,
                studentIdColumn,
                courseIdColumn,
                waitlistedColumn
        );

        // Configure table size.
        enrollmentTable.setPrefWidth(400);
        enrollmentTable.setPrefHeight(250);

        // Arrange controls.
        this.getChildren().addAll(
                titleLabel,
                enrollmentTable,
                enrollButton,
                dropButton
        );

        // Connect buttons to event handlers.
        enrollButton.setOnAction(e -> {
            System.out.println("Enroll button clicked");
        });

        dropButton.setOnAction(e -> {
            System.out.println("Drop button clicked");
        });
    }
}
