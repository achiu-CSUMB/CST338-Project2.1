package service;

import dao.GradeDao;
import model.Grade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GradeService, using an in-memory fake GradeDao (see
 * {@link FakeGradeDao} below) instead of a real database, via the
 * GradeService(GradeDao) constructor added for testing. This isolates
 * GradeService's own logic (letter grades, statistics, insert-vs-update
 * decisions) from GradeDao/SQL concerns, which are covered separately by
 * GradeDaoTest.
 */
class GradeServiceTest {

    private FakeGradeDao fakeGradeDao;
    private GradeService gradeService;

    @BeforeEach
    void setUp() {
        fakeGradeDao = new FakeGradeDao();
        gradeService = new GradeService(fakeGradeDao);
    }

    @Test
    void saveGrade_insertsWhenNoExistingGradeForThatAssignment() {
        Grade grade = new Grade("1", "2", "3", 88.0);

        boolean saved = gradeService.saveGrade(grade);

        assertTrue(saved);
        assertEquals(1, fakeGradeDao.insertCallCount);
        assertEquals(0, fakeGradeDao.updateCallCount);
    }

    @Test
    void saveGrade_updatesWhenAGradeAlreadyExistsForThatAssignment() {
        Grade existing = new Grade("1", "2", "3", 70.0);
        fakeGradeDao.seed(existing);

        Grade edited = new Grade("1", "2", "3", 95.0);
        boolean saved = gradeService.saveGrade(edited);

        assertTrue(saved);
        assertEquals(0, fakeGradeDao.insertCallCount);
        assertEquals(1, fakeGradeDao.updateCallCount);
    }

    @Test
    void saveGrade_returnsFalse_whenDaoThrowsSqlException() {
        fakeGradeDao.failOnNextCall = true;
        Grade grade = new Grade("1", "2", "3", 88.0);

        boolean saved = gradeService.saveGrade(grade);

        assertFalse(saved);
    }

    @Test
    void getGradesForCourse_returnsWhatTheDaoHas() {
        fakeGradeDao.seed(new Grade("1", "2", "3", 88.0));
        fakeGradeDao.seed(new Grade("1", "5", "3", 60.0));
        fakeGradeDao.seed(new Grade("9", "2", "3", 100.0)); // different course

        List<Grade> results = gradeService.getGradesForCourse("1");

        assertEquals(2, results.size());
    }

