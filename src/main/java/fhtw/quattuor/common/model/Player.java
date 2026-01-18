package fhtw.quattuor.common.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Player {
    private String username;
    private String password;
    private int highscore;
    private String playerColor = "#FF0000"; //ROT
    private String opponentColor = "#FFD500"; //GELB
    private List<Integer> completedLevels;

    private List<GameSession> gameSessions;

    public Player() {

    }

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.highscore = 0;
        this.gameSessions = new ArrayList<>();
        this.playerColor = "#FFD500";
        this.opponentColor = "#FF0000";
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

    public String getPlayerColor() {
        return playerColor;
    }

    public String getOpponentColor() {
        return opponentColor;
    }

    public List<GameSession> getGameSessions() {
        return gameSessions;
    }

    public void increaseHighscore() {
        highscore++;
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

    public void setPlayerColor(String c) {
        this.playerColor = c;
    }

    public void setOpponentColor(String c) {
        this.opponentColor = c;
    }

    public void setGameSessions(List<GameSession> gameSessions) {
        this.gameSessions = gameSessions;
    }

    public void updateGameSession(GameSession session) {
        if (gameSessions == null) {
            gameSessions = new ArrayList<>();
        }
        for (GameSession s : gameSessions) {
            if (s.getSessionNumber() == session.getSessionNumber()) {
                gameSessions.set(gameSessions.indexOf(s), session);
                return;
            }
        }

        // Add GameSession to List if it does not exist yet
        gameSessions.add(session);
    }

    public GameSession getGameSessionByOpponent(String opponentName) {
        for (GameSession s : gameSessions) {
            if (s.getOpponent().equals(opponentName)) {
                return s;
            }
        }
        return null;
    }

    public GameSession getGameSessionByNumber(int sessionNumber) {
        for (GameSession s : gameSessions) {
            if (s.getSessionNumber() == sessionNumber) {
                return s;
            }
        }
        return null;
    }

    public void removeGameSessionByNumber(int sessionNumber) {
        gameSessions.removeIf(gameSession -> gameSession.getSessionNumber() == sessionNumber);
    }

    public List<Integer> getCompletedLevels() {
        if (completedLevels == null) completedLevels = new ArrayList<>();
        return completedLevels;
    }

    public boolean hasCompletedLevel(int levelId) {
        return getCompletedLevels().contains(levelId);
    }

    public void markLevelCompleted(int levelId) {
        if (levelId <= 0) return;
        Set<Integer> unique = new LinkedHashSet<>(getCompletedLevels());
        unique.add(levelId);
        completedLevels = new ArrayList<>(unique);
    }

    public void setCompletedLevels(List<Integer> completedLevels) {
        this.completedLevels = (completedLevels == null) ? new ArrayList<>() : completedLevels;
    }
}
