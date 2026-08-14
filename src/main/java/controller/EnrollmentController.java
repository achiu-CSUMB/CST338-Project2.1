package controller;

import dao.CourseDao;
import factory.SceneFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import model.Course;
import model.Enrollment;
import model.User;
import service.EnrollmentService;
import java.util.ArrayList;

/**
 * @author Dominic Casoli
 * <br>
 * created: 8/9/2026
 * @since '1.0-SNAPSHOT'
 * Description: Controls the model.Enrollment Management JavaFX interface.
 * Handles enrollments for students, dropping courses, and displaying the enrollment data.
 */

public class EnrollmentController {

    private EnrollmentService enrollmentService;
    private CourseDao courseDao;
    private User currentUser;

    /**
     * Default constructor FX/FXML.
     */
    public EnrollmentController() {
    }

    /**
     * Constructor used for test.
     */
    public EnrollmentController(EnrollmentService enrollmentService, CourseDao courseDao) {
        this.enrollmentService = enrollmentService;
        this.courseDao = courseDao;
    }

    @FXML
    private TableView<Enrollment> enrollmentTable;

    @FXML
    private TableColumn<Enrollment, String> courseNameColumn;

    @FXML
    private TableView<Course> availableCoursesTable;

    @FXML
    private TableColumn<Course, String> availableCourseNameColumn;

    @FXML
    private TableColumn<Course, String> availableCourseInstructorColumn;

    @FXML
    private TableColumn<Course, Integer> availableCourseCapacityColumn;

    @FXML
    private TableColumn<Enrollment, String> statusColumn;

    @FXML
    private Button enrollButton;

    @FXML
    private Button dropButton;

    @FXML
    public void initialize() {

        if (enrollmentService == null) {
            enrollmentService = new EnrollmentService();
        }

        if(courseDao == null) {
            courseDao = new CourseDao();
        }

        courseNameColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getCourseName()
                )
        );

        statusColumn.setCellValueFactory(
                data -> {
                    Enrollment enrollment = data.getValue();

                    int position = enrollmentService.getWaitlistPosition(enrollment);

                    if (position == -1) {
                        return new SimpleStringProperty("Enrolled");
                    }

                    if (position == 0) {
                        return new SimpleStringProperty("Next on Waitlist");
                    }

                    return new SimpleStringProperty("Waitlisted (" + position + " ahead)"
                    );
                }
        );

        availableCourseNameColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getCourseName()
                )
        );

        availableCourseCapacityColumn.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getCapacity()
                ).asObject()
        );

        availableCourseInstructorColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getPrefix() + " " + data.getValue().getTeacherName()
                )
        );

        enrollButton.setOnAction(e -> enrollStudent());

        dropButton.setOnAction(e -> dropStudent());

        loadCourses();
    }

    @FXML
    private void goBackToLogin(ActionEvent event) {
        Stage stage = (Stage) enrollButton.getScene().getWindow();

        SceneFactory sceneFactory = new SceneFactory(stage, currentUser);

        sceneFactory.showScene(SceneFactory.SceneType.MAIN_MENU);
    }

    public void loadEnrollments() {
        if (currentUser == null) {
            System.out.println("No logged in user.");
            return;
        }
        ArrayList<Enrollment> enrollments = enrollmentService.getStudentEnrollments(currentUser.getUserId());

        ObservableList<Enrollment> enrollmentList = FXCollections.observableArrayList(enrollments);

        enrollmentTable.setItems(enrollmentList);
    }

    public void enrollStudent() {
        if (currentUser == null) {
            System.out.println("No logged in user.");
            return;
        }
        int studentId = currentUser.getUserId();

        Course selectedCourse = availableCoursesTable.getSelectionModel().getSelectedItem();

        if (selectedCourse == null) {
            System.out.println("No course selected.");
            return;
        }

        int courseId = selectedCourse.getCourseId();


        if (enrollmentService.enrollStudent(studentId, courseId)) {
            loadEnrollments();

            Enrollment newestEnrollment = null;

            for (Enrollment enrollment : enrollmentService.getAllEnrollments()) {
                if (enrollment.getStudentId() == studentId
                        && enrollment.getCourseId() == courseId) {
                    newestEnrollment = enrollment;
                    break;
                }
            }

            if (newestEnrollment != null) {
                if (newestEnrollment.isWaitlisted()) {
                    System.out.println("Student added to waitlist");
                } else {
                    System.out.println("Student enrolled");
                }
            }
        }
        else {
            System.out.println("Enrollment failed or student already enrolled");
        }
    }

    public void setEnrollmentService(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    public void setCourseDao(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadEnrollments();
    }

    public void loadCourses() {
        ArrayList<Course> courses =
                courseDao.getAllCourses();

        ObservableList<Course> courseList =
                FXCollections.observableArrayList(courses);

        availableCoursesTable.setItems(courseList);
    }

    private void dropStudent() {
        Enrollment selected =
                enrollmentTable.getSelectionModel()
                        .getSelectedItem();

        if(selected == null) {
            System.out.println("No enrollment selected");
            return;
        }

        if(selected.getStudentId() != currentUser.getUserId()) {
            System.out.println("Cannot drop another student's enrollment.");
            return;
        }

        if(enrollmentService.dropStudent(selected.getEnrollmentId())) {
            loadEnrollments();
            System.out.println("Enrollment removed");
        }
        else {
            System.out.println("Delete failed");
        }
    }
}
