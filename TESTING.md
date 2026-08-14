# Testing Documentation

## Owner

John Ly

## Slice

Accounts

## Test Coverage

The Accounts slice includes automated tests for:

- User DAO CRUD operations using an in-memory H2 database
- Login behavior using TestFX
- Registration behavior using TestFX
- Student and Teacher account registration
- Teacher prefix and teacher-name validation
- Account management actions using TestFX
- Password reset
- Role updates
- User deletion
- Negative and edge cases
- Account service/domain logic
- JavaFX scene transition testing

The full project test suite was verified with:

```text
.\gradlew test
```

Final result:

```text
BUILD SUCCESSFUL
```

---

## AI-Drafted, Then Curated Tests

### AI Tool

ChatGPT

### Prompt

> Help me draft TestFX tests for my JavaFX `RegisterController`. The controller should be tested for successful registration, duplicate username, password mismatch, and blank required fields. Use an in-memory H2 database with `UserDao` and match the existing controller behavior and UI IDs.

### What the AI Produced

ChatGPT helped draft the following `RegisterControllerTest` cases:

- `successfulRegistration()`
- `duplicateUsername()`
- `passwordMismatch()`
- `blankRequiredFields()`

The drafted tests used TestFX to interact with the registration scene and an in-memory H2 database to isolate the tests from the production database.

### Evaluation of the AI Output

The AI-generated tests were useful as a starting point, but some parts needed corrections before they were reliable.

#### ComboBox Interaction

The original draft attempted to select the role by clicking the visible ComboBox option directly.

This caused a TestFX interaction failure.

I changed the test to retrieve the ComboBox and set the value on the JavaFX thread:

```java
ComboBox<String> roleComboBox = lookup("#roleComboBox").query();
interact(() -> roleComboBox.setValue("Student"));
```

This was more reliable than opening and clicking the ComboBox popup.

#### Success Message Mismatch

One AI-drafted assertion expected:

```text
Account Created successfully! Please log in.
```

The actual controller returned:

```text
Account Created Successfully! Please log in.
```

I checked the controller behavior and changed the assertion so the expected message exactly matched the application.

#### Incorrect User Import

While adding the duplicate-user test, IntelliJ imported the wrong `User` class.

I corrected the import to:

```java
import model.User;
```

#### H2 Test Database Isolation

During the full Gradle test run, a `UserDaoTest` failed because multiple tests were using the same H2 in-memory database name.

I changed the UserDao test database to:

```text
jdbc:h2:mem:userdaotest;DB_CLOSE_DELAY=-1
```

I also added:

```java
statement.execute("DROP TABLE IF EXISTS users");
```

before creating the table.

This prevented the test classes from interfering with one another.

### What I Kept

After reviewing and correcting the AI output, I kept the tests that exercised meaningful registration behavior:

- Successful Student registration
- Duplicate username rejection
- Password mismatch rejection
- Blank required-field rejection

Additional tests were later added for the completed Teacher registration enhancement.

The final tests were kept only after they compiled and passed.

---

## Additional Tests for the Accounts Slice

### UserDaoTest

Tests CRUD operations against an in-memory H2 database:

- Insert
- Find by ID
- Find by username
- Update
- Delete

These tests verify that data can be created, retrieved, modified, and removed through `UserDao`.

### LoginControllerTest

Tests login behavior using TestFX, including:

- Successful login
- Incorrect password
- Unknown user
- Blank login fields
- Login -> Main Menu scene transition
- Login -> Registration scene transition

The successful-login test verifies that a valid user is taken from the Login scene to the Main Menu by checking that the Menu's Accounts button is visible.

The registration transition test verifies that the user can move from the Login scene to the Registration scene.

### RegisterControllerTest

Tests registration behavior using TestFX, including:

- Successful Student registration
- Successful Teacher registration
- Teacher prefix and teacher-name persistence
- Duplicate username
- Password mismatch
- Blank required fields

The Teacher registration test verifies that the selected prefix and teacher name are saved with the created `User`.

These tests use an in-memory H2 database so they do not affect the production SQLite database.

### AccountsControllerTest

Tests account-management behavior using TestFX, including:

- Loading an existing user
- Updating a user's role
- Resetting a user's password
- Deleting a user
- Attempting an action before loading a user

The tests verify both the displayed status messages and, where appropriate, the updated database values.

### AccountServiceTest

Tests service/domain logic for the Accounts slice, including:

- Successful password reset
- Successful role update
- Rejection of a blank password

The blank-password test provides a negative/edge-case test for the service layer.

### AssignmentsControllerTest - Oswald Perales

AI was used to help draft a TestFX test for the Assignments screen. The first version did not reliably trigger the button action, so I changed the test to fire the button directly and wait for JavaFX to finish before checking the validation message. The updated test passed.

---

## Manual Integration Verification

In addition to the automated tests, the completed navigation flow was manually verified:

```text
Login
  -> Main Menu
  -> Accounts
  -> Back to Main Menu
  -> Logout
  -> Login
```

The Accounts screen was also manually verified to receive the currently logged-in user so account information and role restrictions can be applied correctly.

Teacher account creation was manually verified using the SQLite production database, including the `prefix` and `teacher_name` fields.

The database migration was also verified with an existing SQLite database that did not originally contain those columns.

---

## Final Verification

After completing the tests and integration work, I ran:

```text
.\gradlew test
```

The complete project test suite passed successfully:

```text
BUILD SUCCESSFUL
```

The AI-generated tests were not accepted without review. The generated output was tested, evaluated, corrected where necessary, and incorporated only after the final suite compiled and passed.
