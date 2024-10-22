package com.mycompany.phase1;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Room {
    private ArrayList<NewClient> players = new ArrayList<>();
    private boolean gameStarted = false;
    private Timer gameStartTimer;

    public void addPlayer(NewClient player) {
        players.add(player);
        System.out.println("Player " + player.getUsername() + " has joined the waiting room.");
        broadcastToRoom(player.getUsername() + " has joined the waiting room.");
        if (players.size() == 3) {
            System.out.println("Enough players to start the game. Starting game...");
            startGame();
        } else {
            if (gameStartTimer == null) 
                startWaitingTimer();            
            player.sendMessage("Waiting for more players...");
        }
    }

    public void removePlayer(NewClient player) {
       //after starting the game
        if (isGameStarted()) {
            broadcastToRoom(player.getUsername() + " has left the game.");
            players.remove(player);
            System.out.println("Player " +player.getUsername()  + " has been removed from the game.");
            if (getPlayersCount() == 1) {
                NewClient lastPlayer = getPlayers().get(0);
                lastPlayer.sendMessage("You are the last player. The game is over.");
                 players.remove(lastPlayer);
            }
        }
        
        //befor starting the game
        else{
            broadcastToRoom(player.getUsername()  + " has left the waitting room.");
            players.remove(player);
             System.out.println("Player " +player.getUsername()  + " has been removed from the room.");

        }
    }
        

    private void startWaitingTimer() {
        gameStartTimer = new Timer();
        gameStartTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if ( players.size() >= 2 && !gameStarted) {
                    System.out.println("Timer completed. Starting game now.");
                    startGame();
                } else if(players.size() !=0 ){
                    System.out.println("Timer completed but not enough players to start the game.");
                    startWaitingTimer();
                }
            }
        }, 30000); // 30 seconds
    }

    private void startGame() {
        if (!gameStarted) {
            gameStarted = true;
            broadcastToRoom("Game is starting now with players: " + getPlayersNames());
        }
    }
    
//seters and getters
    public int getPlayersCount() {
        return players.size();
    }

    public ArrayList<NewClient> getPlayers() {
        return players;
    }

    public String getPlayersNames() {
        StringBuilder names = new StringBuilder();
        for (NewClient player : players) {
            names.append(player.getUsername()).append(", ");
        }
        return names.toString().replaceAll(", $", ""); // Remove the last comma
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    private void broadcastToRoom(String message) {
        for (NewClient player : players) {
            player.sendMessage(message);
        }
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
}
