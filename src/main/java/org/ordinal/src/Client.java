package org.ordinal.src;

import org.ordinal.src.model.ConnexionFormDetails;
import org.ordinal.src.view.ClientView;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import static org.ordinal.src.view.ConnexionView.USER_ALREADY_TAKEN_MSG;

public class Client extends JFrame {
    private Container c;

    public static void startNewClient(ConnexionFormDetails connexionFormDetails) {
        EventQueue.invokeLater(() -> {
            try {
                Client window = new Client(connexionFormDetails);
                window.c.setVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Client(ConnexionFormDetails connexionFormDetails) {
        initialize(connexionFormDetails);
    }

    private void initialize(ConnexionFormDetails connexionFormDetails) {
        this.c = getContentPane();
        try {
            Socket clientSocket = this.initializeSocket(connexionFormDetails);
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream outStream = new DataOutputStream(clientSocket.getOutputStream());

            outStream.writeUTF(connexionFormDetails.getName());
            String msgFromServer = inputStream.readUTF();
            if (msgFromServer.equals(USER_ALREADY_TAKEN_MSG)) {
                JOptionPane.showMessageDialog(c, USER_ALREADY_TAKEN_MSG + "\n");
            } else {
                new ClientView(connexionFormDetails, clientSocket);
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(c, "Ip or Port number are not valid\n");
            dispose();
            ex.printStackTrace();
        }
    }

    private Socket initializeSocket(ConnexionFormDetails connexionFormDetails) throws Exception {
        return new Socket(connexionFormDetails.getIp(), connexionFormDetails.getPort());
    }
}
