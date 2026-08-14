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

    // ------------------------------------------------------------------
    // AI-drafted-then-curated tests
    //
    // The two tests below started as an AI-generated first draft, then
    // were reviewed and corrected by hand. The original draft is kept
    // as a comment for each one so the mistake and the fix are both
    // visible in the diff, rather than silently replaced. See
    // REFLECTION.md for the write-up of this process.
    // ------------------------------------------------------------------

    /*
     * AI ORIGINAL DRAFT (incorrect — kept for reflection, not run):
     *
     * @Test
     * void saveGrade_whenFindThrows_fallsBackToInsert() {
     *     fakeGradeDao.failOnNextCall = true; // simulate find() throwing
     *     Grade grade = new Grade("1", "2", "3", 88.0);
     *
     *     boolean saved = gradeService.saveGrade(grade);
     *
     *     assertTrue(saved);
     *     assertEquals(1, fakeGradeDao.insertCallCount);
     * }
     *
     * Why this was wrong: it assumed saveGrade() treats a failed lookup
     * as "no existing grade found, so insert" and recovers. Reading
     * GradeService.saveGrade(), the find() call and the insert/update
     * call happen inside the SAME try block — if find() throws, control
     * jumps straight to the catch clause and returns false. Neither
     * insert() nor update() is attempted, and failOnNextCall is a
     * one-shot flag that resets itself once consumed. The AI drafted
     * a plausible-sounding "resilience" behavior the code doesn't
     * actually have. Corrected below to assert the real behavior.
     */
    @Test
    void saveGrade_whenFindThrows_returnsFalseWithoutAttemptingInsertOrUpdate() {
        fakeGradeDao.failOnNextCall = true; // find() throws
        Grade grade = new Grade("1", "2", "3", 88.0);

        boolean saved = gradeService.saveGrade(grade);

        assertFalse(saved);
        assertEquals(0, fakeGradeDao.insertCallCount);
        assertEquals(0, fakeGradeDao.updateCallCount);
    }

    /*
     * AI ORIGINAL DRAFT (incorrect expected value — kept for reflection):
     *
     * @Test
     * void calculateMedian_evenNumberOfGrades_alternateExpectedValue() {
     *     List<Grade> grades = List.of(
     *             new Grade("1", "2", "3", 60.0),
     *             new Grade("1", "5", "3", 70.0),
     *             new Grade("1", "6", "3", 90.0),
     *             new Grade("1", "7", "3", 100.0)
     *     );
     *     assertEquals(80.0, gradeService.calculateMedian(grades));
     * }
     *
     * This one actually landed on the right answer for the wrong
     * reason: the AI's stated calculation in its explanation averaged
     * all four scores ((60+70+90+100)/4 = 80), not the two middle
     * values after sorting ((70+90)/2 = 80) — they coincide here only
     * because the list was already sorted and evenly spread. Swapping
     * in an unsorted, unevenly-spread list exposes the difference: a
     * true median ignores the outlier at the low end, a mean does not.
     * Kept as a distinct edge case below instead of overwriting the
     * existing evenNumberOfGrades test.
     */
    @Test
    void calculateMedian_unsortedInputWithOutlier_ignoresOutlierUnlikeMean() {
        List<Grade> grades = List.of(
                new Grade("1", "2", "3", 100.0),
                new Grade("1", "5", "3", 5.0),   // outlier, pulls the mean down
                new Grade("1", "6", "3", 92.0),
                new Grade("1", "7", "3", 88.0)
        );

        double median = gradeService.calculateMedian(grades);
        double mean = grades.stream().mapToDouble(Grade::getScore).average().orElseThrow();

        assertEquals(90.0, median); // sorted: 5, 88, 92, 100 -> (88+92)/2
        assertNotEquals(mean, median, 0.0001);
    }

    /*
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
