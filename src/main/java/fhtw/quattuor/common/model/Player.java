package fhtw.quattuor.common.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String username;
    private String password;
    private int highscore;
    private List<GameSession> gameSessions;

    public Player() {

    }

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.highscore = 0;
        this.gameSessions = new ArrayList<>();
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

    public void setGameSessions(List<GameSession> gameSessions) {
        this.gameSessions = gameSessions;
    }
}
