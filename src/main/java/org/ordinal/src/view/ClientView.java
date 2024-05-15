package org.ordinal.src.view;

import org.ordinal.src.db.DatabaseService;
import org.ordinal.src.db.MessageDao;
import org.ordinal.src.db.UserDAO;
import org.ordinal.src.model.ConnexionFormDetails;
import org.ordinal.src.model.Message;
import org.ordinal.src.model.User;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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


public class ClientView extends JFrame {

    private JFrame frame;
    private JTextField typingBoard;
    private JList activeUsersList;
    private JTextArea displayBoard;
    private DataInputStream inputStream;
    private DataOutputStream outStream;
    private DefaultListModel<String> dm;
    private String senderName, selectedUsers = "";
    private ConnexionFormDetails connexionFormDetails;
    private UserDAO userDAO;
    private MessageDao messageDao;
    private List<User> allUsers;
    private Socket socketConnection;

    public ClientView(ConnexionFormDetails connexionFormDetails, Socket socketConnection) {
        this.socketConnection = socketConnection;
        DatabaseService databaseService = new DatabaseService();
        userDAO = new UserDAO(databaseService);
        messageDao = new MessageDao(databaseService);
        this.connexionFormDetails = connexionFormDetails;
        initialize(); // initilize UI components
        this.senderName = connexionFormDetails.getName();
        SaveUser();
        try {
            frame.setTitle("e-chat : " + senderName + (connexionFormDetails.isClient() ? "" : " " + connexionFormDetails.getIp() + ":" + connexionFormDetails.getPort())); // set title of UI
            dm = new DefaultListModel<String>(); // default list used for showing active users on UI
            activeUsersList.setModel(dm);// show that list on UI component JList named clientActiveUsersList
            inputStream = new DataInputStream(socketConnection.getInputStream()); // initilize input and output stream
            outStream = new DataOutputStream(socketConnection.getOutputStream());
            new Read().start(); // create a new thread for reading the messages
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void SaveUser() {
        allUsers = userDAO.getAllUsers();
        boolean userExistsInDatabase = allUsers.stream().anyMatch(user -> user.getUserName().equals(senderName));

        if (!userExistsInDatabase) {
            User newUser = new User();
            newUser.setUserName(senderName);
            userDAO.saveUser(newUser);
        }
    }

    class Read extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    String m = inputStream.readUTF();  // read message from server, this will contain :;.,/=<comma seperated clientsIds>
                    System.out.println("inside read thread : " + m); // print message for testing purpose
                    if (m.contains(":;.,/=")) { // prefix(i know its random)
                        m = m.substring(6); // comma separated all active user ids
                        dm.clear(); // clear the list before inserting fresh elements
                        StringTokenizer st = new StringTokenizer(m, ","); // split all the clientIds and add to dm below
                        while (st.hasMoreTokens()) {
                            String u = st.nextToken();
                            if (!senderName.equals(u)) // we do not need to show own user id in the active user list pane
                                dm.addElement(u); // add all the active user ids to the defaultList to display on active
                            // user pane on client view
                        }
                    } else {
                        displayBoard.append("" + m + "\n"); //otherwise print on the clients message board
                    }
                } catch (SocketException e) {
                    displayBoard.append("(Info) : Communication terminée");
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() { // initialize all the components of UI
        frame = new JFrame();
        frame.setBounds(100, 100, 926, 705);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setTitle("Client View");
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    outStream.writeUTF("exit"); // closes the thread and show the message on server and client's message
                    // board
                    displayBoard.append("You are disconnected now.\n");
                    frame.dispose(); // close the frame
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
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

        JButton sendMessageButton = new JButton("Send");
        // action to be taken on send message button
        sendMessageButton.addActionListener(e -> {
            String textAreaMessage = typingBoard.getText(); // get the message from textbox
            if (textAreaMessage != null && !textAreaMessage.isEmpty()) {  // only if message is not empty then send it further otherwise do nothing
                try {
                    String messageToBeSentToServer = "";
                    String cast = "multicast"; // this will be an identifier to identify type of message
                    int flag = 0; // flag used to check whether used has selected any client or not for multicast

                    List<String> clientList = activeUsersList.getSelectedValuesList(); // get all the users selected on UI
                    if (clientList.size() == 0) // if no user is selected then set the flag for further use
                        flag = 1;
                    for (String selectedUsr : clientList) { // append all the usernames selected in a variable
                        if (selectedUsers.isEmpty())
                            selectedUsers += selectedUsr;
                        else
                            selectedUsers += "," + selectedUsr;
                    }
                    messageToBeSentToServer = cast + ":" + selectedUsers + ":" + textAreaMessage; // prepare message to be sent to server


                    if (flag == 1) { // for multicast check if no user was selected then prompt a message dialog
                        JOptionPane.showMessageDialog(frame, "No user selected");
                    } else { // otherwise just send the message to the user
                        outStream.writeUTF(messageToBeSentToServer);
                        typingBoard.setText("");
                        displayBoard.append(textAreaMessage + "\n"); //show the sent message to the sender's message board
                        saveMessages(connexionFormDetails, textAreaMessage);
                    }
                    selectedUsers = ""; // clear the all the client ids
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "model.ConnexionFormDetails does not exist anymore."); // if user doesn't exist then show message
                }
            }
        });
        sendMessageButton.setBounds(554, 533, 137, 84);
        frame.getContentPane().add(sendMessageButton);

        activeUsersList = new JList();
        activeUsersList.setToolTipText("Active Users");
        activeUsersList.setBounds(554, 63, 327, 457);
        activeUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        activeUsersList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedUser = (String) activeUsersList.getSelectedValue();
                    loadChatHistory(selectedUser);
                }
            }
        });


        frame.getContentPane().add(activeUsersList);

        JLabel lblNewLabel = new JLabel("Active Users");
        lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
        lblNewLabel.setBounds(559, 43, 95, 16);
        frame.getContentPane().add(lblNewLabel);

        frame.setVisible(true);
    }

    private void saveMessages(ConnexionFormDetails connexionFormDetails, String textAreaMessage) {
        User sender = userDAO.findByName(connexionFormDetails.getName());
        List<String> receiversName = Arrays.stream(selectedUsers.split(",")).toList();
        List<User> receivers = userDAO.findByNames(receiversName);
        List<Message> messages = new ArrayList<>();
        for (User receiver : receivers) {
            Message message = Message.builder()
                    .messageBody(textAreaMessage)
                    .sender(sender)
                    .receiver(receiver)
                    .build();
            messages.add(message);
        }
        messageDao.saveMessages(messages);
    }

    private void loadChatHistory(String receiverName) {
        if (senderName == null || senderName.isEmpty() || receiverName == null || receiverName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please select a receiver");
        }
        User sender = userDAO.findByName(senderName);
        User receiver = userDAO.findByName(receiverName);
        List<Message> messages = messageDao.findMessagesByNames(sender, receiver);
        displayBoard.setText("");
        for (Message msg : messages) {
            String chatLog = "";
            if (sender.getUserId() == msg.getSender().getUserId()) {
                chatLog = String.format("%s\n", msg.getMessageBody());
            } else {
                chatLog = String.format("< %s >%s\n", msg.getSender().getUserName(), msg.getMessageBody());
            }
            displayBoard.append(chatLog);
        }
    }
}
