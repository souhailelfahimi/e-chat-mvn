package org.ordinal.src.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ordinal.src.configuration.DatabaseConnection;
import org.ordinal.src.model.Message;
import org.ordinal.src.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MessageDaoTest {

    private DatabaseConnection databaseConnection;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MessageDao messageDao;

    @BeforeEach
    public void setUp() {
        databaseConnection = mock(DatabaseConnection.class);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        messageDao = new MessageDao(databaseConnection);
    }

    @Test
    public void testSaveMessage() throws SQLException {
        String senderId = "1";
        String receiverId = "2";
        String message = "Test message";

        when(databaseConnection.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        doNothing().when(preparedStatement).setString(anyInt(), anyString());
        when(preparedStatement.executeUpdate()).thenReturn(1);

        messageDao.saveMessage(senderId, receiverId, message);

        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).setString(1, senderId);
        verify(preparedStatement, times(1)).setString(2, receiverId);
        verify(preparedStatement, times(1)).setString(3, message);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testSaveMessages() throws SQLException {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(User.builder().userId(1).build(), User.builder().userId(2).build(), "Test message"));

        when(databaseConnection.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        doNothing().when(preparedStatement).setInt(anyInt(), anyInt());
        doNothing().when(preparedStatement).setString(anyInt(), anyString());
        doNothing().when(preparedStatement).addBatch();
        when(preparedStatement.executeBatch()).thenReturn(new int[]{1});

        messageDao.saveMessages(messages);

        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).setInt(1, 1);
        verify(preparedStatement, times(1)).setInt(2, 2);
        verify(preparedStatement, times(1)).setString(3, "Test message");
        verify(preparedStatement, times(1)).addBatch();
        verify(preparedStatement, times(1)).executeBatch();
    }

    @Test
    public void testFindMessagesBySenderAndReceiver() throws SQLException {
        User sender = User.builder().userId(1).build();
        User recipient = User.builder().userId(2).build();
        List<Message> expectedMessages = new ArrayList<>();
        expectedMessages.add(new Message(sender, recipient, "Test message"));

        when(databaseConnection.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("message_body")).thenReturn("Test message");
        when(resultSet.getInt("sender_id")).thenReturn(sender.getUserId());
        when(resultSet.getInt("receiver_id")).thenReturn(recipient.getUserId());

        List<Message> actualMessages = messageDao.findMessagesBySenderAndReceiver(sender, recipient);

        assertEquals(expectedMessages, actualMessages);
        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).setInt(1, sender.getUserId());
        verify(preparedStatement, times(1)).setInt(2, recipient.getUserId());
        verify(preparedStatement, times(1)).setInt(3, recipient.getUserId());
        verify(preparedStatement, times(1)).setInt(4, sender.getUserId());
        verify(resultSet, times(2)).next();
        verify(resultSet, times(1)).getString("message_body");
        verify(resultSet, times(1)).getInt("sender_id");
        verify(resultSet, times(1)).getInt("receiver_id");
    }
}
