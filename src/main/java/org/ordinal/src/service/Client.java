package org.ordinal.src.service;

import org.ordinal.src.model.ConnexionFormDetails;
import org.ordinal.src.view.ClientView;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import static org.ordinal.src.view.ConnexionView.USER_ALREADY_TAKEN_MSG;

public class Client extends JFrame {
    Logger logger = Logger.getLogger(ClientView.class.getName());
    private Container frame;

    public static void startNewClient(ConnexionFormDetails connexionFormDetails) {
        EventQueue.invokeLater(() -> {
            try {
                Client window = new Client(connexionFormDetails);
                window.frame.setVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Client(ConnexionFormDetails connexionFormDetails) throws ConnectException {
        initialize(connexionFormDetails);
    }

    private void initialize(ConnexionFormDetails connexionFormDetails) throws ConnectException {
        this.frame = getContentPane();
        try {
            Socket clientSocket = this.initializeSocket(connexionFormDetails);
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream outStream = new DataOutputStream(clientSocket.getOutputStream());

            outStream.writeUTF(connexionFormDetails.getName());
            String msgFromServer = inputStream.readUTF();
            if (msgFromServer.equals(USER_ALREADY_TAKEN_MSG)) {
                JOptionPane.showMessageDialog(frame, USER_ALREADY_TAKEN_MSG + "\n");
                dispose();
            } else {
                new ClientView(connexionFormDetails, clientSocket);
                dispose();
            }
        } catch (ConnectException connectionException) {
            JOptionPane.showMessageDialog(frame, "Ip or Port number are not valid\n");
            logger.warning("Ip or Port number are not valid" + connectionException.getMessage());
            dispose();
            throw new ConnectException();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(frame, "Unable to Connect to the server specified \n");
            logger.warning("unable to initialize the communication due to :  \n" + exception.getMessage());
            dispose();
        }
    }

    private Socket initializeSocket(ConnexionFormDetails connexionFormDetails) throws Exception {
        try {
            return new Socket(connexionFormDetails.getIp(), connexionFormDetails.getPort());
        } catch (UnknownHostException e) {
            logger.warning("The host name " + connexionFormDetails.getIp() + " is not recognized. Please check your input.");
            throw new UnknownHostException();
        }

    }
}
