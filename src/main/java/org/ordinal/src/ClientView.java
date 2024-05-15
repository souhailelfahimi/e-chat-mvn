package org.ordinal.src;

import org.ordinal.src.db.DatabaseService;
import org.ordinal.src.db.MessageDao;
import org.ordinal.src.db.UserDAO;
import org.ordinal.src.model.FormDetails;
import org.ordinal.src.model.Message;
import org.ordinal.src.model.User;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;


public class ClientView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JFrame frame;
    private JTextField clientTypingBoard;
    private JList clientActiveUsersList;
    private JTextArea clientMessageBoard;
    private JButton clientKillProcessBtn;
    private DataInputStream inputStream;
    private DataOutputStream outStream;
    private DefaultListModel<String> dm;
    private String senderName, selectedUsers = "";
    private FormDetails formDetails;
    private UserDAO userDAO;
    private MessageDao messageDao;
    private List<User> allUsers;

    public ClientView(FormDetails formDetails, Socket s) {
        DatabaseService databaseService = new DatabaseService();
        userDAO = new UserDAO(databaseService);
        messageDao = new MessageDao(databaseService);
        this.formDetails = formDetails;
        initialize(); // initilize UI components
        this.senderName = formDetails.getName();
        SaveUser();
        try {
            frame.setTitle("e-chat : " + senderName + (formDetails.isClient() ? "" : " " + formDetails.getIp() + ":" + formDetails.getPort())); // set title of UI
            dm = new DefaultListModel<String>(); // default list used for showing active users on UI
            clientActiveUsersList.setModel(dm);// show that list on UI component JList named clientActiveUsersList
            inputStream = new DataInputStream(s.getInputStream()); // initilize input and output stream
            outStream = new DataOutputStream(s.getOutputStream());
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
                        clientMessageBoard.append("" + m + "\n"); //otherwise print on the clients message board
                    }
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

        clientMessageBoard = new JTextArea();
        clientMessageBoard.setEditable(false);
        clientMessageBoard.setBounds(12, 25, 530, 495);
        JScrollPane scrollPane = new JScrollPane(
                clientMessageBoard,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(clientMessageBoard);
        frame.getContentPane().add(scrollPane);
        frame.getContentPane().add(clientMessageBoard);

        clientTypingBoard = new JTextField();
        clientTypingBoard.setHorizontalAlignment(SwingConstants.LEFT);
        clientTypingBoard.setBounds(12, 533, 530, 84);
        frame.getContentPane().add(clientTypingBoard);
        clientTypingBoard.setColumns(10);

        JButton clientSendMsgBtn = new JButton("Send");
        // action to be taken on send message button
        clientSendMsgBtn.addActionListener(e -> {
            String textAreaMessage = clientTypingBoard.getText(); // get the message from textbox
            if (textAreaMessage != null && !textAreaMessage.isEmpty()) {  // only if message is not empty then send it further otherwise do nothing
                try {
                    String messageToBeSentToServer = "";
                    String cast = "multicast"; // this will be an identifier to identify type of message
                    int flag = 0; // flag used to check whether used has selected any client or not for multicast

                    List<String> clientList = clientActiveUsersList.getSelectedValuesList(); // get all the users selected on UI
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
                        clientTypingBoard.setText("");
                        clientMessageBoard.append(textAreaMessage + "\n"); //show the sent message to the sender's message board
                        saveMessagetoDatabase(formDetails, textAreaMessage);
                    }
                    selectedUsers = ""; // clear the all the client ids
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "model.FormDetails does not exist anymore."); // if user doesn't exist then show message
                }
            }
        });
        clientSendMsgBtn.setBounds(554, 533, 137, 84);
        frame.getContentPane().add(clientSendMsgBtn);

        clientActiveUsersList = new JList();
        clientActiveUsersList.setToolTipText("Active Users");
        clientActiveUsersList.setBounds(554, 63, 327, 457);
        clientActiveUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        clientActiveUsersList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedUser = (String) clientActiveUsersList.getSelectedValue();
                    loadChatHistoryFromDatabase(selectedUser);
                }
            }
        });


        frame.getContentPane().add(clientActiveUsersList);

        clientKillProcessBtn = new JButton("Kill Process");
        // kill process event
        clientKillProcessBtn.addActionListener(e -> {
            try {
                outStream.writeUTF("exit"); // closes the thread and show the message on server and client's message
                // board
                clientMessageBoard.append("You are disconnected now.\n");
                frame.dispose(); // close the frame
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        clientKillProcessBtn.setBounds(703, 533, 193, 84);
        frame.getContentPane().add(clientKillProcessBtn);

        JLabel lblNewLabel = new JLabel("Active Users");
        lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
        lblNewLabel.setBounds(559, 43, 95, 16);
        frame.getContentPane().add(lblNewLabel);

        frame.setVisible(true);
    }

    private void saveMessagetoDatabase(FormDetails formDetails, String textAreaMessage) {
        User sender = userDAO.findByName(formDetails.getName());
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

    private void loadChatHistoryFromDatabase(String receiverName) {
        if (senderName.isEmpty() || receiverName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please select a receiver");
        }
        User sender = userDAO.findByName(senderName);
        User receiver = userDAO.findByName(receiverName);
        List<Message> messages = messageDao.findMessagesByNames(sender, receiver);
        // Clear the chat history in the UI
        clientMessageBoard.setText("");

        // Populate the chat history in the UI
        for (Message msg : messages) {
            // This is just an example of how you can format the messages,
            // you might want to modify this to match how you wants to display the messages
            String chatLog = "";
            if (sender.getUserId() == msg.getSender().getUserId()) {
                chatLog = String.format("%s\n", msg.getMessageBody());
            } else {
                chatLog = String.format("< %s >%s\n", msg.getSender().getUserName(), msg.getMessageBody());
            }
            clientMessageBoard.append(chatLog);
        }
    }
}
