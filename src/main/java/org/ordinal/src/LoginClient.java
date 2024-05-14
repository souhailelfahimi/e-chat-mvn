package org.ordinal.src;

import org.ordinal.src.model.FormDetails;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;


public class LoginClient extends JFrame {

    private int port = 8818;
    private String ip = "localhost";
    private Container c;


    public static void startNewClient(FormDetails formDetails) {
        EventQueue.invokeLater(() -> {
            try {
                LoginClient window = new LoginClient(formDetails);
                window.c.setVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public LoginClient(FormDetails formDetails) {
        this.c = this.getContentPane();
        initialize(formDetails);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize(FormDetails formDetails) { // it will initialize the components of UI

        try {
            Socket clientSocket = new Socket(formDetails.getIp(), formDetails.getPort()); // create a socket
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream()); // create input and output stream
            DataOutputStream outStream = new DataOutputStream(clientSocket.getOutputStream());
            outStream.writeUTF(formDetails.getName()); // send username to the output stream

            String msgFromServer = new DataInputStream(clientSocket.getInputStream()).readUTF(); // receive message on socket
            if (msgFromServer.equals("Username already taken")) {//if server sent this message then prompt formDetails to enter other username
                JOptionPane.showMessageDialog(c, "Username already taken\n"); // show message in other dialog box
            } else {
                new ClientView(formDetails, clientSocket); // otherwise just create a new thread of Client view and close the register jframe
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(c, "Ip or Port number are not valid\n");
            dispose();
            ex.printStackTrace();
        }
    }


}
