package org.ordinal.src.model;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    private int messageId;
    private User sender;
    private User receiver;
    private String messageBody;
    private boolean isRead;
    private Timestamp creationTimeStamp;
}
