package org.ordinal.src.service;

import org.ordinal.src.db.DatabaseService;
import org.ordinal.src.db.MessageDao;
import org.ordinal.src.model.Message;
import org.ordinal.src.model.User;

import java.util.List;

public class MessageService {
    private final MessageDao messageDao;

    public MessageService() {
        this.messageDao = new MessageDao(new DatabaseService());
    }

    public void saveMessages(List<Message> messages) {
        messageDao.saveMessages(messages);
    }

    public List<Message> getMessagesBySenderAndReceiver(User sender, User recipient) {
        return messageDao.findMessagesBySenderAndReceiver(sender, recipient);
    }
}
