package org.ordinal.src.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ordinal.src.configuration.DatabaseConnection;
import org.ordinal.src.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserDAOTest {

    private DatabaseConnection databaseConnection;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        databaseConnection = mock(DatabaseConnection.class);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        userDAO = new UserDAO(databaseConnection);
    }

    @Test
    public void testFindAll() throws SQLException {
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User(1, "user1"));
        expectedUsers.add(new User(2, "user2"));

        when(databaseConnection.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("user_id")).thenReturn(1).thenReturn(2);
        when(resultSet.getString("user_name")).thenReturn("user1").thenReturn("user2");

        List<User> actualUsers = userDAO.findAll();

        assertEquals(expectedUsers, actualUsers);
        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).executeQuery();
        verify(resultSet, times(3)).next();
        verify(resultSet, times(2)).getInt("user_id");
        verify(resultSet, times(2)).getString("user_name");
    }

    @Test
    public void testSaveUser() throws SQLException {
        User newUser = new User("newUser");

        when(databaseConnection.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        doNothing().when(preparedStatement).setString(anyInt(), anyString());
        when(preparedStatement.executeUpdate()).thenReturn(1);

        userDAO.saveUser(newUser);

        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).setString(1, newUser.getUserName());
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testFindByNames() throws SQLException {
        List<String> names = List.of("name1", "name2");
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User(1, "name1"));
        expectedUsers.add(new User(2, "name2"));

        when(databaseConnection.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("user_id")).thenReturn(1).thenReturn(2);
        when(resultSet.getString("user_name")).thenReturn("name1").thenReturn("name2");

        List<User> actualUsers = userDAO.findByNames(names);

        assertEquals(expectedUsers, actualUsers);
        verify(connection, times(2)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).setString(1, "name1");
        verify(preparedStatement, times(1)).setString(1, "name2");
        verify(preparedStatement, times(2)).executeQuery();
        verify(resultSet, times(4)).next();
        verify(resultSet, times(2)).getInt("user_id");
        verify(resultSet, times(2)).getString("user_name");
    }

    @Test
    public void testFindByName() throws SQLException {
        User expectedUser = new User(1, "name");

        when(databaseConnection.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("user_id")).thenReturn(1);
        when(resultSet.getString("user_name")).thenReturn("name");

        User actualUser = userDAO.findByName("name");

        assertEquals(expectedUser, actualUser);
        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).setString(1, "name");
        verify(preparedStatement, times(1)).executeQuery();
        verify(resultSet, times(1)).next();
        verify(resultSet, times(1)).getInt("user_id");
        verify(resultSet, times(1)).getString("user_name");
    }
}
