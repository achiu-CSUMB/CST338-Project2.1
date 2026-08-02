package dao;

import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
/**
   * Author: John Ly
   * Date: 8/1/2026
   * Description: Tests UserDao CRUD using H2 database
    */class UserDaoTest {

        private Connection connection;
        private UserDao userDao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
        );

        String sql = """
                CREATE TABLE users (
                    user_id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    role VARCHAR(255) NOT NULL
                );
                """;

        try (java.sql.Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }

        userDao = new UserDao(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE users");
        }

        connection.close();
    }

    @Test
    void insert() {
        User user = new User("john338", "password123", "student");

        boolean inserted = userDao.insert(user);
        assertTrue(inserted);
        assertTrue(user.getUserId() > 0);
    }

    @Test
    void findById() {
    }

    @Test
    void findByUsername() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}