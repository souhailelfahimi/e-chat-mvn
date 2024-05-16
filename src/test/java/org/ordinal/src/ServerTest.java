package org.ordinal.src;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ordinal.src.service.Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServerTest {

    private static final int PORT = 8818;

    private Server server;
    private ServerSocket serverSocketMock;

    @BeforeEach
    public void setUp() throws IOException {
        serverSocketMock = mock(ServerSocket.class);
        when(serverSocketMock.accept()).thenReturn(mock(Socket.class));
        server = new Server();
        server.serverSocket = serverSocketMock;
        server.clientSocketMap = new ConcurrentHashMap<>();
        server.activeUsers = Collections.synchronizedSet(new HashSet<>());

    }

    @Test
    public void testClientConnectionThread() throws IOException {
        Server.ClientConnectionThread clientConnectionThread = server.new ClientConnectionThread();
        clientConnectionThread.start();
        assertNotNull(server.clientSocketMap);
    }

    @Test
    public void testIsUsernameTaken() {
        server.activeUsers.add("user1");
        assertTrue(server.new ClientConnectionThread().isUsernameTaken("user1"));
        assertFalse(server.new ClientConnectionThread().isUsernameTaken("user2"));
    }

    @Test
    public void testAddUser() throws IOException {
        DataOutputStream outputStreamMock = mock(DataOutputStream.class);
        Socket clientSocketMock = mock(Socket.class);
        when(clientSocketMock.getOutputStream()).thenReturn(outputStreamMock);

        Server.ClientConnectionThread clientConnectionThread = server.new ClientConnectionThread();
        clientConnectionThread.addUser(clientSocketMock, "user1", outputStreamMock);

        assertTrue(server.activeUsers.contains("user1"));
        assertTrue(server.clientSocketMap.containsKey("user1"));
        verify(outputStreamMock).writeUTF("");
    }


}
