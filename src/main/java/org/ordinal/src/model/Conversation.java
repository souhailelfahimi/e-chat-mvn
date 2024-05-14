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
public class Conversation {
    private int conversationId;
    private Server server;
    private LocalDateTime conversationCreatedAt;

}
