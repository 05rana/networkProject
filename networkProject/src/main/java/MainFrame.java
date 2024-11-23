/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rana
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MainFrame extends JFrame {
    private JTextArea onlinePlayersList;
    private DefaultListModel<String> onlinePlayersListModel;
    private JButton playButton;
    private JButton leaveButton;
    private UserListUpdateListener userListUpdateListener;
 private Client client;
 
    public MainFrame(String users, Client c) {
        // Set the title of the frame
        this.client = c;
        setTitle("Online Players");

        // Initialize the user list model
        onlinePlayersListModel = new DefaultListModel<>();

        // Initialize the JList with the user list model
        onlinePlayersList = new JTextArea(10, 30);
        onlinePlayersList.setEditable(false);                // Make it non-editable
        onlinePlayersList.setLineWrap(true);                 // Wrap lines if they exceed width
        onlinePlayersList.setWrapStyleWord(true);            // Wrap at word boundaries
System.out.println("1st time run: users are:"+users);
        onlinePlayersList.setText(formatFancyScoreBoard(users));
        JScrollPane scrollPane = new JScrollPane(onlinePlayersList);
        // Initialize the Play and Leave buttons
        playButton = new JButton("Play");
        leaveButton = new JButton("Leave");

        // Add listeners for buttons
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle Play button action
                System.out.println("ask to join room for user:"+client.getUsername());
                 client.sendMessage("JOIN_ROOM:"+client.getUsername());
            }
        });

        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle Leave button action
                onLeaveButtonClicked();
            }
        });

        // Set the layout manager for the frame
        setLayout(new BorderLayout());

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(playButton);
        buttonPanel.add(leaveButton);

        // Add components to the frame
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);

        // Set frame properties
        this.setSize(300, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // Center the frame
    }

    // Update the user list when new users join
    public void updateUserList(String message) {
       System.out.println("receieved updated message:"+message);
       onlinePlayersList.setText(formatFancyScoreBoard(message));  
    }

     private String formatFancyScoreBoard(String names) {
        // Split the names by comma
        String[] players = names.split(",");
        // Create a fancy output
        StringBuilder output = new StringBuilder();
        output.append("*************************************\n");
        output.append("         * Online Players *          \n");
        output.append("*************************************\n");
        
        for (String player : players) {
            output.append("*  ").append(player.trim()).append("  *\n");
        }

        output.append("*************************************\n");

        // Print the fancy text
         System.out.println("fancy text is :"+ output.toString());
        return output.toString();
    
    }

    // Handle Leave button click (disconnect the client, for example)
    private void onLeaveButtonClicked() {
        // You can handle the "Leave" button action here
        System.out.println("Leave button clicked");
        // Close the frame or disconnect from the server
        System.exit(0);
    }
}


