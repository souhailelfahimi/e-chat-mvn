package org.ordinal.src.view;

import org.ordinal.src.model.ConnexionFormDetails;
import org.ordinal.src.model.Message;
import org.ordinal.src.model.User;
import org.ordinal.src.service.MessageService;
import org.ordinal.src.service.UserService;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import static org.ordinal.src.Server.IDENTIFIER_PREFIX;


public class ClientView extends JFrame {
    Logger logger = Logger.getLogger(ClientView.class.getName());
    private JFrame frame;
    private JTextField typingBoard;
    private JList activeUsersList;
    private JTextArea displayBoard;
    private JButton sendMessageButton;
    private DataInputStream inputStream;
    private DataOutputStream outStream;
    private DefaultListModel<String> activeUsersModel;
    private String senderName, selectedUsers = "";
    private ConnexionFormDetails connexionFormDetails;
    private UserService userService;
    private MessageService messageService;
    private List<User> allUsers;
    private Socket socketConnection;

    public ClientView(ConnexionFormDetails connexionFormDetails, Socket socketConnection) {
        this.userService = new UserService();
        this.messageService = new MessageService();
        this.socketConnection = socketConnection;
        this.connexionFormDetails = connexionFormDetails;
        buildUI();
        this.senderName = connexionFormDetails.getName();
        SaveUser();
        try {
            frame.setTitle("e-chat : " + senderName + (connexionFormDetails.isClient() ? "" : " " + connexionFormDetails.getIp() + ":" + connexionFormDetails.getPort())); // set title of UI
            activeUsersModel = new DefaultListModel<String>(); // default list used for showing active users on UI
            activeUsersList.setModel(activeUsersModel);// show that list on UI component JList named clientActiveUsersList
            inputStream = new DataInputStream(socketConnection.getInputStream()); // initilize input and output stream
            outStream = new DataOutputStream(socketConnection.getOutputStream());
            new ServerMessageListener().start(); // create a new thread for reading the messages
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void SaveUser() {
        allUsers = userService.getAllUsers();
        boolean userExistsInDatabase = allUsers.stream().anyMatch(user -> user.getUserName().equals(senderName));
        if (!userExistsInDatabase) {
            User newUser = new User();
            newUser.setUserName(senderName);
            userService.addUser(newUser);
        }
    }

    class ServerMessageListener extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    String message = readMessage();
                    processMessage(message);
                } catch (SocketException e) {
                    displayBoard.append("(Info) : Communication terminated");
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        private String readMessage() throws IOException {
            return inputStream.readUTF();
        }

        private void processMessage(String message) {
            if (isUserListMessage(message)) {
                updateUserListView(message.substring(6));
            } else {
                displayBoard.append(message + "\n");
            }
        }

        private boolean isUserListMessage(String message) {
            return message.contains(IDENTIFIER_PREFIX);
        }

        private void updateUserListView(String userList) {
            activeUsersModel.clear();
            StringTokenizer tokenizer = new StringTokenizer(userList, ",");
            while (tokenizer.hasMoreTokens()) {
                addUserToView(tokenizer.nextToken());
            }
        }

        private void addUserToView(String userName) {
            if (!senderName.equals(userName))
                activeUsersModel.addElement(userName);
        }
    }


    private void buildUI() {
        frame = new JFrame();
        frame.setBounds(100, 100, 926, 705);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setTitle("Client View");

        displayBoard = new JTextArea();
        displayBoard.setEditable(false);
        displayBoard.setBounds(12, 25, 530, 495);
        JScrollPane scrollPane = new JScrollPane(
                displayBoard,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(displayBoard);
        frame.getContentPane().add(scrollPane);
        frame.getContentPane().add(displayBoard);

        typingBoard = new JTextField();
        typingBoard.setHorizontalAlignment(SwingConstants.LEFT);
        typingBoard.setBounds(12, 533, 530, 84);
        frame.getContentPane().add(typingBoard);
        typingBoard.setColumns(10);

        sendMessageButton = new JButton("Send");
        sendMessageButton.setBounds(554, 533, 137, 84);
        frame.getContentPane().add(sendMessageButton);

        activeUsersList = new JList();
        activeUsersList.setToolTipText("Active Users");
        activeUsersList.setBounds(554, 63, 327, 457);
        activeUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        frame.getContentPane().add(activeUsersList);
        JLabel lblNewLabel = new JLabel("Active Users");
        lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
        lblNewLabel.setBounds(559, 43, 95, 16);
        frame.getContentPane().add(lblNewLabel);
        frame.setVisible(true);
        initialiseActions();
    }

    private void saveMessages(ConnexionFormDetails connexionFormDetails, String textAreaMessage) {
        User sender = userService.getUserByName(connexionFormDetails.getName());
        List<String> receiversName = Arrays.stream(selectedUsers.split(",")).toList();
        List<User> receivers = userService.getUserByNames(receiversName);
        List<Message> messages = new ArrayList<>();
        for (User receiver : receivers) {
            Message message = Message.builder()
                    .messageBody(textAreaMessage)
                    .sender(sender)
                    .receiver(receiver)
                    .build();
            messages.add(message);
        }
        messageService.saveMessages(messages);
    }

    private void loadChatHistory(String receiverName) {
        if (isValidUserName(senderName) || isValidUserName(receiverName)) {
            displayBoard.setText("");
            activeUsersList.setModel(activeUsersModel);
        } else {
            fetchAndFormatMessages(receiverName);
        }
    }

    private boolean isValidUserName(String name) {
        return name == null || name.isEmpty();
    }

    private void fetchAndFormatMessages(String receiverName) {
        User sender = userService.getUserByName(senderName);
        User receiver = userService.getUserByName(receiverName);
        List<Message> messages = messageService.getMessagesBySenderAndReceiver(sender, receiver);
        displayBoard.setText("");
        for (Message msg : messages) {
            int senderId = msg.getSender().getUserId();
            String senderName = msg.getSender().getUserName();
            String messageBody = msg.getMessageBody();

            String chatLog = (sender.getUserId() == senderId) ?
                    String.format("%s\n", messageBody) :
                    String.format("< %s >%s\n", senderName, messageBody);

            displayBoard.append(chatLog);
        }
    }

    public void initialiseActions() {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    outStream.writeUTF("exit");
                    displayBoard.append("You are disconnected now.\n");
                    frame.dispose();
                } catch (IOException ioException) {
                    logger.info("Unable to disconnect properly: " + ioException.getMessage());
                }
            }
        });

        sendMessageButton.addActionListener(e -> {
            String message = typingBoard.getText();
            if (message != null && !message.isEmpty()) {
                try {
                    List<String> selectedUsersList = activeUsersList.getSelectedValuesList();
                    boolean noUserSelected = selectedUsersList.isEmpty();
                    StringBuilder selectedUsersBuilder = new StringBuilder();
                    for (String selectedUser : selectedUsersList) {
                        if (!selectedUsersBuilder.isEmpty()) {
                            selectedUsersBuilder.append(",");
                        }
                        selectedUsersBuilder.append(selectedUser);
                    }
                    String messageToBeSentToServer = prepareMessageToServer("multicast", selectedUsersBuilder.toString(), message);
                    if (noUserSelected) {
                        JOptionPane.showMessageDialog(frame, "No user selected");
                    } else {
                        outStream.writeUTF(messageToBeSentToServer);
                        typingBoard.setText("");
                        displayBoard.append(message + "\n");
                        saveMessages(connexionFormDetails, message);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "model.ConnexionFormDetails does not exist anymore.");
                }
            }
        });

        activeUsersList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedUser = (String) activeUsersList.getSelectedValue();
                loadChatHistory(selectedUser);
            }
        });
    }

    private String prepareMessageToServer(String cast, String selectedUsers, String message) {
        return cast + ":" + selectedUsers + ":" + message;
    }
}
