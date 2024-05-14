package org.ordinal.src.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private int messageId;
    private int msgConversationId;
    private int msgSenderId;
    private String messageBody;
    private boolean messageReadStatus;
    private LocalDateTime messageCreatedAt;

}
