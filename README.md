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

A JavaFX grade and assignment tracking application that allows students and teachers to manage accounts, courses, assignments, grades, enrollment, and academic records.

CST 338 Project 2 — Team **Quintate**

## Team & Slice Ownership
| Slice | Owner | GitHub username | Issues | Branch(es) | PR(s) | Enhancement chosen | Status |
|-------|-------|-----------------|--------|------------|-------|--------------------|--------|
| 1 — Accounts |John Ly |6e4st |#7, #8, #9, #36, #39 |john/user-dao, john/account-scene |#15, #19, #35, #37, #38, #40 |Admin role and password reset | planned |
| 2 — Courses & Enrollment |Dominic Casoli |dcasoliprettyawesome|#4, #5, #6, #30, #32, #53 |dominic/course-management-ui, dominic/course-enrollment-database, dominic/enrollment-ui, dominic/enrollment-waitlist, dominic/final-project-polish |#18, #23, #25, #26, #33, #34, #41, #44, #47, #54 |Waitlists, Capacity Limits | finished |
| 3 – Assignments | Oswald Perales | operalescs | #20, #21, #22 | oswald/assignments-skeleton | #24 (Reviewed by Alvin) | Assignment table and DAO test | in-progress |
| 4 — Grades & Statistics |Alvin Chiu  |achiu-CSUMB |#1,#2,#3, #12 |alvin/FXskeleton, alvin/grade-ui, alvin/grade-and-DB, alvin/model.Grade-Skeleton |#14,#17, #31,#50,#51 |TableView / ListView populated with live data | in-progress |

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
|[#18](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/18)  |Dominic |None |——— |Merged  |
|[#23](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/23)  |Dominic |John |——— |Merged  |
|[#25](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/25)  |Dominic |John |——— |Merged  |
|[#26](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/26)  |Dominic |Alvin |——— |Merged  |
|[#33](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/33)  |Dominic |None |——— |Merged  |
|[#34](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/34)  |Dominic |None |(https://github.com/achiu-CSUMB/CST338-Project2.1/pull/34) |Merged  |
|[#41](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/41)  |Dominic |None |——— |Merged  |
|[#44](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/44)  |Dominic |None |——— |Merged  |
|[#47](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/47)  |Dominic |None |——— |Merged  |
|[#54](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/54)  |Dominic |None |——— |Merged  |

## AI Usage Log
| John Ly | ChatGPT | Drafted TestFX registration tests; reviewed and corrected ComboBox interaction, expected messages, User import, and H2 test isolation | [Curated Tests / TESTING.md](https://github.com/achiu-CSUMB/CST338-Project2.1/pull/38/changes/1ae9a6030faeae71aa938898ee4a80f08e3f3e82) | [Testing Commit](https://github.com/achiu-CSUMB/CST338-Project2.1/commit/bc1ad70eb5561c33fc37b3d63705eb4d30f3c16f) |
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
### macOS / Linux
```text
./gradlew run
./gradlew test
```
Requirements
JDK 25
JavaFX 25
Gradle wrapper included in the repository
No separate Gradle installation required
Setup Notes
Clone the repository.
Open the project in IntelliJ IDEA.
Make sure the project SDK is set to JDK 25.
Allow Gradle to download the required dependencies.
Run the application with the Gradle wrapper command shown above.
Run the test suite with the Gradle wrapper command shown above.

1. Clone the repository.
2. Open the project in IntelliJ IDEA.
3. Set the project SDK to JDK 25.
4. Allow Gradle to download the required dependencies.
5. Run the application using the Gradle wrapper.
6. Run the full automated test suite before submission.

### Default Demo Admin Account

A default administrator account is created automatically when the application database is initialized.

- Username: `admin`
- Password: `admin123`

The Admin account can be used to demonstrate account management features such as role updates.
