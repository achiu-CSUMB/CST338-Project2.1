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
- Account management actions using TestFX
- Password reset
- Role updates
- User deletion
- Negative and edge cases
- Account service/domain logic
- JavaFX scene transition testing

The full project test suite was verified with:

```text
.\gradlew clean test
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

> Help me draft TestFX tests for my JavaFX `RegisterController`. The controller should be tested for successful registration, duplicate username, password mismatch, and blank required fields. Use an in-memory H2 database with `UserDao`and match the existing controller behavior and UI IDs.

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

- Successful registration
- Duplicate username rejection
- Password mismatch rejection
- Blank required-field rejection

I reran the tests after the corrections and kept the final versions only after they compiled and passed.

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
- Login to Register scene transition

The scene transition test verifies that the user can move from the Login scene to the Register scene.

### RegisterControllerTest

Tests registration behavior using TestFX, including:

- Successful registration
- Duplicate username
- Password mismatch
- Blank required fields

These tests use an in-memory H2 database so they do not affect the production database.

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

---

## Final Verification

After completing the tests and correcting the test database isolation issue, I ran:

```text
.\gradlew clean test
```

The complete project test suite passed successfully.

```text
BUILD SUCCESSFUL
```

The AI-generated tests were not accepted without review. The generated output was tested, evaluated, corrected where necessary, and incorporated only after the final suite compiled and passed.
