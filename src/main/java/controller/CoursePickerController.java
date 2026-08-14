package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dao.CourseDao;
import dao.EnrollmentDao;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import factory.SceneFactory;
import model.Course;
import model.Enrollment;
import model.Grade;
import model.User;
import service.GradeService;

/**
 * Description: Controller for course-picker-view.fxml. Shown after clicking
 * "Grades" or "Assignments" from the main menu so the user can choose which
 * course they want before landing on the relevant screen: grades-view.fxml
 * / grades-student-view.fxml for Grades, or assignments-view.fxml (scoped
 * to that course) for Assignments.
 */
public class CoursePickerController {

    /**
     * Which screen this picker should open once a course is selected.
     */
    public enum Mode {
        GRADES,
        ASSIGNMENTS
    }

    @FXML private Label headerLabel;
    @FXML private ListView<Course> courseListView;
    @FXML private Button selectCourseButton;
    @FXML private Button backButton;

    private final CourseDao courseDao = new CourseDao();
    private final EnrollmentDao enrollmentDao = new EnrollmentDao();
    private final GradeService gradeService = new GradeService();

    private User currentUser;
    private Mode mode = Mode.GRADES;

    @FXML
    public void initialize() {
        courseListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                } else {
                    String prefix = course.getPrefix() != null ? course.getPrefix() + " " : "";
                    setText(prefix + course.getCourseName());
                }
            }
        });
    }

    /**
     * Sets which screen this picker opens once a course is selected.
     * Call this before, or after, setCurrentUser — order doesn't matter.
     */
    public void setMode(Mode mode) {
        this.mode = mode;
        selectCourseButton.setText(mode == Mode.GRADES ? "View Grades" : "View Assignments");
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadCourses();
    }

    private void loadCourses() {
        if (currentUser == null) {
            return;
        }

        List<Course> courses = new ArrayList<>();

        if (currentUser.getRole().equalsIgnoreCase("TEACHER")) {
            headerLabel.setText("Select One of Your Courses");
            for (Course course : courseDao.getAllCourses()) {
                if (course.getTeacherName() != null
                        && course.getTeacherName().equalsIgnoreCase(currentUser.getTeacherName())) {
                    courses.add(course);
                }
            }
        } else if (currentUser.getRole().equalsIgnoreCase("STUDENT")) {
            headerLabel.setText("Select a Course You're Enrolled In");
            for (Enrollment enrollment : enrollmentDao.getStudentEnrollments(currentUser.getUserId())) {
                Course course = courseDao.findById(enrollment.getCourseId());
                if (course != null) {
                    courses.add(course);
                }
            }
        }

        courseListView.setItems(FXCollections.observableArrayList(courses));
    }

    @FXML
    private void handleSelectCourse(ActionEvent event) {
        Course selectedCourse = courseListView.getSelectionModel().getSelectedItem();

        if (selectedCourse == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Course Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a course first.");
            alert.showAndWait();
            return;
        }

        if (mode == Mode.GRADES) {
            openGrades(selectedCourse);
        } else {
            openAssignments(selectedCourse);
        }
    }

    private void openGrades(Course selectedCourse) {
        boolean isTeacher = currentUser.getRole().equalsIgnoreCase("TEACHER");

        // Teachers pick which assignment they want to view grades for
        // before landing on grades-view.fxml. Students only ever see
        // their own grade, so they go straight to grades-student-view.fxml.
        if (isTeacher) {
            openAssignmentPicker(selectedCourse);
            return;
        }

        String courseId = String.valueOf(selectedCourse.getCourseId());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/grades-student-view.fxml"));
            Parent root = loader.load();

            GradeController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setGrades(fetchStudentGrade(courseId));

            Stage stage = (Stage) selectCourseButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAssignmentPicker(Course selectedCourse) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/assignment-picker-view.fxml"));
            Parent root = loader.load();

            AssignmentPickerController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setCourse(selectedCourse);

            Stage stage = (Stage) selectCourseButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAssignments(Course selectedCourse) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/assignments-view.fxml"));
            Parent root = loader.load();

            AssignmentsController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setCourseFilter(selectedCourse.getCourseId());

            Stage stage = (Stage) selectCourseButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Grade> fetchStudentGrade(String courseId) {
        String studentId = String.valueOf(currentUser.getUserId());
        Grade grade = gradeService.getGradeForStudent(courseId, studentId);
        return grade != null ? Collections.singletonList(grade) : Collections.emptyList();
    }

    @FXML
    private void goBackToLogin(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

        SceneFactory sceneFactory = new SceneFactory(stage, currentUser);

        sceneFactory.showScene(SceneFactory.SceneType.MAIN_MENU);
    }
}
