package service;

import dao.GradeDao;
import model.Grade;

import java.sql.SQLException;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: Provides the grade business logic between the grade-related
 * controllers (GradeController, CoursePickerController, StatisticsController)
 * and dao.GradeDao. Handles fetching grades for a course or student, and
 * shared calculations (letter grades, median, summary statistics) so that
 * logic isn't duplicated across controllers.
 */
public class GradeService {

    private GradeDao gradeDao;

    /**
     * Creates a service.GradeService object.
     */
    public GradeService() {
        gradeDao = new GradeDao();
    }

    /**
     * Creates a service.GradeService object using a provided DAO, used for testing.
     */
    public GradeService(GradeDao gradeDao) {
        this.gradeDao = gradeDao;
    }

    /**
     * Retrieves every grade recorded for a course (e.g. for a teacher's roster view).
     * Returns an empty list if none exist or the lookup fails.
     */
    public List<Grade> getGradesForCourse(String courseId) {
        try {
            return gradeDao.findByCourseId(courseId);
        } catch (SQLException e) {
            System.err.println("Could not retrieve grades for course: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves every grade recorded for a single assignment within a course
     * (e.g. for a teacher viewing grades scoped to one assignment via the
     * assignment picker). Returns an empty list if none exist or the lookup fails.
     */
    public List<Grade> getGradesForAssignment(String courseId, String assignmentId) {
        try {
            return gradeDao.findByCourseIdAndAssignmentId(courseId, assignmentId);
        } catch (SQLException e) {
            System.err.println("Could not retrieve grades for assignment: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Creates or updates a grade record, deciding automatically based on
     * whether a grade already exists for that student/course/assignment.
     * Used when a teacher edits a score in the grades table.
     * Returns true if the save succeeded.
     */
    public boolean saveGrade(Grade grade) {
        try {
            Grade existing = gradeDao.find(grade.getCourseId(), grade.getStudentId(), grade.getAssignmentId());

            if (existing != null) {
                gradeDao.update(grade);
            } else {
                gradeDao.insert(grade);
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Could not save grade: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a single student's grade in a course (e.g. for a student's own view).
     * Returns null if no grade exists or the lookup fails.
     */
    public Grade getGradeForStudent(String courseId, String studentId) {
        try {
            return gradeDao.find(courseId, studentId);
        } catch (SQLException e) {
            System.err.println("Could not retrieve grade for student: " + e.getMessage());
            return null;
        }
    }

    /**
     * Converts a numeric score into a letter grade.
     */
    public String calculateLetterGrade(double score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    /**
     * Calculates the median score across a list of grades.
     * Returns 0 for a null or empty list.
     */
    public double calculateMedian(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return 0;
        }

        List<Double> scores = grades.stream()
                .map(Grade::getScore)
                .sorted()
                .collect(Collectors.toList());

        int size = scores.size();
        int mid = size / 2;

        return (size % 2 == 0)
                ? (scores.get(mid - 1) + scores.get(mid)) / 2.0
                : scores.get(mid);
    }

    /**
     * Returns count/average/min/max statistics for a list of grades.
     */
    public DoubleSummaryStatistics getSummaryStatistics(List<Grade> grades) {
        return grades.stream()
                .mapToDouble(Grade::getScore)
                .summaryStatistics();
    }
}
