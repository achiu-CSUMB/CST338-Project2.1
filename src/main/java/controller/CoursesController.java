package controller;

import factory.SceneFactory;
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
import model.Course;
import model.User;
import service.CourseService;

/**
 * @author Dominic Casoli
 * <br>
 * created: 8/4/2026
 * @since '1.0-SNAPSHOT'
 * Description: Controls the model.Course Management JavaFX interface.
 * Handles the user interactions for creating courses and loading the course data from database.
 */

public class CoursesController {
    private User currentUser;
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
    private TableColumn<Course, String> instructorColumn;

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

        instructorColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getPrefix() + " " +
                                data.getValue().getTeacherName()
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
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();

        SceneFactory sceneFactory = new SceneFactory(stage, currentUser);

        sceneFactory.showScene(SceneFactory.SceneType.MAIN_MENU);
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
        if(currentUser == null){
            System.out.println("No logged in user.");
                    return;
        }

        if (courseNameField.getText().isBlank() || capacityField.getText().isBlank()) {
            System.out.println("All fields are required");
            return;
        }

        try {
            Course course = new Course(
                    courseNameField.getText(),
                    Integer.parseInt(capacityField.getText())
            );

            course.setPrefix(currentUser.getPrefix());

            course.setTeacherName(currentUser.getTeacherName());

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
        selectedCourse.setPrefix(
                currentUser.getPrefix()
        );
        selectedCourse.setTeacherName(
                currentUser.getTeacherName()
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

    /**
     *
     * @param user the user object to set as current.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
