/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author rana
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
class Room {
    private String roomName;
    private Map<String, Integer> playerNames;
    private int round;
    private boolean started;
    public Room(String roomName) {
        this.roomName = roomName;
        this.playerNames =  new HashMap<>();
        this.round=0;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getRound() {
        return round;
    }
    public void setRound(int round) {
        this.round = round;
    }
    
    public boolean getStarted() {
        return started;
    }
    
    public void setStarted(boolean started) {
        this.started = started;
    }
    
    public Map<String, Integer> getPlayersList() {
        return playerNames;
    }

     
    public void addPlayer( String playerName) {
        playerNames.put(playerName,0);
        if (playerNames.size() == 3 ){
            started=true;
        }
        
    }
    
    public void updatePlayerScore( String playerName,int score) {
        playerNames.put(playerName,score);
        
    }

    public void removePlayer(String username) {
            playerNames.remove(username);
    }

    public boolean hasWaitingPlayers() {
        return !started; 
    }
    
     public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Add the round
        sb.append("round:").append(round).append(";");

        System.out.println("round from toString is:"+round);
        // Add player scores in the desired format (name:score,name:score,...)
        for (Map.Entry<String, Integer> entry : playerNames.entrySet()) {
             System.out.println("user score from toString is:"+entry.getValue());
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
        }

        // Remove the trailing comma
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
     
     public  String getWinnerString() {
    // Initialize variables to track the highest score and a list for winners
    List<String> winners = new ArrayList<>();
    int maxScore = Integer.MIN_VALUE;

    // Iterate through the map to find the players with the highest score
    for (Map.Entry<String, Integer> entry : playerNames.entrySet()) {
        String player = entry.getKey();
        int score = entry.getValue();

        // If a new higher score is found, reset the winner list
        if (score > maxScore) {
            winners.clear(); // Clear previous winners
            winners.add(player); // Add the new winner
            maxScore = score; // Update the highest score
        }
        // If the score is equal to the current highest, add the player to the winner list
        else if (score == maxScore) {
            winners.add(player);
        }
    }

    // Join the winners list into a single string
    return String.join(", ", winners);
}
}