    @Test
    void getGradesForCourse_returnsEmptyList_whenDaoThrowsSqlException() {
        fakeGradeDao.failOnNextCall = true;

        List<Grade> results = gradeService.getGradesForCourse("1");

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void getGradesForAssignment_scopesToCourseAndAssignment() {
        fakeGradeDao.seed(new Grade("1", "2", "3", 88.0));
        fakeGradeDao.seed(new Grade("1", "2", "4", 70.0)); // different assignment

        List<Grade> results = gradeService.getGradesForAssignment("1", "3");

        assertEquals(1, results.size());
        assertEquals("3", results.get(0).getAssignmentId());
    }

    @Test
    void getGradesForStudentInCourse_returnsAllAssignmentsForThatStudent() {
        fakeGradeDao.seed(new Grade("1", "2", "3", 88.0));
        fakeGradeDao.seed(new Grade("1", "2", "4", 70.0));
        fakeGradeDao.seed(new Grade("1", "5", "3", 60.0)); // different student

        List<Grade> results = gradeService.getGradesForStudentInCourse("1", "2");

        assertEquals(2, results.size());
    }

    @Test
    void calculateLetterGrade_bordersAreInclusive() {
        assertEquals("A", gradeService.calculateLetterGrade(90));
        assertEquals("A", gradeService.calculateLetterGrade(100));
        assertEquals("B", gradeService.calculateLetterGrade(89.99));
        assertEquals("B", gradeService.calculateLetterGrade(80));
        assertEquals("C", gradeService.calculateLetterGrade(79.99));
        assertEquals("C", gradeService.calculateLetterGrade(70));
        assertEquals("D", gradeService.calculateLetterGrade(69.99));
        assertEquals("D", gradeService.calculateLetterGrade(60));
        assertEquals("F", gradeService.calculateLetterGrade(59.99));
        assertEquals("F", gradeService.calculateLetterGrade(0));
    }

    @Test
    void calculateMedian_oddNumberOfGrades_returnsMiddleValue() {
        List<Grade> grades = List.of(
                new Grade("1", "2", "3", 70.0),
                new Grade("1", "5", "3", 90.0),
                new Grade("1", "6", "3", 80.0)
        );

        assertEquals(80.0, gradeService.calculateMedian(grades));
    }

    @Test
    void calculateMedian_evenNumberOfGrades_returnsAverageOfMiddleTwo() {
        List<Grade> grades = List.of(
                new Grade("1", "2", "3", 60.0),
                new Grade("1", "5", "3", 70.0),
                new Grade("1", "6", "3", 90.0),
                new Grade("1", "7", "3", 100.0)
        );

        assertEquals(80.0, gradeService.calculateMedian(grades));
    }

    @Test
    void calculateMedian_emptyOrNullList_returnsZero() {
        assertEquals(0.0, gradeService.calculateMedian(new ArrayList<>()));
        assertEquals(0.0, gradeService.calculateMedian(null));
    }

    @Test
    void getSummaryStatistics_reportsCountAverageMinMax() {
        List<Grade> grades = List.of(
                new Grade("1", "2", "3", 60.0),
                new Grade("1", "5", "3", 80.0),
                new Grade("1", "6", "3", 100.0)
        );

        DoubleSummaryStatistics stats = gradeService.getSummaryStatistics(grades);

        assertEquals(3, stats.getCount());
        assertEquals(60.0, stats.getMin());
        assertEquals(100.0, stats.getMax());
        assertEquals(80.0, stats.getAverage());
    }

    /**
     * In-memory stand-in for GradeDao. Overrides every method GradeService
     * calls so no real database or connection is ever touched. Set
     * failOnNextCall to true to make the next call throw SQLException, to
     * exercise GradeService's error-handling branches.
     */
    private static class FakeGradeDao extends GradeDao {

        private final Map<String, Grade> store = new HashMap<>();
        int insertCallCount = 0;
        int updateCallCount = 0;
        boolean failOnNextCall = false;

        FakeGradeDao() {
            super(null);
        }

        void seed(Grade grade) {
            store.put(key(grade.getCourseId(), grade.getStudentId(), grade.getAssignmentId()), grade);
        }

        private String key(String courseId, String studentId, String assignmentId) {
            return courseId + ":" + studentId + ":" + assignmentId;
        }

        private void maybeFail() throws SQLException {
            if (failOnNextCall) {
                failOnNextCall = false;
                throw new SQLException("simulated failure");
            }
        }

        @Override
        public void insert(Grade grade) throws SQLException {
            maybeFail();
            insertCallCount++;
            seed(grade);
        }

        @Override
        public void update(Grade grade) throws SQLException {
            maybeFail();
            updateCallCount++;
            seed(grade);
        }

        @Override
        public Grade find(String courseId, String studentId, String assignmentId) throws SQLException {
            maybeFail();
            return store.get(key(courseId, studentId, assignmentId));
        }

        @Override
        public Grade find(String courseId, String studentId) throws SQLException {
            maybeFail();
            return store.values().stream()
                    .filter(g -> g.getCourseId().equals(courseId) && g.getStudentId().equals(studentId))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<Grade> findByCourseId(String courseId) throws SQLException {
            maybeFail();
            return store.values().stream()
                    .filter(g -> g.getCourseId().equals(courseId))
                    .toList();
        }

        @Override
        public List<Grade> findByCourseIdAndAssignmentId(String courseId, String assignmentId) throws SQLException {
            maybeFail();
            return store.values().stream()
                    .filter(g -> g.getCourseId().equals(courseId) && g.getAssignmentId().equals(assignmentId))
                    .toList();
        }

        @Override
        public List<Grade> findByCourseIdAndStudentId(String courseId, String studentId) throws SQLException {
            maybeFail();
            return store.values().stream()
                    .filter(g -> g.getCourseId().equals(courseId) && g.getStudentId().equals(studentId))
                    .toList();
        }
    }
}
