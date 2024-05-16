package org.ordinal.src.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ordinal.src.model.User;
import org.ordinal.src.repository.UserDAO;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserDAO userDAO;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userDAO = mock(UserDAO.class);
        userService = new UserService(userDAO);
    }

    @Test
    public void testGetUserByName() {
        String name = "testUser";
        User expectedUser = new User();
        expectedUser.setUserName(name);

        when(userDAO.findByName(name)).thenReturn(expectedUser);

        User actualUser = userService.getUserByName(name);

        assertEquals(expectedUser, actualUser);
        verify(userDAO, times(1)).findByName(name);
    }

    @Test
    public void testGetUserByNames() {
        List<String> names = new ArrayList<>();
        names.add("user1");
        names.add("user2");

        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User());
        expectedUsers.add(new User());

        when(userDAO.findByNames(names)).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getUserByNames(names);

        assertEquals(expectedUsers, actualUsers);
        verify(userDAO, times(1)).findByNames(names);
    }

    @Test
    public void testAddUser() {
        User user = new User();

        userService.addUser(user);

        verify(userDAO, times(1)).saveUser(user);
    }

    @Test
    public void testGetAllUsers() {
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User());
        expectedUsers.add(new User());

        when(userDAO.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers();

        assertEquals(expectedUsers, actualUsers);
        verify(userDAO, times(1)).findAll();
    }
}
