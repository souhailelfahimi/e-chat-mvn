package org.ordinal.src.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Server {
    private int serverId;
    private String serverIp;
    private int serverPort;

}
