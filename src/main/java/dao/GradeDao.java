package dao;

import database.DatabaseManager;
import model.Grade;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Alvin Chiu
 * Created: 8/4/2026
 * Current version: V1.0 - 8/4/2026
 * Description: Data Access Object for the model.Grade class. Handles CRUD
 * operations against a "grades" table keyed on (course_id, student_id).
 *
 */
public class GradeDao {

    private static final String TABLE_NAME = "grades";

    private Connection getConnection() {
        return DatabaseManager.getInstance().getConnection();
    }
    private final UserDao userDao = new UserDao();
    private final AssignmentDao assignmentDao = new AssignmentDao();
    /**
     * Inserts a new grade record.
     */
    public void insert(Grade grade) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME
                + " (course_id, student_id, assignment_id, score, entry_date) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(grade.getCourseId()));
            stmt.setInt(2, Integer.parseInt(grade.getStudentId()));
            stmt.setInt(3, Integer.parseInt(grade.getAssignmentId()));
            stmt.setDouble(4, grade.getScore());
            stmt.setString(5, grade.getDate().toString());
            stmt.executeUpdate();
        }
    }

    /**
     * Returns all grade records for a given student.
     */
    public List<Grade> findByStudentId(String studentId) throws SQLException {
        String sql = "SELECT course_id, student_id, assignment_id, score, entry_date FROM "
                + TABLE_NAME + " WHERE student_id = ?";

        List<Grade> results = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(studentId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    /**
     * Returns all grade records for a given course.
     */
    public List<Grade> findByCourseId(String courseId) throws SQLException {
        String sql = "SELECT course_id, student_id, assignment_id, score, entry_date FROM "
                + TABLE_NAME + " WHERE course_id = ?";

        List<Grade> results = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(courseId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    /**
     * Returns all grade records for a given course, scoped to a single
     * assignment. Used by the assignment picker so a teacher can view
     * grades for one assignment at a time instead of every assignment
     * mixed together.
     */
    public List<Grade> findByCourseIdAndAssignmentId(String courseId, String assignmentId) throws SQLException {
        String sql = "SELECT course_id, student_id, assignment_id, score, entry_date FROM "
                + TABLE_NAME + " WHERE course_id = ? AND assignment_id = ?";

        List<Grade> results = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(courseId));
            stmt.setInt(2, Integer.parseInt(assignmentId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    /**
     * Returns every grade record for a given student within a single
     * course, across all assignments. Used by a student's own grades view
     * so they see every assignment they've been graded on in that course,
     * not just one.
     */
    public List<Grade> findByCourseIdAndStudentId(String courseId, String studentId) throws SQLException {
        String sql = "SELECT course_id, student_id, assignment_id, score, entry_date FROM "
                + TABLE_NAME + " WHERE course_id = ? AND student_id = ?";

        List<Grade> results = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(courseId));
            stmt.setInt(2, Integer.parseInt(studentId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    /**
     * Returns the single grade record for a specific student in a specific
     * course, or null if none exists.
     */
    public Grade find(String courseId, String studentId) throws SQLException {
        String sql = "SELECT course_id, student_id, assignment_id, score, entry_date FROM "
                + TABLE_NAME + " WHERE course_id = ? AND student_id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(courseId));
            stmt.setInt(2, Integer.parseInt(studentId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Returns the single grade record for a specific student, in a specific
     * course, for a specific assignment, or null if none exists yet. Used
     * to decide whether an edited grade should be inserted or updated.
     */
    public Grade find(String courseId, String studentId, String assignmentId) throws SQLException {
        String sql = "SELECT course_id, student_id, assignment_id, score, entry_date FROM "
                + TABLE_NAME + " WHERE course_id = ? AND student_id = ? AND assignment_id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(courseId));
            stmt.setInt(2, Integer.parseInt(studentId));
            stmt.setInt(3, Integer.parseInt(assignmentId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Returns every grade record in the table.
     */
    public List<Grade> findAll() throws SQLException {
        String sql = "SELECT course_id, student_id, assignment_id, score, entry_date FROM " + TABLE_NAME;

        List<Grade> results = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    /**
     * Updates the score and entry date for an existing grade record,
     * scoped to a specific assignment so editing one assignment's grade
     * doesn't clobber a student's other grades in the same course.
     */
    public void update(Grade grade) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME
                + " SET score = ?, entry_date = ? WHERE course_id = ? AND student_id = ? AND assignment_id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setDouble(1, grade.getScore());
            stmt.setString(2, grade.getDate().toString());
            stmt.setInt(3, Integer.parseInt(grade.getCourseId()));
            stmt.setInt(4, Integer.parseInt(grade.getStudentId()));
            stmt.setInt(5, Integer.parseInt(grade.getAssignmentId()));
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a grade record for a specific student in a specific course.
     */
    public void delete(String courseId, String studentId) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE course_id = ? AND student_id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(courseId));
            stmt.setInt(2, Integer.parseInt(studentId));
            stmt.executeUpdate();
        }
    }

    private Grade mapRow(ResultSet rs) throws SQLException {
        Grade grade = new Grade(
                String.valueOf(rs.getInt("course_id")),
                String.valueOf(rs.getInt("student_id")),
                rs.getString("assignment_id"),
                rs.getDouble("score")
        );

        LocalDate entryDate = LocalDate.parse(rs.getString("entry_date"));
        grade.setDate(entryDate);

        User student = userDao.findById(rs.getInt("student_id"));
        if (student != null) {
            grade.setStudentName(student.getUsername());
        }

        model.Assignment assignment = assignmentDao.findById(rs.getInt("assignment_id"));
        if (assignment != null) {
            grade.setAssignmentTitle(assignment.getTitle());
            grade.setMaxPoints(assignment.getMaxPoints());
        }

        return grade;
    }
}