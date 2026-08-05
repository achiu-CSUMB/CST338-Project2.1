import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Author: Alvin Chiu
 * Created: 8/4/2026
 * Current version: V1.0 - 8/4/2026
 * Description: test for the Grade class.
 *

 */
public class GradeTest {

    private static final String COURSE_ID = "CST338";
    private static final String STUDENT_ID = "student01";

    private Grade grade;

    @BeforeEach
    void setUp() {
        grade = new Grade(COURSE_ID, STUDENT_ID, 90.0);
    }

    @Test
    @DisplayName("Constructor (double score) sets all fields correctly")
    void testDoubleConstructor() {
        assertEquals(COURSE_ID, grade.getCourseId());
        assertEquals(STUDENT_ID, grade.getStudentId());
        assertEquals(90.0, grade.getScore());
        assertEquals(LocalDate.now(), grade.getDate());
    }


    @Test
    @DisplayName("setScore updates the score when the value is within range")
    void testSetScoreValid() {
        grade.setScore(75);
        assertEquals(75, grade.getScore());
    }

    @Test
    @DisplayName("setScore accepts the minimum boundary value")
    void testSetScoreMinBoundary() {
        grade.setScore(0);
        assertEquals(0, grade.getScore());
    }

    @Test
    @DisplayName("setScore accepts the maximum boundary value")
    void testSetScoreMaxBoundary() {
        grade.setScore(100);
        assertEquals(100, grade.getScore());
    }

    @Test
    @DisplayName("setScore rejects a value below the minimum and leaves score unchanged")
    void testSetScoreBelowMin() {
        grade.setScore(-5);
        assertEquals(90.0, grade.getScore());
    }

    @Test
    @DisplayName("setScore rejects a value above the maximum and leaves score unchanged")
    void testSetScoreAboveMax() {
        grade.setScore(150);
        assertEquals(90.0, grade.getScore());
    }

    @Test
    @DisplayName("setScore prints an error message when the value is out of range")
    void testSetScoreInvalidPrintsError() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            grade.setScore(200);
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(outContent.toString().contains("Score must be between"));
    }

    @Test
    @DisplayName("setDate updates the entry date")
    void testSetDate() {
        LocalDate newDate = LocalDate.of(2026, 1, 15);
        grade.setDate(newDate);
        assertEquals(newDate, grade.getDate());
    }

    @Test
    @DisplayName("getCourseId returns the course id passed to the constructor")
    void testGetCourseId() {
        assertEquals(COURSE_ID, grade.getCourseId());
    }

    @Test
    @DisplayName("getStudentId returns the student id passed to the constructor")
    void testGetStudentId() {
        assertEquals(STUDENT_ID, grade.getStudentId());
    }
}