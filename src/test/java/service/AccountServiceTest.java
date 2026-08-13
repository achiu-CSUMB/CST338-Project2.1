package service;

import dao.UserDao;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: John Ly
 * Date: 8/12/2026
 * Description:
 */
class AccountServiceTest {

    private Connection connection;
    private UserDao userDao;
    private AccountService accountService;

    @BeforeEach
    void setUp() throws Exception {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:accountservicetest;DB_CLOSE_DELAY=-1"
        );

        try (java.sql.Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS users");

            statement.execute("""
                    CREATE TABLE users (
                        user_id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(255) NOT NULL
                    );
                    """);
        }
        userDao = new UserDao(connection);
        accountService = new AccountService(userDao);
    }

    @Test
    void resetPassword() {
        userDao.insert(new User("john338", "password123", "student"));

        boolean result = accountService.resetPassword("john338", "newpassword123");
        assertTrue(result);

        User user = userDao.findByUsername("john338");
        assertEquals("newpassword123", user.getPassword());
    }

    @Test
    void updateRole() {
        userDao.insert(new User("john338", "password123", "student"));

        boolean result = accountService.updateRole("john338", "admin");
        assertTrue(result);

        User user = userDao.findByUsername("john338");
        assertEquals("admin", user.getRole());
    }

    @Test
    void resetPasswordWithBlankPassword() {
        userDao.insert(new User("john338", "password123", "student"));

        boolean result = accountService.resetPassword("john338", "");
        assertFalse(result);

        User user = userDao.findByUsername("john338");
        assertEquals("password123", user.getPassword());
    }
}