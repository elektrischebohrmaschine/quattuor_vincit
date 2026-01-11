package fhtw.quattuor.server;

import fhtw.quattuor.common.model.GameSession;
import fhtw.quattuor.common.model.Player;
import java.util.ArrayList;
import java.util.List;

public class ServerGameSessionService {

    private List<GameSession> sessions = new ArrayList<>();
    private final ServerPlayerService playerService;

    public ServerGameSessionService(ServerPlayerService playerService) {
        this.playerService = playerService;
    }

    public GameSession getSession(int sessionNumber) {
        return sessions.get(sessionNumber);
    }

    public void addSession(GameSession session) {
        sessions.add(session);
    }

    public List<GameSession> getAllSessions() {
        return new ArrayList<>(sessions);
    }

    public GameSession getSessionFromSessionNumber(int sessionNumber) {
        for (GameSession session : sessions) {
            if (session.getSessionNumber() == sessionNumber)
                return session;
        }
        return null;
    }

    public List<GameSession> getPlayersSession(String username) {
        Player player = playerService.findByUsername(username);
        if (!(player == null)) {
            return player.getGameSessions();
        }
        return new ArrayList<>();
    }
}
