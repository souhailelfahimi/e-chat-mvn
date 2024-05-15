package org.ordinal.src.service;

import org.ordinal.src.db.DatabaseService;
import org.ordinal.src.db.UserDAO;
import org.ordinal.src.model.User;

import java.util.List;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO(new DatabaseService());
    }

    public User getUserByName(String name) {
        return userDAO.findByName(name);
    }

    public List<User> getUserByNames(List<String> receiversName) {
        return userDAO.findByNames(receiversName);
    }

    public void addUser(User user) {
        userDAO.saveUser(user);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }


}
