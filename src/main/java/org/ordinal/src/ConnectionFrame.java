package org.ordinal.src;//Java program to implement
//a Simple Registration Form
//using Java Swing


import org.ordinal.src.model.FormDetails;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectionFrame extends JFrame {

    // Components of the Form
    private Container c;
    private JLabel title;
    private JLabel name;
    private JTextField tname;
    private JLabel ip;
    private JTextField tIp;
    private JLabel cType;
    private JRadioButton server;
    private JRadioButton client;
    private ButtonGroup gengp;
    private JTextArea tadd;
    private JLabel res;
    private JTextField tport;
    private JLabel port;

    // constructor, to initialize the components
    // with default values.
    public ConnectionFrame() {
        setTitle("Registration Form");
        setBounds(300, 90, 483, 331);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        c = getContentPane();
        c.setLayout(null);

        title = new JLabel("E-chat Connexion");
        title.setFont(new Font("Arial", Font.PLAIN, 30));
        title.setSize(300, 30);
        title.setLocation(133, 28);
        c.add(title);

        name = new JLabel("Nom");
        name.setFont(new Font("Arial", Font.PLAIN, 20));
        name.setSize(100, 20);
        name.setLocation(100, 100);
        c.add(name);

        tname = new JTextField();
        tname.setFont(new Font("Arial", Font.PLAIN, 15));
        tname.setSize(233, 20);
        tname.setLocation(200, 100);
        c.add(tname);

        ip = new JLabel("IP");
        ip.setFont(new Font("Arial", Font.PLAIN, 20));
        ip.setSize(100, 20);
        ip.setLocation(100, 150);
        c.add(ip);

        tIp = new JTextField();
        tIp.setEnabled(false);
        tIp.setFont(new Font("Arial", Font.PLAIN, 15));
        tIp.setSize(126, 20);
        tIp.setLocation(133, 152);
        c.add(tIp);

        cType = new JLabel("Start As :");
        cType.setFont(new Font("Arial", Font.PLAIN, 20));
        cType.setSize(100, 20);
        cType.setLocation(100, 200);
        c.add(cType);

        server = new JRadioButton("Server");
        server.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tIp.setEnabled(false);
                tport.setEnabled(false);

            }
        });


        server.setFont(new Font("Arial", Font.PLAIN, 15));
        server.setSelected(true);
        server.setSize(75, 20);
        server.setLocation(200, 200);
        c.add(server);

        client = new JRadioButton("Client");
        client.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tIp.setEnabled(true);
                tport.setEnabled(true);
            }
        });
        client.setFont(new Font("Arial", Font.PLAIN, 15));
        client.setSelected(false);
        client.setSize(80, 20);
        client.setLocation(275, 200);
        c.add(client);

        gengp = new ButtonGroup();
        gengp.add(server);
        gengp.add(client);

        res = new JLabel("");
        res.setFont(new Font("Arial", Font.PLAIN, 20));
        res.setSize(500, 25);
        res.setLocation(100, 500);
        c.add(res);

        JButton cancel = new JButton("cancel");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        cancel.setFont(new Font("Arial", Font.PLAIN, 15));
        cancel.setBounds(314, 253, 100, 31);
        getContentPane().add(cancel);

        JButton start = new JButton("start");
        start.setFont(new Font("Arial", Font.PLAIN, 15));
        start.setBounds(187, 253, 117, 31);
        start.addActionListener(e -> {
            try {
                FormDetails formDetails =new FormDetails(tname.getText(),tIp.getText(),tport.getText());
                if (server.isSelected()) {
                    ServerView.main(null);
                    LoginClient.startNewClient(formDetails,false);
                } else {
                    LoginClient.startNewClient(formDetails,true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }finally {
                dispose();
            }
        });
        getContentPane().add(start);

        port = new JLabel("Port");
        port.setFont(new Font("Arial", Font.PLAIN, 20));
        port.setBounds(269, 145, 62, 30);
        getContentPane().add(port);

        tport = new JTextField();
        tport.setFont(new Font("Arial", Font.PLAIN, 15));
        tport.setEnabled(false);
        tport.setBounds(324, 152, 103, 20);
        getContentPane().add(tport);

        setVisible(true);


    }


}

