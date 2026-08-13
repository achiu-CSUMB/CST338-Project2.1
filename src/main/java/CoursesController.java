import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.util.ArrayList;

/**
 * @author Dominic Casoli
 * <br>
 * created: 8/4/2026
 * @since '1.0-SNAPSHOT'
 * Description: Controls the Course Management JavaFX interface.
 * Handles the user interactions for creating courses and loading the course data from database.
 */

public class CoursesController {

    private CourseService courseService;

    @FXML
    private TableView<Course> courseTable;

    @FXML
    private TableColumn<Course, Integer> courseIdColumn;

    @FXML
    private TableColumn<Course, String> courseNameColumn;

    @FXML
    private TableColumn<Course, Integer> capacityColumn;

    @FXML
    private TextField courseNameField;

    @FXML
    private TextField capacityField;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button updateButton;

    /**
     * Initialize course screen.
     */
    @FXML
    public void initialize() {
        if (courseService == null) {
            courseService = new CourseService();
        }

        courseIdColumn.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getCourseId()
                ).asObject()
        );

        courseNameColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getCourseName()
                )
        );

        capacityColumn.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getCapacity()
                ).asObject()
        );

        addButton.setOnAction(e -> addCourse());

        updateButton.setOnAction(e -> updateCourse());

        deleteButton.setOnAction(e -> deleteCourse());

        courseTable.getSelectionModel().selectedItemProperty().addListener((observable, oldCourse, newCourse) -> {
            if (newCourse != null) {
                courseNameField.setText(
                        newCourse.getCourseName()
                );

                capacityField.setText(
                        String.valueOf(
                                newCourse.getCapacity()
                        )
                );
            }
        });

        loadCourses();
    }


    @FXML
    private void goBackToLogin(ActionEvent event) {
        Stage stage = (Stage) addButton.getScene().getWindow();

        SceneFactory sceneFactory = new SceneFactory(stage);

        sceneFactory.showScene(SceneFactory.SceneType.LOGIN);
    }

    private void loadCourses() {
        ArrayList<Course> courses = courseService.getAllCourses();

        ObservableList<Course> courseList =
                FXCollections.observableArrayList(courses);

        courseTable.setItems(courseList);
    }

    /**
     * Adds a course.
     */
    private void addCourse() {
        if (courseNameField.getText().isBlank() || capacityField.getText().isBlank()) {
            System.out.println("All fields are required");
            return;
        }

        try {
            Course course = new Course(
                    courseNameField.getText(),
                    Integer.parseInt(capacityField.getText())
            );

            boolean created = courseService.createCourse(course);

            if (created) {
                loadCourses();
                System.out.println("Course added successfully");
            }
            else {
                System.out.println("Failed to add course.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Capacity must use numbers.");
        }
    }

    /**
     * Deletes a course.
     */
    private void deleteCourse() {
        Course selectedCourse =
                courseTable.getSelectionModel().getSelectedItem();

        if (selectedCourse == null) {
            System.out.println(
                    "No course selected"
            );
            return;
        }
        boolean deleted =
                courseService.deleteCourse(
                        selectedCourse.getCourseId()
                );
        if (deleted) {
            loadCourses();
            System.out.println(
                    "Course deleted"
            );
        } else {
            System.out.println(
                    "Failed to delete course"
            );
        }
    }

    /**
     * Updates a course.
     */
    private void updateCourse() {
        Course selectedCourse =
                courseTable.getSelectionModel().getSelectedItem();

        if (selectedCourse == null) {
            System.out.println(
                    "No course selected"
            );
            return;
        }
        selectedCourse.setCourseName(
                courseNameField.getText()
        );
        selectedCourse.setCapacity(
                Integer.parseInt(
                        capacityField.getText()
                )
        );
        boolean updated = courseService.updateCourse(selectedCourse);

        if (updated) {
            loadCourses();

            System.out.println(
                    "Course updated"
            );
        }
        else {
            System.out.println("Failed to update course");
        }
    }

    /**
     * Used for testing.
     */
    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }
}
