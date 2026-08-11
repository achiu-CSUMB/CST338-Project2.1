import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;

/**
 * @author Dominic Casoli
 * <br>
 * created: 8/9/2026
 * @since '1.0-SNAPSHOT'
 * Description: Controls the Enrollment Management JavaFX interface.
 * Handles enrollments for students, dropping courses, and displaying the enrollment data.
 */

public class EnrollmentController {

    private EnrollmentService enrollmentService;
    private CourseDao courseDao;

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
    private TableColumn<Enrollment, Integer> enrollmentIdColumn;

    @FXML
    private TableColumn<Enrollment, Integer> studentIdColumn;

    @FXML
    private TableColumn<Enrollment, Integer> courseIdColumn;

    @FXML
    private TableColumn<Enrollment, String> courseNameColumn;

    @FXML
    private TableView<Course> availableCoursesTable;

    @FXML
    private TableColumn<Course, Integer> availableCourseIdColumn;

    @FXML
    private TableColumn<Course, String> availableCourseNameColumn;

    @FXML
    private TableColumn<Course, Integer> availableTeacherIdColumn;

    @FXML
    private TableColumn<Enrollment, Boolean> waitlistedColumn;

    @FXML
    private TextField studentIdField;

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

        enrollmentIdColumn.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getEnrollmentId()
                ).asObject()
        );

        studentIdColumn.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getStudentId()
                ).asObject()
                );

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

        waitlistedColumn.setCellValueFactory(
                data -> new SimpleBooleanProperty(
                        data.getValue().isWaitlisted()
                ).asObject()
        );

        availableCourseIdColumn.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getCourseId()
                ).asObject()
        );

        availableCourseNameColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getCourseName()
                )
        );

        availableTeacherIdColumn.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getTeacherId()
                ).asObject()
        );

        enrollButton.setOnAction(e -> enrollStudent());

        dropButton.setOnAction(e -> dropStudent());

        loadEnrollments();
        loadCourses();
    }

    public void loadEnrollments() {
        ArrayList<Enrollment> enrollments =
                enrollmentService.getAllEnrollments();

        ObservableList<Enrollment> enrollmentList =
                FXCollections.observableArrayList(enrollments);

        enrollmentTable.setItems(enrollmentList);
    }

    public void enrollStudent() {
        int studentId;

        try{
            studentId = Integer.parseInt(studentIdField.getText());
        }
        catch(NumberFormatException e){
            System.out.println("Student ID must be a number");
            return;
        }

        Course selectedCourse =
                availableCoursesTable
                        .getSelectionModel()
                        .getSelectedItem();

        if(selectedCourse == null) {
            System.out.println("No course selected");
            return;
        }

        int courseId =
                selectedCourse.getCourseId();


        if(enrollmentService.enrollStudent(studentId, courseId)) {
        loadEnrollments();
        System.out.println("Student enrolled");
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

        if(enrollmentService.dropStudent(
                selected.getEnrollmentId()
        )) {
            loadEnrollments();
            System.out.println("Enrollment removed");
        }
        else {
            System.out.println("Delete failed");
        }
    }
}
