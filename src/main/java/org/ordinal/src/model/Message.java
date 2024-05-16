package org.ordinal.src.model;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Message {
    private int messageId;
    private User sender;
    private User receiver;
    private String messageBody;
    private boolean isRead;
    private Timestamp creationTimeStamp;

    public Message(User sender, User receiver, String testMessage) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageBody = testMessage;
    }
}
