package service;

import dao.UserDao;
import model.User;

/**
 * Author: John Ly
 * Date: 8/12/2026
 * Description:
 */
public class AccountService {

    private UserDao userDao;

    public AccountService(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean resetPassword(String username, String newPassword) {
        if (username == null || username.isBlank() || newPassword == null || newPassword.isBlank()) {
            return false;
        }

        User user = userDao.findByUsername(username);

        if (user == null) {
            return false;
        }

        user.setPassword(newPassword);
        return userDao.update(user);
    }

    public boolean updateRole(String username, String newRole) {
        if (username == null || username.isBlank() || newRole == null || newRole.isBlank()) {
            return false;
        }

        User user = userDao.findByUsername(username);

        if (user == null) {
            return false;
        }

        user.setRole(newRole);
        return userDao.update(user);
    }
}
