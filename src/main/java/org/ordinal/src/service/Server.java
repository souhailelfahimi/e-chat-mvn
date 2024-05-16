package org.ordinal.src.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


public class Server {
    public static final String USERNAME_ALREADY_TAKEN = "Username already taken";
    public static final String IDENTIFIER_PREFIX = ":;.,/=";
    public static Map<String, Socket> clientSocketMap = new ConcurrentHashMap<>();
    public static Set<String> activeUsers = new HashSet<>();
    private static int port = 8818;
    public ServerSocket serverSocket;
    private Logger logger = Logger.getLogger(Server.class.getName());

    public Server() {
        try {
            serverSocket = new ServerSocket(port);
            new ClientConnectionThread().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ClientConnectionThread extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    String username = new DataInputStream(clientSocket.getInputStream()).readUTF();
                    DataOutputStream clientOutputStream = new DataOutputStream(clientSocket.getOutputStream());

                    if (isUsernameTaken(username)) {
                        clientOutputStream.writeUTF(USERNAME_ALREADY_TAKEN);
                    } else {
                        addUser(clientSocket, username, clientOutputStream);
                        createClientThreads(username, clientSocket);
                    }
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean isUsernameTaken(String username) {
            return activeUsers != null && activeUsers.contains(username);
        }

        public void addUser(Socket clientSocket, String username, DataOutputStream clientOutputStream) throws IOException {
            clientSocketMap.put(username, clientSocket);
            activeUsers.add(username);
            clientOutputStream.writeUTF("");
        }

        public void createClientThreads(String username, Socket clientSocket) {
            new ClientMessageListenerThread(clientSocket, username).start();
            new ActiveUserListThread().start();
        }
    }

    public class ClientMessageListenerThread extends Thread {
        Socket socket;
        String userName;

        public ClientMessageListenerThread(Socket s, String userName) {
            this.socket = s;
            this.userName = userName;
        }

        private static final String ACTION_MULTICAST = "multicast";
        private static final String ACTION_EXIT = "exit";

        @Override
        public void run() {
            while (!clientSocketMap.isEmpty()) {
                try {
                    String message = new DataInputStream(socket.getInputStream()).readUTF();
                    logger.info("message read ==> " + message);
                    String[] msgList = message.split(":");

                    if (msgList[0].equalsIgnoreCase(ACTION_MULTICAST)) {
                        handleMulticastAction(msgList, userName);
                    } else if (msgList[0].equalsIgnoreCase(ACTION_EXIT)) {
                        handleExitAction(userName);
                    }
                } catch (EOFException e) {
                    logger.warning("Client " + userName + " has disconnected.");
                    break;
                } catch (SocketException e) {
                    logger.warning("Lost connection to client " + userName);
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleMulticastAction(String[] msgList, String userName) {
            String[] sendToList = msgList[1].split(",");
            for (String user : sendToList) {
                try {
                    if (activeUsers.contains(user)) {
                        new DataOutputStream(clientSocketMap.get(user).getOutputStream())
                                .writeUTF("< " + userName + " >" + msgList[2]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleExitAction(String userName) {
            activeUsers.remove(userName);
            new ActiveUserListThread().start();
            Iterator<String> usersIterator = activeUsers.iterator();
            while (usersIterator.hasNext()) {
                String nextUser = usersIterator.next();
                if (!nextUser.equalsIgnoreCase(userName)) {
                    try {
                        new DataOutputStream(clientSocketMap.get(nextUser).getOutputStream())
                                .writeUTF(userName + " disconnected...");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    new ActiveUserListThread().start();
                }
            }
        }


    }

    public class ActiveUserListThread extends Thread {


        @Override
        public void run() {
            try {
                String activeUsersIds = prepareActiveUsersListForUI();
                sendActiveUsersListToClients(activeUsersIds);
            } catch (IOException e) {
                logger.warning("Error occurred while sending the active users lists to users :\n" + e);
            }

        }

        private String prepareActiveUsersListForUI() {
            StringBuilder activeUsersListForUI = new StringBuilder();
            for (String activeUser : activeUsers) {
                activeUsersListForUI.append(activeUser).append(",");
            }
            int length = activeUsersListForUI.length();
            if (length > 0) {
                activeUsersListForUI.deleteCharAt(length - 1);
            }
            return activeUsersListForUI.toString();
        }

        private void sendActiveUsersListToClients(String activeUsersIds) throws IOException {
            for (String activeUser : activeUsers) {
                try {
                    Socket clientSocket = clientSocketMap.get(activeUser);
                    if (clientSocket != null && !clientSocket.isClosed()) {
                        DataOutputStream outStream = new DataOutputStream(clientSocket.getOutputStream());
                        outStream.writeUTF(IDENTIFIER_PREFIX + activeUsersIds);
                    } else {
                        logger.warning("Connection to user " + activeUser + " is closed. Skipping sending activeUserIds");
                    }
                } catch (IOException e) {
                    logger.warning("an exception occurred  when sending active user list" + e.getMessage());
                }
            }
        }
    }
}
