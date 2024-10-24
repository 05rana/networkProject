
package com.mycompany.phase1;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JFrame {
    private static final String Server_IP = "localhost";
    private static final int Server_port = 9090;
    private JTextField nameField;
    private JButton connectButton, joinButton, leaveButton;
    private JPanel connectPanel, allClientsPanel, roomClientsPanel;
    private JTextArea allClientsArea, roomClientsArea;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client() {
        // Create the GUI components for connecting
        connectPanel = new JPanel();
        nameField = new JTextField(20);
        connectButton = new JButton("Connect");

        connectPanel.add(new JLabel("Enter your name:"));
        connectPanel.add(nameField);
        connectPanel.add(connectButton);

        // Frame setup
        setTitle("Client Connection");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(connectPanel);
        setVisible(true);

        // Connect Button action to establish connection to the server
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    socket = new Socket(Server_IP, Server_port);
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    System.out.println("Connected to server");
                    out.println(nameField.getText()); // Send the client's name
                    showAllClientsPanel();
                    startListeningForMessages();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    // Show all clients panel for broadcasting to all connected clients
    private void showAllClientsPanel() {
        allClientsPanel = new JPanel();
        joinButton = new JButton("Join Room");
        leaveButton = new JButton("Leave");
        allClientsArea = new JTextArea(10, 40); // Area for broadcast messages for all clients
        allClientsArea.setEditable(false);

        allClientsPanel.add(new JLabel("Broadcast to All Clients"));
        allClientsPanel.add(new JScrollPane(allClientsArea));
        allClientsPanel.add(joinButton);
        allClientsPanel.add(leaveButton);

        getContentPane().removeAll();
        add(allClientsPanel);
        revalidate();
        repaint();

        // Join Button action to move to room-specific broadcast
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.println("join room");
                allClientsArea.append("Joined room successfully.\\n");
                showRoomClientsPanel(); // Move to room broadcast panel
            }
        });

        // Leave Button action to send leave command
        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.println("leave");
                allClientsArea.append("Sent leave command to server.\\n");
            }
        });
    }

    // Show room-specific clients panel for broadcasting to clients in the room
    private void showRoomClientsPanel() {
        roomClientsPanel = new JPanel();
        leaveButton = new JButton("Leave Room");
        roomClientsArea = new JTextArea(10, 40); // Area for room-specific messages
        roomClientsArea.setEditable(false);

        roomClientsPanel.add(new JLabel("Broadcast to Room Clients"));
        roomClientsPanel.add(new JScrollPane(roomClientsArea));
        roomClientsPanel.add(leaveButton);

        getContentPane().removeAll();
        add(roomClientsPanel);
        revalidate();
        repaint();

        // Leave Button action in the room to send leave command
        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.println("leave");
                roomClientsArea.append("Left the room.\\n");
                showAllClientsPanel();  // Back to general broadcast panel
            }
        });
    }

    // Start a thread to listen for incoming server messages and show them in respective areas
    private void startListeningForMessages() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    allClientsArea.append("Received from server: " + message + "\\n"); // Show message in the general output area
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        new Client();
    }
}