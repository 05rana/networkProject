/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rana
 */
import javax.swing.*;

public class LoginFrame extends JFrame {
    private Client client;
    private JTextField usernameField;
    private JButton loginButton;

    public LoginFrame(Client client) {
        this.client = client;

        // Initialize components
        usernameField = new JTextField(20);
        loginButton = new JButton("Login");

        // Set up layout
        JPanel panel = new JPanel();
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(loginButton);

        // Add action listener for login
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (!username.isEmpty()) {
                client.sendUsername(username);  // Call sendUsername from Client class
            } else {
                JOptionPane.showMessageDialog(this, "Username cannot be empty.");
            }
        });

        // Set up frame properties
        this.add(panel);
        this.setTitle("Login");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);  // Center the frame
        this.setVisible(true);  // Make sure the frame is visible
    }
}
