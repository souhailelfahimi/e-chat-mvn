package org.ordinal.src.model;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    private int messageId;
    private int msgConversationId;
    private int msgSenderId;
    private String messageBody;
    private boolean messageReadStatus;
    private LocalDateTime messageCreatedAt;

}
