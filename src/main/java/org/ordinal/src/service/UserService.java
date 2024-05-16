package org.ordinal.src.service;

import lombok.RequiredArgsConstructor;
import org.ordinal.src.configuration.DatabaseConnection;
import org.ordinal.src.model.User;
import org.ordinal.src.repository.UserDAO;

import java.util.List;
@RequiredArgsConstructor
public class UserService {
    private final UserDAO userDAO;

    public UserService() {

        this.userDAO = new UserDAO(new DatabaseConnection());
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
