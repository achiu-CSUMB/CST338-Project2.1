import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class CoursesController {

    private CourseService courseService;

    @FXML
    private TableView<Course> courseTable;

    @FXML
    private TableColumn<Course, Integer> courseIdColumn;

    @FXML
    private TableColumn<Course, String> courseNameColumn;

    @FXML
    private TableColumn<Course, Integer> teacherIdColumn;

    @FXML
    private TextField courseNameField;

    @FXML
    private TextField teacherIdField;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    /**
     * Initialize course screen.
     */
    @FXML
    public void initialize() {
        courseService = new CourseService();

        // javafx beans fixed the issue of Table not loading the data.
        courseIdColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getCourseId()
                ).asObject()
        );

        courseNameColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCourseName()
                )
        );

        teacherIdColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getTeacherId()
                ).asObject()
        );

        addButton.setOnAction(e -> addCourse());

        deleteButton.setOnAction(e -> deleteCourse());

        loadCourses();
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
        Course course = new Course(
                courseNameField.getText(),
                Integer.parseInt(teacherIdField.getText())
        );

        boolean created = courseService.createCourse(course);

        if (created) {
            loadCourses();
            System.out.println("Course added");
        }
        else {
            System.out.println("Failed to add course");
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
}
