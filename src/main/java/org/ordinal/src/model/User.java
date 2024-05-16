package org.ordinal.src.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
public class User {
    private int userId;
    private String userName;

    public User(String name) {
        this.userName = name;
    }
}
