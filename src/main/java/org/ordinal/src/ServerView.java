package org.ordinal.src;

import javax.swing.*;
import java.awt.*;
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


public class ServerView {

    private static Map<String, Socket> allUsersList = new ConcurrentHashMap<>(); // keeps the mapping of all the
    // usernames used and their socket connections
    private static Set<String> activeUserSet = new HashSet<>(); // this set keeps track of all the active users
    private static int port = 8818;  // port number to be used
    private ServerSocket serverSocket; //server socket variable
    private DefaultListModel<String> activeDlm = new DefaultListModel<String>(); // keeps list of active users for display on UI
    private DefaultListModel<String> allDlm = new DefaultListModel<String>(); // keeps list of all users for display on UI

    public ServerView() {

        try {
            serverSocket = new ServerSocket(port);  // create a socket for server

            new ClientAccept().start(); // this will create a thread for client
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ClientAccept extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();  // create a socket for client
                    String uName = new DataInputStream(clientSocket.getInputStream()).readUTF(); // this will receive the username sent from client register view
                    DataOutputStream cOutStream = new DataOutputStream(clientSocket.getOutputStream()); // create an output stream for client
                    if (activeUserSet != null && activeUserSet.contains(uName)) { // if username is in use then we need to prompt user to enter new name
                        cOutStream.writeUTF("Username already taken");
                    } else {
                        allUsersList.put(uName, clientSocket); // add new user to allUserList and activeUserSet
                        activeUserSet.add(uName);
                        cOutStream.writeUTF(""); // clear the existing message
                        activeDlm.addElement(uName); // add this user to the active user JList
                        if (!allDlm.contains(uName)) // if username taken previously then don't add to allUser JList otherwise add it
                            allDlm.addElement(uName);
                        new MsgRead(clientSocket, uName).start(); // create a thread to read messages
                        new PrepareCLientList().start(); //create a thread to update all the active clients
                    }
                } catch (IOException ioex) {  // throw any exception occurs
                    ioex.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MsgRead extends Thread { // this class reads the messages coming from client and take appropriate actions
        Socket s;
        String Id;

        private MsgRead(Socket s, String uname) { // socket and username will be provided by client
            this.s = s;
            this.Id = uname;
        }

        @Override
        public void run() {
            while (!allUsersList.isEmpty()) {  // if allUserList is not empty then proceed further
                try {
                    String message = new DataInputStream(s.getInputStream()).readUTF(); // read message from client
                    System.out.println("message read ==> " + message); // just print the message for testing
                    String[] msgList = message.split(":"); // I have used my own identifier to identify what action to take on the received message from client
                    // i have appended actionToBeTaken:clients_for_receiving_msg:message
                    if (msgList[0].equalsIgnoreCase("multicast")) { // if action is multicast then send messages to selected active users
                        String[] sendToList = msgList[1].split(","); //this variable contains list of clients which will receive message
                        for (String usr : sendToList) { // for every user send message
                            try {
                                if (activeUserSet.contains(usr)) { // check again if user is active then send the message
                                    new DataOutputStream(((Socket) allUsersList.get(usr)).getOutputStream())
                                            .writeUTF("< " + Id + " >" + msgList[2]); // put message in output stream
                                }
                            } catch (Exception e) { // throw exceptions
                                e.printStackTrace();
                            }
                        }
                    } else if (msgList[0].equalsIgnoreCase("exit")) { // if a client's process is killed then notify other clients
                        activeUserSet.remove(Id); // remove that client from active usre set
                        new PrepareCLientList().start(); // update the active and all user list on UI

                        Iterator<String> itr = activeUserSet.iterator(); // iterate over other active users
                        while (itr.hasNext()) {
                            String usrName2 = (String) itr.next();
                            if (!usrName2.equalsIgnoreCase(Id)) { // we don't need to send this message to ourself
                                try {
                                    new DataOutputStream(((Socket) allUsersList.get(usrName2)).getOutputStream())
                                            .writeUTF(Id + " disconnected..."); // notify all other active user for disconnection of a user
                                } catch (Exception e) { // throw errors
                                    e.printStackTrace();
                                }
                                new PrepareCLientList().start(); // update the active user list for every client after a user is disconnected
                            }
                        }
                        activeDlm.removeElement(Id); // remove client from Jlist for server
                    }
                } catch (EOFException e) {
                    System.out.println("Client " + Id + " has disconnected."); // if user doesn't exist then show message

                    break; // exit the loop, otherwise it will generate constant stream of the same error
                } catch (SocketException e) {
                    System.out.println("Lost connection to client " + Id);
                    break; // exit the loop
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class PrepareCLientList extends Thread { // it prepares the list of active user to be displayed on the UI
        @Override
        public void run() {
            try {
                String ids = "";
                Iterator itr = activeUserSet.iterator(); // iterate over all active users
                while (itr.hasNext()) { // prepare string of all the users
                    String key = (String) itr.next();
                    ids += key + ",";
                }
                if (!ids.isEmpty()) { // just trimming the list for the safe side.
                    ids = ids.substring(0, ids.length() - 1);
                }
                itr = activeUserSet.iterator();
                while (itr.hasNext()) { // iterate over all active users
                    String key = (String) itr.next();
                    try {
                        new DataOutputStream(((Socket) allUsersList.get(key)).getOutputStream())
                                .writeUTF(":;.,/=" + ids); // set output stream and send the list of active users with identifier prefix :;.,/=
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
