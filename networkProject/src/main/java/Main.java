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
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final int PORT = 9091;
    private static Map<String, Room> rooms = new HashMap<>();  // Maps room name to Room object
    private static Map<Socket, String> clientUsernames = new ConcurrentHashMap<>();
    
    public static void main(String[] args) {
        System.out.println("Server started on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Client handler
    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        private Room assignedRoom;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("message is: "+message);
                    processClientMessage(message);  // Process the received message
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientUsernames.remove(clientSocket); // Remove the client from the user list
                    clientSocket.close(); // Close the socket
                    System.out.println("Client disconnected.");
                    broadcastUserList();
                    
                    if (assignedRoom != null) {
                    assignedRoom.removePlayer(username);
                    
                    broadcastUserList(); // Update other clients about the disconnection
                    List<String> usersList = new ArrayList<>(assignedRoom.getPlayersList().keySet());
                     String message = "USERS_IN_ROOM:" + assignedRoom.toString();
                    broadcastToSpecificClients(message,usersList);
                    if(assignedRoom.getPlayersList().size() == 1 && assignedRoom.getStarted()) {
                    System.out.println("No Other Players.");
                     message = "NO_PLAYERS" ;
                     broadcastToSpecificClients(message,usersList);
                    
                    }
                    
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Process the client's message
        private void processClientMessage(String message) {
            if (message.startsWith("USERNAME:")) {
                // Start game logic
              this.username = message.substring(9);  // Skipping the first 9 characters ("START_GAME:")
        
               clientUsernames.put(clientSocket,username);
                System.out.println(username + " connected.");
                broadcastUserList();
               
            } else if (message.startsWith("JOIN_ROOM:")) {
                // Room join logic
                assignedRoom = assignToRoom(clientSocket,username);
                System.out.println("JOIN_ROOM:You joined " + assignedRoom);
                out.println("JOINED_ROOM:"+assignedRoom.getRoomName()+",PLAYERS:"+assignedRoom.toString());
                 broadcastRoomUpdate();
                 if (assignedRoom.hasWaitingPlayers()==false){
                     StartRoundOne();
                     broadcastRoomUpdate();
                 } else {
                     startWaitingTimer();
                 }
            } else if (message.startsWith("GET_USERS")) {
                broadcastUserList();
            } else if (message.startsWith("ANSWER:")) {
               
                   String answer=message.substring(7);
                    System.out.println("got answer:"+answer+" from user:"+username);
                   switch (assignedRoom.getRound()){
                       case 1:
                           if (answer.equalsIgnoreCase("SOS")){
                               //TODO: update user score
                               assignedRoom.updatePlayerScore(username,assignedRoom.getPlayersList().get(username)+1);
                               broadcastRoomUpdate();
                               StartRoundTwo();
                           }
                        case 2:
                           if (answer.equalsIgnoreCase("IT")){
                               //TODO: update user score
                               assignedRoom.updatePlayerScore(username,assignedRoom.getPlayersList().get(username)+1);
                               broadcastRoomUpdate();
                               StartRoundThree();
                           }
                        case 3:
                            if (answer.equalsIgnoreCase("NW")){
                                broadcastRoomUpdate();
                                assignedRoom.updatePlayerScore(username,assignedRoom.getPlayersList().get(username)+1);
                                 List<String> usersList = new ArrayList<>(assignedRoom.getPlayersList().keySet());
                                broadcastToSpecificClients("WINNERS:"+assignedRoom.getWinnerString(),usersList);
                                assignedRoom.setFinished(true);
                           }
                           
                   
                   }
                     
                } else if (message.startsWith("START_GAME")) {
                // Update user list logic
                System.out.println("Updating user list...");
                // Example response: send user list to client
                out.println("UPDATE_USER_LIST:User1,User2,User3");
            } else {
                // Unknown message
                System.out.println("Received unknown message: " + message);
            }
        }
    
        // Assign a client to a room
        private Room assignToRoom(Socket clientSocket, String username) {
//             Find a room with waiting players

            for (Room room : rooms.values()) {
                System.out.println("---- Looking for a room ");
                if (room.hasWaitingPlayers()) {
                    System.out.println("Room is found: "+room.getRoomName());
                    room.addPlayer(username);
                    broadcastRoomUpdate();
                    assignedRoom=room;
                    return room;
                }
            }
            System.out.println("----- Create a new room");
            // If no rooms with waiting players, create a new room
            Room newRoom = new Room("Room" + (rooms.size() + 1));
            rooms.put(newRoom.getRoomName(), newRoom);
            newRoom.addPlayer(username);
            //TODO set timer
            return newRoom;
        }
       
         
                 private void broadcastUserList() {
                     String message = "USERS:" + String.join(",", clientUsernames.values());
    for (Map.Entry<Socket, String> entry : clientUsernames.entrySet()) {
        Socket clientSocket = entry.getKey();
        String username = entry.getValue();

        // Check if the current client username matches any in the targetUsernames list
            try {
            System.out.print("broadcasting to all message:"+message);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(message); // Send the message to the client
                System.out.println("Sent message to " + username + ": " + message);
            } catch (IOException e) {
                System.err.println("Error sending message to " + username);
                e.printStackTrace();
            }
    }
}
         private  void broadcastToSpecificClients(String message, List<String> targetUsernames) {
             
             if (!assignedRoom.getFinished()){
             
             
    for (Map.Entry<Socket, String> entry : clientUsernames.entrySet()) {
        Socket clientSocket = entry.getKey();
        String username = entry.getValue();

        // Check if the current client username matches any in the targetUsernames list
        if (targetUsernames.contains(username)) {
            try {
            System.out.print("broadcasting to all message:"+message);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(message); // Send the message to the client
                System.out.println("Sent message to " + username + ": " + message);
            } catch (IOException e) {
                System.err.println("Error sending message to " + username);
                e.printStackTrace();
            }
        }
    }
             }
}
        // Broadcast the updated player list to all clients in the room
         private void broadcastRoomUpdate() {
             if( assignedRoom!=null ){
                 
            String message = "USERS_IN_ROOM:" + assignedRoom.toString();
            System.out.println("broadcasted message:"+ message);
             List<String> usersList = new ArrayList<>(assignedRoom.getPlayersList().keySet());
            broadcastToSpecificClients(message,usersList);
                
            }
        }
         
         
                 // Broadcast the updated player list to all clients in the room
         private void StartRoundOne() {
             if( assignedRoom!=null ){
                 assignedRoom.setRound(1);
                 assignedRoom.setStarted(true);
                 endGameTimer();
            String q = "QUESTION:⚠Question 1: What does the Morse code ... --- ... represent?"; //SOS
             List<String> usersList = new ArrayList<>(assignedRoom.getPlayersList().keySet());
            broadcastToSpecificClients(q,usersList);
            }
        }
         
                   // Broadcast the updated player list to all clients in the room
         private void StartRoundTwo() {
             if( assignedRoom!=null ){
                 assignedRoom.setRound(2);
            String q = "QUESTION:⚠Question 2: What does the Morse code .. - represent?"; // IT
             List<String> usersList = new ArrayList<>(assignedRoom.getPlayersList().keySet());
            broadcastToSpecificClients(q,usersList);
             }
        }
                   // Broadcast the updated player list to all clients in the room
         private void StartRoundThree() {
             if( assignedRoom!=null ){
                 assignedRoom.setRound(3);
            String q = "QUESTION:⚠Question 3: What does the Morse code  -. .-- represent?"; // NW
              List<String> usersList = new ArrayList<>(assignedRoom.getPlayersList().keySet());
            broadcastToSpecificClients(q,usersList);
             }
        }

 private void endGameTimer() {
        Timer gameEndTimer = new Timer();
        gameEndTimer.schedule(new TimerTask() {
            public void run() {
                String message = "GAME_ENDED" ;
            System.out.println("broadcasted message:"+ message);
             List<String> usersList = new ArrayList<>(assignedRoom.getPlayersList().keySet());
            broadcastToSpecificClients(message,usersList);
            assignedRoom.setFinished(true);
            }
        }, 30000); // 30 seconds
    }
           private void startWaitingTimer() {
        Timer gameStartTimer = new Timer();
        gameStartTimer.schedule(new TimerTask() {
            public void run() {
                if ( assignedRoom.getPlayersList().size() >= 2 && assignedRoom.hasWaitingPlayers()) {
                   
                        System.out.println("Timer completed. Starting game now.");
                        StartRoundOne();
                        broadcastRoomUpdate();
                } else if(assignedRoom.getPlayersList().size()!=0 ){
                    System.out.println("Timer completed but not enough players to start the game.");
                    startWaitingTimer();
                }
            }
        }, 30000); // 30 seconds
    }

    }
}



