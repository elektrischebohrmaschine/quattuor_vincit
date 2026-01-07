package fhtw.quattuor.common.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String username;
    private String password;
    private int highscore;
    private String primaryColor = "#FF0000"; //ROT
    private String fallbackColor = "#FFD500"; //GELB
    private String assignedColor = "#FF0000"; // default


    private List<GameSession> gameSessions;

    public Player() {

    }

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.highscore = 0;
        this.gameSessions = new ArrayList<>();
        this.primaryColor = "#FFD500";
        this.fallbackColor = "#FF0000";
        this.assignedColor = this.primaryColor;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getHighscore() {
        return highscore;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public String getFallbackColor() {
        return fallbackColor;
    }

    public String getAssignedColor() {
        return assignedColor;
    }

    public void increaseHighscore() {
        highscore++;
    }


    public List<GameSession> getGameSessions() {
        return gameSessions;
    }

    public void addGameSession(GameSession session) {
        if (gameSessions == null) {
            gameSessions = new ArrayList<>();
        }
        gameSessions.add(session);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }

    public void setPrimaryColor(String c) {
        this.primaryColor = c;
    }

    public void setFallbackColor(String c) {
        this.fallbackColor = c;
    }

    public void setAssignedColor(String c) {
        this.assignedColor = c;
    }

    public void setGameSessions(List<GameSession> gameSessions) {
        this.gameSessions = gameSessions;
    }

}
