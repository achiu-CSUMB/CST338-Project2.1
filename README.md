# CST338-Project2.1
<!--
CST 338 Project 2 — README template.
Copy this file into the ROOT of your team's repository as README.md and keep it current.
This README is your project dashboard: it is the first thing the instructor reads when
grading, and a working, up-to-date README is part of your integration score.

GitHub Issues are your LIVE tracker — every slice task, enhancement, and scope decision is
an Issue: assigned to its owner, labeled (slice-1, testing, enhancement, will-not-do,
extra-credit), and closed by a PR via "Closes #N". The tables below link into those Issues
and PRs. Replace every <placeholder> and delete this comment before you submit.
-->

# Grade and Assignment Tracker

Grade and Assignment Tracker that allows students and teachers to manage accounts, courses, assignments, grades, and academic records. CST 338 Project 2 — Team **Quintate**.

## Team & Slice Ownership
| Slice | Owner | GitHub username | Issues | Branch(es) | PR(s) | Enhancement chosen | Status |
|-------|-------|-----------------|--------|------------|-------|--------------------|--------|
| 1 — Accounts |John Ly |6e4st |#7, #8, #9, #36 |john/user-dao, john/account-scene |#15, #19, #35, #37 |Admin role and password reset | planned |
| 2 — Courses & Enrollment |Dominic Casoli |dcasoliprettyawesome|#4, #5, #6, #30, #32 |dominic/course-management-ui, dominic/course-enrollment-database |18(Reviewed by Alvin), 23(Reviewed by John), 25(Reviewed by John), 26(Reviewed by Alvin), 33(Reviewed by ___), 34 (Reviewed by ___) |Waitlists | planned |
| 3 – Assignments | Oswald Perales | operalescs | #20, #21, #22 | oswald/assignments-skeleton | #24 (Reviewed by Alvin) | Assignment table and DAO test | in-progress |
| 4 — Grades & Statistics |Alvin Chiu  |achiu-CSUMB |#1,#2,#3, #12 |alvin/FXskeleton, alvin/grade-ui, alvin/grade-and-DB, alvin/Grade-Skeleton |#14,#17, #31 |TableView / ListView populated with live data | in-progress |

_Status values: planned · in-progress · complete_

## WILL NOT DO (declared scope cuts)
_Slices and beyond-scope items we are consciously NOT building. Move an item to a tracked
Issue if the team later decides to attempt it for extra credit._

- Slice 5 — Attendance and Reports: not building because of team size.
- Slice 2 — Sections, waitlists, and capacity limits: out of scope.
- Slice 3 — Due-date reminders, attachments, and weighted categories: out of scope.

## Code Review Log
| PR | Author | Human reviewer(s) | AI review (link) | Outcome |
|----|--------|-------------------|------------------|---------|
|  | | |  |  |

## AI Usage Log
- **AI-drafted tests:** <link to TESTING.md / commit> — per owner.
- **AI code reviews:** <[PR link](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/34) + Slice 2 (Courses & Enrollment), covers EnrollmentController, Enrollment, EnrollmentControllerTest, and EnrollmentServiceTest - Dominic

## Extra Credit Log
| Item | Who | Evidence (Issue/PR) |
|------|-----|---------------------|
|  | |  |

## Build & Run
```
./gradlew run        # launch the app
./gradlew test       # run the test suite
```
Requirements: JDK <version>, JavaFX <version>. Any setup notes go here.
