
package com.mycompany.phase1;

import java.util.ArrayList;

public class Room {
    private ArrayList<NewClient> players = new ArrayList<>();
    private String name;

    // Constructor to set the room name
    public Room(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Add a player to the room
    public void addClient(NewClient client) {
        players.add(client);
        System.out.println("Player " + client.getUsername() + " has joined the room: " + name);
        broadcastClientList();
        broadcast(client.getUsername() + " has joined the room.");
    }

    // Remove a player from the room
    public void removeClient(NewClient client) {
        players.remove(client);
        System.out.println("Player " + client.getUsername() + " has left the room.");
        broadcastClientList();
        broadcast(client.getUsername() + " has left the room.");
    }

    // Broadcast a message to all players in the room
    public void broadcast(String message) {
        for (NewClient client : players) {
            client.sendMessageToRoom("Room broadcast: " + message);
        }
    }

    // Broadcast the current list of players in the room
    public void broadcastClientList() {
        StringBuilder clientList = new StringBuilder("Room clients: ");
        for (NewClient client : players) {
            clientList.append(client.getUsername()).append(", ");
        }
        for (NewClient client : players) {
            client.sendMessageToRoom(clientList.toString());
        }
    }
}