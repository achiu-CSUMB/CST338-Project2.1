# Reflection: AI-Drafted-Then-Curated Tests

## What was AI-drafted

While building out the test suite for GradeDao, GradeService, and
GradeController, two tests in GradeServiceTest started as first drafts
written without checking them against the actual source line by line
first, then reviewed afterward. Both drafts are preserved as comments
directly above their corrected versions in GradeServiceTest.java
(saveGrade_whenFindThrows_... and calculateMedian_unsortedInputWithOutlier_...)
so the mistake and the fix are both visible, not just the final answer.

## Draft 1: saveGrade_whenFindThrows_fallsBackToInsert

The first draft assumed saveGrade() was resilient to a failed lookup --
that if gradeDao.find() threw, the service would reasonably treat that
as "no existing grade, so insert." That's a plausible design, but it's
not what the code does. Rereading GradeService.saveGrade():

    try {
        Grade existing = gradeDao.find(...);
        if (existing != null) { gradeDao.update(grade); } else { gradeDao.insert(grade); }
        return true;
    } catch (SQLException e) {
        return false;
    }

find() and the insert/update call are in the same try block. If find()
throws, execution jumps straight to catch -- insert() is never reached.
The draft's assertion (insertCallCount == 1) would have failed against
the real code, which is exactly the point of writing the test: it
caught a wrong assumption before it became a false sense of coverage.
The corrected version asserts what the code actually does -- returns
false, and neither insert() nor update() is called.

## Draft 2: calculateMedian even-count case

The second draft reused the four scores from the existing
calculateMedian_evenNumberOfGrades test and got the right numeric
answer (80.0), but for a coincidental reason: with that particular
list, the mean of all four scores and the median of the sorted middle
two happen to be equal. That's a case where a test can pass without
actually testing the thing it claims to test -- if calculateMedian() had
been buggy and silently computed a mean instead of a median, this draft
would not have caught it. The curated version keeps the original test
but adds a new one with an unsorted list containing a clear outlier,
where the mean and true median diverge (median 90.0 vs. mean 71.25), so
the assertion is actually discriminating between correct and incorrect
implementations.

## General takeaways

- AI-drafted tests are a good starting point for coverage breadth (edge
  cases, boundary values, "what if this list is empty" prompts) but a
  bad substitute for actually reading the implementation. Both mistakes
  above came from reasoning about what the code should plausibly do
  rather than what it does do -- the fix in both cases was going back
  to the source and tracing the exact control flow.
- A test that passes isn't automatically a good test. Draft 2 is the
  sharper example: it was "correct" in the sense of not failing, but it
  wasn't actually exercising the median logic in a way that would fail
  if the logic were wrong. Curating AI-drafted tests means asking "what
  would make this test fail?", not just "does this test pass?"
- Where AI-assistance genuinely helped: generating the boilerplate and
  enumerating edge cases to consider (FK violations, empty lists,
  boundary scores, null lists) was faster and more thorough than
  brainstorming them from scratch, and the DAO-level date-format bug
  found earlier in this project (the entry_date "Error parsing time
  stamp" issue) came directly out of writing a round-trip test for it --
  the kind of test that's easy to skip when writing tests by hand under
  time pressure, but easy to generate once someone thinks to ask "what
  happens when I read back what I just wrote?"
