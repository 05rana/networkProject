package com.mycompany.phase1;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private JFrame frame;
    private JTextField textField;
    private JTextArea textArea;
    private PrintWriter out;
    private Socket socket;

    public Client() {
        // إعداد الإطار
        frame = new JFrame("Client");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // إعداد مربع النص
        textField = new JTextField();
        textField.setBounds(10, 10, 200, 30);
        frame.add(textField);

        // إعداد منطقة النص
        textArea = new JTextArea();
        textArea.setBounds(10, 50, 280, 200);
        frame.add(textArea);

        // إعداد الزر
        JButton button = new JButton("Send");
        button.setBounds(220, 10, 70, 30);
        frame.add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // عرض الإطار
        frame.setVisible(true);
    }

    public void connectToServer(String serverAddress, int port) throws Exception {
        socket = new Socket(serverAddress, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        Scanner in = new Scanner(socket.getInputStream());
        while (in.hasNextLine()) {
            String line = in.nextLine();
            textArea.append(line + "\n");
        }
    }

    private void sendMessage() {
        String message = textField.getText();
        out.println(message);
        textField.setText("");
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.connectToServer("localhost", 9090);  // يجب تعديل هذا السطر بناءً على عنوان IP الخادم
    }
}
