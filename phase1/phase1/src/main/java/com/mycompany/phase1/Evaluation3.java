package com.mycompany.phase1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Evaluation3 {
    private static ArrayList<NewClient> clients = new ArrayList<>();
    private static ArrayList<Room> rooms = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9090);

        while (true) {
            System.out.println("Waiting for client connection...");
            Socket client = serverSocket.accept();
            System.out.println("Connected to client");

            NewClient clientThread = new NewClient(client, clients, rooms);
            clients.add(clientThread);
            new Thread(clientThread).start();
        }
    }
}
