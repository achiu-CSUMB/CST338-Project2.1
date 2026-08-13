package model;

/**
 * Author: John Ly
 * Date: 8/1/2026
 * Description: For user accounts
 */
public class User {

    private int userId;
    private String username;
    private String password;
    private String role;
    private String prefix;
    private String teacherName;

    public User(int userId, String username, String password, String role, String prefix, String teacherName) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.prefix = prefix;
        this.teacherName = teacherName;
    }

    public User(String username, String password, String role, String prefix, String teacherName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.prefix = prefix;
        this.teacherName = teacherName;
    }

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
