package org.ordinal.src.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ordinal.src.model.Message;
import org.ordinal.src.model.User;
import org.ordinal.src.repository.MessageDao;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MessageServiceTest {
    private MessageDao messageDao;
    private MessageService messageService;

    @BeforeEach
    public void setUp() {
        messageDao = mock(MessageDao.class);
        messageService = new MessageService(messageDao);
    }

    @Test
    public void testSaveMessages() {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(new User(), new User(), "Test message"));

        messageService.saveMessages(messages);

        verify(messageDao, times(1)).saveMessages(messages);
    }

    @Test
    public void testGetMessagesBySenderAndReceiver() {
        User sender = new User();
        User recipient = new User();
        List<Message> expectedMessages = new ArrayList<>();
        expectedMessages.add(new Message(sender, recipient, "Test message"));

        when(messageDao.findMessagesBySenderAndReceiver(sender, recipient)).thenReturn(expectedMessages);

        List<Message> actualMessages = messageService.getMessagesBySenderAndReceiver(sender, recipient);

        assertEquals(expectedMessages, actualMessages);
        verify(messageDao, times(1)).findMessagesBySenderAndReceiver(sender, recipient);
    }
}
