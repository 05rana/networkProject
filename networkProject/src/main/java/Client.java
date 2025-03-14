/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rana
 */
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class Client {
    private static final String SERVER_ADDRESS = "10.6.193.62";
    private static final int SERVER_PORT = 9091;

    private String username;
    private Socket socket;
    private PrintWriter serverOutput;
    private BufferedReader serverInput;
    private MainFrame mainFrame;
    private GameFrame gameFrame;
    private LoginFrame loginFrame;
    
      // Pass the GameFrame reference to Client so we can update it
    private GameFrame currentGameFrame;

      private PrintWriter out;
    private BufferedReader in;
    public Client() {
        loginFrame = new LoginFrame(this);
    }

    // Method to send the username to the server (called after login)
    public void sendUsername(String username) {
        try {
            this.username = username;
            System.out.println("Trying to connect to the server at " + SERVER_ADDRESS + ":" + SERVER_PORT);

            // Open the socket in a separate thread to prevent blocking the GUI
            new Thread(() -> {
                try {
                    socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                    serverOutput = new PrintWriter(socket.getOutputStream(), true);
                    serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    System.out.println("Connected to server successfully.");

                    // Send the username to the server
                    serverOutput.println("USERNAME:"+username);
                    System.out.println("Username sent to server: " + username);

                    // Start the listening thread to receive messages from the server
                    new Thread(this::listenForServerMessages).start();

                    // Request the initial list of users
                    requestUserList();

                    // Now that the connection is set up, show the MainFrame
                    SwingUtilities.invokeLater(() -> {
                        mainFrame = new MainFrame(username,this); // Pass connected users
                        mainFrame.setVisible(true);
                        sendMessage("GET_USERS");
                        loginFrame.dispose();  // Close the login frame once the user is logged in
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Error while connecting to the server: " + e.getMessage());
                }
            }).start();  // Start the socket connection in a new thread

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing username: " + e.getMessage());
        }
    }

    // Listen for server messages (like user list updates)
    private void listenForServerMessages() {
        try {
            String message;
            while ((message = serverInput.readLine()) != null) {
                final String msg = message; 
                System.out.println("Received message from server: " + message);
                if (message.startsWith("USERS:")) {
                    updateUserList(message.substring(6));
                } else if (message.startsWith("JOINED_ROOM:")) {
                  
                     showGameFrame(message);
                } else if (message.startsWith("USERS_IN_ROOM:")) {
                   showPlayers(message.substring(14));
                     
                } else if (message.startsWith("QUESTION:")) {
                   gameFrame.setQuestionText(message.substring(9));
                     
                } else if (message.startsWith("WINNERS:")) {
                   showWinner(message.substring(8));
                     
                } else if (message.startsWith("GAME_ENDED")) {
                   endGame();
                     
                }
                else if (message.startsWith("NO_PLAYERS")) {
                noPlayers();}
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error reading from server: " + e.getMessage());
        }
    }

      // Method to send messages to the server (e.g., for play button click)
    public void sendMessage(String message) {
        serverOutput.println(message);
    }
    // Request the current list of connected users from the server
    private void requestUserList() {
        if (serverOutput != null) {
            serverOutput.println("GET_USERS");
        }
    }
   
    

    // Update the user list from the server
    private void updateUserList(String users) {
        System.out.println("Updated user list: " + users);
        if (mainFrame != null) {
            SwingUtilities.invokeLater(() -> mainFrame.updateUserList(users));
        }
    }
    
     private void showPlayers(String message ) {
        if (gameFrame != null) {
            SwingUtilities.invokeLater(() -> gameFrame.updateUserList(message));
        }
    }
     
     private void showWinner(String message ) {
        if (gameFrame != null) {
            SwingUtilities.invokeLater(() -> gameFrame.updateUserListWithWinner(message));
        }
    }
     
     private void endGame() {
        if (gameFrame != null) {
            SwingUtilities.invokeLater(() -> gameFrame.endGame());
        }
    }

     private void noPlayers() {
        if (gameFrame != null) {
            SwingUtilities.invokeLater(() -> gameFrame.noPlayers());
        }
    }


    // Method to show GameFrame and pass necessary data (like room info)
    private void showGameFrame(String message) {
        // Hide MainFrame
        mainFrame.setVisible(false);

       // Step 1: Extract the room name (everything after "JOINED_ROOM:" and before ",PLAYERS")
        int roomStartIndex = message.indexOf("JOINED_ROOM:") + 12;  // Skipping "JOINED_ROOM:" (12 characters)
        int playersStartIndex = message.indexOf(",PLAYERS:");
        
        // Extract room name
        System.out.println("ind1:,"+roomStartIndex+"ind2:"+playersStartIndex);
        String roomName = message.substring(roomStartIndex, playersStartIndex);
        
        // Step 2: Extract the players list (everything after "PLAYERS:")
        String playersList = message.substring(message.indexOf("PLAYERS:") + 8);  // Skipping "PLAYERS:" (8 characters)
        
        
        // Print the extracted room name and player names
        System.out.println("Room Name: " + roomName);  // Output: Room1
        System.out.println("Players: ");
        // Create the GameFrame and pass Client instance and room info
        gameFrame = new GameFrame(this, roomName,playersList);  // Passing Client and Room Info
        currentGameFrame = gameFrame;  // Save reference to GameFrame
        gameFrame.setVisible(true);
    }


    // Getter for username
    public String getUsername() {
        return username;
    }

  

    public static void main(String[] args) {
        // Start the Client application
        System.out.println("Client application started...");
        SwingUtilities.invokeLater(() -> new Client());
    }
}
