

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GameFrame extends JFrame {
    private JTextArea onlinePlayersList;
    private JButton answerButton;
    private JButton leaveButton;
    private UserListUpdateListener userListUpdateListener;
    private Client client;

    // New components for question and answer
    private JLabel questionLabel;
    private JTextField answerTextField;
    private JLabel playersLabel;  // Label for "Online Players"

    public GameFrame(Client c, String title, String users) {
        // Set the title of the frame
        this.client = c;
        setTitle("Room Name: " + title);

        // Initialize the user list model
        
        // Initialize the JList with the user list model
         onlinePlayersList = new JTextArea(10, 30);
         onlinePlayersList.setEditable(false);                // Make it non-editable
        onlinePlayersList.setLineWrap(true);                 // Wrap lines if they exceed width
        onlinePlayersList.setWrapStyleWord(true);            // Wrap at word boundaries

        onlinePlayersList.setText(formatFancyScoreBoard(users));
         JScrollPane scrollPane = new JScrollPane(onlinePlayersList);
        // Initialize the Play and Leave buttons
        answerButton = new JButton("Answer");
        leaveButton = new JButton("Leave");

        // Add listeners for buttons
        answerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle Answer button action here
                onAnswerButtonClicked();
            }
        });

        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle Leave button action
                onLeaveButtonClicked();
            }
        });

        // Initialize the label and text field for the question and answer
        questionLabel = new JLabel("Question will appear here...");
        answerTextField = new JTextField(20);  // Set the width of the answer text field
        playersLabel = new JLabel("Online Players:");  // Label for players list

        
        // Set the layout manager for the frame
        setLayout(new BorderLayout());

        // Create a panel for the question, answer text field, and buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 1));  // 3 rows: question label, answer field, and players list
        topPanel.add(questionLabel);
        topPanel.add(answerTextField);

        // Create a panel for online players list and label
        JPanel playersPanel = new JPanel();
        playersPanel.setLayout(new BorderLayout());
        playersPanel.add(playersLabel, BorderLayout.NORTH); // Add label above the list
        playersPanel.add(scrollPane, BorderLayout.CENTER);  // Add the player list

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(answerButton);
        buttonPanel.add(leaveButton);

        // Add components to the frame
        this.add(playersPanel, BorderLayout.NORTH);  // Add player list panel at the top
        this.add(topPanel, BorderLayout.CENTER);  // Add the top panel (question and answer)
        this.add(buttonPanel, BorderLayout.SOUTH);  // Add the buttons panel at the bottom

        // Set frame properties
        this.setSize(400, 500);  // Increase frame size to accommodate new components
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);  // Center the frame
    }

    // Handle the Answer button click
    private void onAnswerButtonClicked() {
        String answer = answerTextField.getText();
        // Send the answer to the server or handle the logic as needed
        System.out.println("Answer submitted: " + answer);
        // Optionally clear the answer field after submitting
//        String q=questionLabel.getText();
//        String questionNumber = q.substring(10,q.indexOf(":"));
        client.sendMessage("ANSWER:"+answer);
        answerTextField.setText("");
    }

 
    // Handle the Leave button click
    private void onLeaveButtonClicked() {
        // Handle the logic for leaving the room
        System.out.println("Leave button clicked");
        System.exit(0);
    }

    // Set the question text dynamically
    public void setQuestionText(String question) {
        questionLabel.setText(question);
    }

   


    // Update the user list when new users join
    public void updateUserList(String message) {
        System.out.println("receieved updated message:"+message);
       onlinePlayersList.setText(formatFancyScoreBoard(message));  
    }
    
    public void updateUserListWithWinner(String message) {
        System.out.println("receieved updated message:"+message);
       onlinePlayersList.setText(formatFancyWinnerText(message));  
    }

     private String formatFancyScoreBoard(String rawText) {
        StringBuilder sb = new StringBuilder();

        // Extract round and player data from the raw text
        String[] parts = rawText.split(";");
        String roundText = parts[0].split(":")[1];  // Extract the round number
        String playerScores = parts[1];  // Extract the player scores data

        sb.append("Round ").append(roundText).append(" Player Scores:\n");
        sb.append("===============================\n");
        sb.append(String.format("%-20s %-10s\n", "Player", "Score"));
        sb.append("-------------------------------\n");

        // Split the player data by commas
        String[] playerEntries = playerScores.split(",");
        for (String entry : playerEntries) {
            String[] playerData = entry.split(":");
            String playerName = playerData[0];
            int score = Integer.parseInt(playerData[1]);
            sb.append(String.format("%-20s %-10d\n", playerName, score));
        }

        return sb.toString();
    }
     
     public static String formatFancyWinnerText(String winnerString) {
    StringBuilder sb = new StringBuilder();

    // Top box with special Unicode characters (using box-drawing)
    String topLine = "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓";
    sb.append(topLine).append("\n");

    // Decorative crown and winner text
    sb.append("━☆━☆━  🏆 CONGRATULATIONS!!! 🏆  ━☆━☆━\n");
    sb.append("━☆━  THE CROWN GOES TO  ━☆━\n");
    sb.append("\n");

    // Center the winner's names within the box
    String centeredWinner = String.format("%" + (topLine.length() / 2 + winnerString.length() / 2) + "s%s%" + (topLine.length() / 2 - winnerString.length() / 2) + "s", "", winnerString, "");
    sb.append("┃   ").append(centeredWinner).append("   ┃\n");

    // Additional text to emphasize the victory with crown
    sb.append("━☆━  🏅 YOU ARE THE CHAMPION! 🏅  ━☆━\n");

    // Bottom box
    sb.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛\n");

    return sb.toString();
}
    // Set the user list update listener
    public void setUserListUpdateListener(UserListUpdateListener listener) {
        this.userListUpdateListener = listener;
    }


 public static void main(String[] args) {
        // Sample usage
        SwingUtilities.invokeLater(() -> {
            GameFrame gameFrame = new GameFrame(new Client(), "Room 1", "players:1,2,3");
            gameFrame.setQuestionText("What is the capital of France?");
            gameFrame.setVisible(true);
        });
    }
}

