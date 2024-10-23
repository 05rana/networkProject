package com.mycompany.phase1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String Server_IP = "localhost";
    private static final int Server_port = 9090;

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket(Server_IP, Server_port)) {
            ServerConnection serverConn = new ServerConnection(socket);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            
            // Start a new thread for reading server responses
            new Thread(serverConn).start();

            // Handle user input
            while (true) {
                System.out.print("> ");
                String command = keyboard.readLine();
                
                // stop the conction
                if (command.equalsIgnoreCase("quit"))
                    break;
                
                // Send command to server
                out.println(command);
            }
        }
        System.exit(0);
    }
}
