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
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    public void run() {//not read and print methods
        try {
            out.println("Enter your username: ");
            username = in.readLine();
            out.println("Welcome " + username + "!");
            broadcast(username + " has joined the game!");
            sendConnectedPlayers();

            String command;
            while ((command = in.readLine()) != null) {

                if (command.equalsIgnoreCase("pair")) {
                    pairRequest();
                } else if (command.equalsIgnoreCase("leave")) {
                    leaveGame();
                } 
            }
        } catch (IOException e) {
            System.err.println("IO exception in NewClient class");
            e.printStackTrace();
        } finally {
            disconnect();
        }

    }

   private void pairRequest() {//join buttonre
        Room room = findAvailableRoom();
        if (room == null) {
            room = new Room();
            rooms.add(room);
        }
        room.addPlayer(this);
        currentRoom = room;
    }

    private void leaveGame() {//leave button from room and game
       currentRoom.removePlayer(this);
       if (currentRoom==null) 
        rooms.remove(currentRoom);
       
    }

    private void disconnect() {
        clients.remove(this);
        if (currentRoom != null) {
            leaveGame();
        }
        broadcast(username + " has disconnected");
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        for (NewClient aclient : clients) {
            aclient.out.println(message);
        }//to all clients
    }

    private void broadcastToRoom(Room room, String message) {
        for (NewClient player : room.getPlayers()) {
            player.out.println(message);
        }
    }

    private void sendConnectedPlayers() {
        StringBuilder playerList = new StringBuilder("Connected players: ");
        for (NewClient aclient : clients) {
            if (aclient.username != null) {
                playerList.append(aclient.username).append(", ");
            }
        }
        out.println(playerList.toString());
    }

    private Room findAvailableRoom() {
        for (Room room : rooms) {
            if (!room.isGameStarted() && room.getPlayersCount() < 3) {
                return room;
            }
        }
        return null;
    }

    public String getUsername() {
        return username;
    }

    void sendMessage(String message) {
        out.println(message);  // Implemented to send messages to the client
    }
}
