# CST338-Project2.1

# Grade and Assignment Tracker

Grade and Assignment Tracker that allows students and teachers to manage accounts, courses, assignments, grades, and academic records. CST 338 Project 2 — Team **Quintate**.

## Team & Slice Ownership
| Slice | Owner | GitHub username | Issues | Branch(es) | PR(s) | Enhancement chosen | Status |
|-------|-------|-----------------|--------|------------|-------|--------------------|--------|
| 1 — Accounts | John Ly | 6e4st | #7, #8, #9, #36, #39 | john/user-dao, john/account-scenes, john/account-auth, john/account-management, john/account-tests, john/account-service, john/teacher-name, john/menu | #15, #19, #35, #37, #38, #40 | Admin role and password reset; teacher prefix/name; main menu/account integration | complete |
| 2 — Courses & Enrollment |Dominic Casoli |dcasoliprettyawesome|#4, #5, #6, #30, #32 |dominic/course-management-ui, dominic/course-enrollment-database |18(Reviewed by Alvin), 23(Reviewed by John), 25(Reviewed by John), 26(Reviewed by Alvin), 33(Reviewed by ___), 34 (Reviewed by ___) |Waitlists, Capacity Limits | planned |
| 3 – Assignments | Oswald Perales | operalescs | #20, #21, #22 | oswald/assignments-skeleton | #24 (Reviewed by Alvin) | Assignment table and DAO test | in-progress |
| 4 — Grades & Statistics |Alvin Chiu  |achiu-CSUMB |#1,#2,#3, #12 |alvin/FXskeleton, alvin/grade-ui, alvin/grade-and-DB, alvin/Grade-Skeleton |#14,#17, #31 |TableView / ListView populated with live data | in-progress |

_Status values: planned · in-progress · complete_

## WILL NOT DO (declared scope cuts)
_Slices and beyond-scope items we are consciously NOT building. Move an item to a tracked
Issue if the team later decides to attempt it for extra credit._

- Slice 5 — Attendance and Reports: not building because of team size.
- Slice 2 — Sections: out of scope + time constraints.
- Slice 3 — Due-date reminders, attachments, and weighted categories: out of scope.

## Code Review Log
| PR | Author | Human reviewer(s) | AI review (link) | Outcome |
|----|--------|-------------------|------------------|---------|
| #23 — Dominic/course management UI | Dominic Casoli | John Ly | — | Approved and merged |
| #25 — dominic/course-enrollment-database | Dominic Casoli | John Ly | — | Feedback accepted, approved, and merged |

## AI Usage Log
- | John Ly | ChatGPT | Drafted TestFX registration tests; reviewed and corrected ComboBox interaction, expected messages, User import, and H2 test isolation | [Curated Tests / TESTING.md](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/38/changes/1ae9a6030faeae71aa938898ee4a80f08e3f3e82) | [Testing Commit](https://github.com/achiu-CSUMB/CST338-Project2.1/commit/bc1ad70eb5561c33fc37b3d63705eb4d30f3c16f) |
- **AI code reviews:** [PR link](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/34) + Slice 2 (Courses & Enrollment), covers EnrollmentController, Enrollment, EnrollmentControllerTest, and EnrollmentServiceTest - Dominic
- **AI code reviews:** [PR #37 AI review](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/37#issuecomment-5270511821) + Slice 1 (Accounts), covers AccountsController, role updates, account CRUD, and password reset - John
- **AI code reviews:** [PR #24 AI review](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/24#issuecomment-5275939228) + Slice 3 (Assignments), covers `AssignmentDao.findById()` and database error handling - Oswald
## Extra Credit Log

| Item | Who | Evidence (Issue/PR) |
| ---- | --- | ------------------- |
| Password reset enhancement for Accounts | John Ly |#36, #37 |
| User role/admin management enhancement | John Ly |#36, #37 |

## Build & Run

### Windows PowerShell

```text
.\gradlew run
.\gradlew test
```
macOS / Linux
```text
./gradlew run
./gradlew test
```
### Requirements

- JDK 25
- JavaFX 25
- Gradle wrapper included
- SQLite for application data
- H2 in-memory database for automated tests
- 
Setup
Clone the repository.
Open the project in IntelliJ IDEA.
Set the project SDK to JDK 25.
Allow Gradle to download the required dependencies.
Run the application using the Gradle wrapper.
Run the full automated test suite before submission.
