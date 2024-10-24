
package com.mycompany.phase1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

class NewClient implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<NewClient> clients;
    private ArrayList<Room> rooms;
    private String username;
    private Room currentRoom;

    public NewClient(Socket c, ArrayList<NewClient> clients, ArrayList<Room> rooms) throws IOException {
        this.client = c;
        this.clients = clients;
        this.rooms = rooms;
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new PrintWriter(client.getOutputStream(), true);

        // Ask for the username
        out.println("Enter your name:");
        this.username = in.readLine();
        
        // Broadcast the updated list of clients when a new client joins
        broadcastClientsList();
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("leave")) {
                    leaveRoom();
                    break;
                } else if (message.startsWith("join")) {
                    joinRoom(message.substring(5)); // Assuming room name follows 'join '
                } else if (currentRoom != null) {
                    sendMessageToRoom(message);
                } else {
                    sendMessageToAll(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Remove client and update others
            clients.remove(this);
            broadcastClientsList();
        }
    }

    private void joinRoom(String roomName) {
        // Find or create the room by name
        for (Room room : rooms) {
            if (room.getName().equals(roomName)) {
                currentRoom = room;
                room.addClient(this);
                broadcastRoomClientList();
                return;
            }
        }

        // If the room doesn't exist, create a new one
        Room newRoom = new Room(roomName);
        rooms.add(newRoom);
        currentRoom = newRoom;
        currentRoom.addClient(this);
        broadcastRoomClientList();
    }

    private void leaveRoom() {
        if (currentRoom != null) {
            currentRoom.removeClient(this);
            broadcastRoomClientList();
            currentRoom = null;
        }
    }

    public void sendMessageToRoom(String message) {
        if (currentRoom != null) {
            currentRoom.broadcast(message);
        }
    }

    public void sendMessageToAll(String message) {
        for (NewClient client : clients) {
            client.out.println(username + ": " + message);
        }
    }

    private void broadcastClientsList() {
        StringBuilder clientList = new StringBuilder("Connected clients: ");
        for (NewClient client : clients) {
            clientList.append(client.username).append(", ");
        }

        for (NewClient client : clients) {
            client.out.println(clientList.toString());
        }
    }

    private void broadcastRoomClientList() {
        if (currentRoom != null) {
            currentRoom.broadcastClientList();
        }
    }

    String getUsername() {
       return username;}
}