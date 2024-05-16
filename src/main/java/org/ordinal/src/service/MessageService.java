package org.ordinal.src.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.ordinal.src.configuration.DatabaseConnection;
import org.ordinal.src.model.Message;
import org.ordinal.src.model.User;
import org.ordinal.src.repository.MessageDao;

import java.util.List;
@RequiredArgsConstructor
public class MessageService {
    private final MessageDao messageDao;

    public MessageService() {
        this.messageDao = new MessageDao(new DatabaseConnection());
    }

    public void saveMessages(List<Message> messages) {

        messageDao.saveMessages(messages);
    }

    public List<Message> getMessagesBySenderAndReceiver(User sender, User recipient) {
        return messageDao.findMessagesBySenderAndReceiver(sender, recipient);
    }
}
