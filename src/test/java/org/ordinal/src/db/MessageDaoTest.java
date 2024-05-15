package org.ordinal.src.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.ordinal.src.model.Message;
import org.ordinal.src.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

public class MessageDaoTest {
    private MessageDao messageDao;
    private DatabaseService databaseServiceMock;
    private Connection connectionMock;

    @BeforeEach
    public void setUp() {
        databaseServiceMock = Mockito.mock(DatabaseService.class);
        connectionMock = Mockito.mock(Connection.class);
        messageDao = new MessageDao(databaseServiceMock);
    }


    /*@Test
    public void saveMessages_shouldNotThrowException_whenValidMessagesArePassed() {
        try {
            when(databaseServiceMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(Mockito.anyString())).thenReturn(Mockito.mock(PreparedStatement.class));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        List<Message> messages = new ArrayList<>();
        User user1 = new User(1, "test1");
        User user2 = new User(2, "test2");
        messages.add(new Message(user1, user2, "Hello there!"));
        messages.add(new Message(user2, user1, "General Kenobi!"));
        assertDoesNotThrow(() -> messageDao.saveMessages(messages));
    }*/
}
