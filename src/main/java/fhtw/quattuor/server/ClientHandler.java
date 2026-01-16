package fhtw.quattuor.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import fhtw.quattuor.common.logic.GameLogic;
import fhtw.quattuor.common.model.GameSession;
import fhtw.quattuor.common.model.Player;
import fhtw.quattuor.common.net.NetMessage;
import fhtw.quattuor.common.net.NetType;
import fhtw.quattuor.common.serialization.GameSessionSerializer;
import fhtw.quattuor.common.serialization.NetMessageSerializer;
import fhtw.quattuor.common.serialization.PlayerSerializer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final ServerMain server;

    private BufferedReader in;
    private PrintWriter out;

    private final NetMessageSerializer msgSer = new NetMessageSerializer();
    private final PlayerSerializer playerSer = new PlayerSerializer();
    private final GameSessionSerializer gameSessionSer = new GameSessionSerializer();

    private Player loggedInPlayer = null;

    public ClientHandler(Socket clientSocket, ServerMain server) {
        this.clientSocket = clientSocket;
        this.server = server;

        try {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String msg) {
        out.println(msg);
    }

    @Override
    public void run() {
        try {
            out.println("Welcome to Quattuor Vincit!");
            out.println("Please LOGIN or REGISTER (JSON).");

            String input;
            while ((input = in.readLine()) != null) {
                System.out.println("Empfangen von: " + clientSocket.getInetAddress() + ": " + input);

                if (input.startsWith("{")) {
                    NetMessage msg = msgSer.fromJson(input);

                    if (msg == null || msg.getType() == null) {
                        sendError(NetType.ERROR, "Unknown JSON format");
                        continue;
                    }

                    switch (msg.getType()) {
                        case NetType.LOGIN -> handleLogin(msg);
                        case NetType.REGISTER -> handleRegister(msg);
                        case NetType.PLAYER_UPDATE -> handlePlayerUpdate(msg);
                        case NetType.HIGHSCORE_REQUEST -> handleHighscoreRequest();
                        case NetType.LOGOUT -> handleLogout();
                        case NetType.CREATE_SESSION -> handleSessionCreation(msg);
                        case NetType.SESSION_UPDATE -> handleSessionUpdate(msg);
                        case NetType.GET_ALL_SESSIONS -> handleSessionGetAll();
                        case NetType.SESSION_UPDATE_REQUEST -> handleSessionUpdateRequest(msg);
                        default -> sendError(NetType.ERROR, "Unknown message type: " + msg.getType());
                    }

                } else {
                    if (loggedInPlayer == null) {
                        out.println("Please LOGIN first.");
                        continue;
                    }
                    server.broadcast(input, this);
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + clientSocket.getInetAddress());
        } finally {
            if (loggedInPlayer != null) {
                String u = loggedInPlayer.getUsername();
                server.getOnlineUsers().remove(u);
                server.broadcastPresence(u, false);
            }
            server.removeClients(this);
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }

    private ServerPlayerService playerService() {
        return ServerMain.getPlayerService();
    }

    // --- ab hier bleibt dein Code praktisch gleich, nur playerService -> playerService() und onlineUsers -> server.getOnlineUsers()

    private void handleSessionUpdateRequest(NetMessage msg) {
        if (loggedInPlayer == null) {
            sendError(NetType.NOT_LOGGED_IN, "Please LOGIN first");
            return;
        }
        if (msg.getPayload() == null || msg.getPayload().isBlank()) {
            sendError(NetType.ERROR, "payload missing");
            return;
        }

        GameSession session = loggedInPlayer.getGameSessionByNumber(Integer.parseInt(msg.getPayload()));
        if (session == null) {
            sendError(NetType.ERROR, "Invalid session number");
            return;
        }

        NetMessage res = new NetMessage(NetType.SESSION_UPDATE);
        res.setUsername(loggedInPlayer.getUsername());
        res.setPayload(gameSessionSer.serializeSession(session));
        out.println(msgSer.toJson(res));
    }

    private void handleSessionGetAll() {
        if (loggedInPlayer == null) {
            sendError(NetType.NOT_LOGGED_IN, "Please LOGIN first");
            return;
        }

        NetMessage res = new NetMessage(NetType.SET_ALL_SESSIONS);
        res.setUsername(loggedInPlayer.getUsername());
        res.setPayload(playerSer.serializePlayer(loggedInPlayer));
        out.println(msgSer.toJson(res));
    }

    private void handleSessionCreation(NetMessage msg) {
        if (loggedInPlayer == null) {
            sendError(NetType.NOT_LOGGED_IN, "Please LOGIN first");
            return;
        }
        if (msg.getPayload() == null || msg.getPayload().isBlank()) {
            sendError(NetType.ERROR, "payload missing");
            return;
        }

        Player opponent = playerService().findByUsername(msg.getPayload());
        if (opponent == null) {
            sendError(NetType.ERROR, "Opponent not found");
            return;
        }

        GameSession session = loggedInPlayer.getGameSessionByOpponent(opponent.getUsername());
        if (session != null) {
            if (session.isFinished()) {
                int sessionNumber = session.getSessionNumber();
                loggedInPlayer.removeGameSessionByNumber(sessionNumber);
                opponent.removeGameSessionByNumber(sessionNumber);
                session = null;
            } else {
                NetMessage res = new NetMessage(NetType.SESSION_UPDATE);
                res.setUsername(loggedInPlayer.getUsername());
                res.setPayload(gameSessionSer.serializeSession(session));
                out.println(msgSer.toJson(res));
                return;
            }
        }

        int sessionNumber = playerService().getHighestSessionNumber() + 1;
        session = new GameSession(opponent.getUsername(), sessionNumber, true, 6, 7);
        loggedInPlayer.updateGameSession(session);

        GameSession oppSession = new GameSession(loggedInPlayer.getUsername(), sessionNumber, false, 6, 7);
        opponent.updateGameSession(oppSession);

        playerService().registerOrUpdate(loggedInPlayer);
        playerService().registerOrUpdate(opponent);
        playerService().safePlayersToDisk();

        server.broadcastAllSessionsToUser(opponent.getUsername());

        NetMessage res = new NetMessage(NetType.SESSION_UPDATE);
        res.setUsername(loggedInPlayer.getUsername());
        res.setPayload(gameSessionSer.serializeSession(session));
        out.println(msgSer.toJson(res));
    }

    private void handleSessionUpdate(NetMessage msg) {
        if (loggedInPlayer == null) {
            sendError(NetType.NOT_LOGGED_IN, "Please LOGIN first");
            return;
        }
        if (msg.getPayload() == null || msg.getPayload().isBlank()) {
            sendError(NetType.ERROR, "payload missing");
            return;
        }

        GameSession session = gameSessionSer.deserializeSession(msg.getPayload());
        if (session == null) {
            sendError(NetType.ERROR, "Error deserializing session");
            return;
        }

        GameSession old = loggedInPlayer.getGameSessionByNumber(session.getSessionNumber());
        boolean wasFinished = (old != null && old.isFinished());

        loggedInPlayer.updateGameSession(session);

        Player opponent = playerService().findByUsername(session.getOpponent());
        if (opponent == null) {
            sendError(NetType.ERROR, "Error deserializing opponent");
            return;
        }

        GameSession oppSession = opponent.getGameSessionByNumber(session.getSessionNumber());
        if (oppSession == null) {
            sendError(NetType.ERROR, "Opponent session not found");
            return;
        }

        oppSession.toggleTurn();
        oppSession.setBoard(session.flippedBoard());
        oppSession.setMoveCount(session.getMoveCount());

        GameLogic gl = new GameLogic(session);
        int outcome = gl.checkWinCondition(session.getBoard());

        boolean nowFinished = (outcome != 0);
        session.setFinished(nowFinished);
        oppSession.setFinished(nowFinished);

        if (nowFinished && !wasFinished) {
            if (outcome == 1) loggedInPlayer.increaseHighscore();
            else if (outcome == 2) opponent.increaseHighscore();
        }

        opponent.updateGameSession(oppSession);

        playerService().registerOrUpdate(opponent);
        playerService().registerOrUpdate(loggedInPlayer);
        playerService().safePlayersToDisk();

        server.broadcastAllSessionsToUser(opponent.getUsername());
        server.broadcastAllSessionsToUser(loggedInPlayer.getUsername());

        System.out.println("Updated GameSession: ID: " + session.getSessionNumber());
    }

    private void handleLogout() {
        if (loggedInPlayer != null) {
            String u = loggedInPlayer.getUsername();
            server.getOnlineUsers().remove(u);
            server.broadcastPresence(u, false);
        }
        loggedInPlayer = null;
        NetMessage res = new NetMessage(NetType.LOGOUT_SUCCESS);
        out.println(msgSer.toJson(res));
    }

    private void handleLogin(NetMessage msg) {
        Player p = playerService().authenticate(msg.getUsername(), msg.getPassword());
        if (p == null) {
            Player usernameFound = playerService().findByUsername(msg.getUsername());
            if (usernameFound != null) {
                NetMessage res = new NetMessage(NetType.LOGIN_FAIL_PASSWORD);
                res.setError("Wrong password for " + msg.getUsername());
                out.println(msgSer.toJson(res));
                return;
            } else {
                NetMessage res = new NetMessage(NetType.LOGIN_FAIL_USERNAME);
                res.setError("User does not exist");
                out.println(msgSer.toJson(res));
                return;
            }
        }

        loggedInPlayer = p;
        server.getOnlineUsers().add(p.getUsername());

        NetMessage res = new NetMessage(NetType.LOGIN_SUCCESS);
        res.setUsername(p.getUsername());
        res.setPassword(p.getPassword());
        res.setPayload(playerSer.serializePlayer(p));
        out.println(msgSer.toJson(res));

        try {
            NetMessage list = new NetMessage(NetType.ONLINE_LIST);
            list.setPayload(new ObjectMapper().writeValueAsString(server.getOnlineUsers()));
            out.println(msgSer.toJson(list));
        } catch (Exception e) {
            sendError(NetType.ERROR, "Could not create ONLINE_LIST");
        }

        server.broadcastPresence(p.getUsername(), true);
    }

    private void handleRegister(NetMessage msg) {
        boolean ok = playerService().register(msg.getUsername(), msg.getPassword());
        if (!ok) {
            NetMessage res = new NetMessage(NetType.REGISTER_FAIL);
            res.setError("Username exists or invalid input");
            out.println(msgSer.toJson(res));
            return;
        }

        playerService().safePlayersToDisk();
        loggedInPlayer = playerService().authenticate(msg.getUsername(), msg.getPassword());

        NetMessage res = new NetMessage(NetType.REGISTER_SUCCESS);
        res.setUsername(msg.getUsername());
        res.setPassword(msg.getPassword());
        res.setPayload(playerSer.serializePlayer(loggedInPlayer));
        out.println(msgSer.toJson(res));

        if (loggedInPlayer == null) return;

        server.getOnlineUsers().add(loggedInPlayer.getUsername());

        try {
            NetMessage list = new NetMessage(NetType.ONLINE_LIST);
            list.setPayload(new ObjectMapper().writeValueAsString(server.getOnlineUsers()));
            out.println(msgSer.toJson(list));
        } catch (Exception e) {
            sendError(NetType.ERROR, "Could not create ONLINE_LIST");
        }

        server.broadcastPresence(loggedInPlayer.getUsername(), true);
    }

    private void handlePlayerUpdate(NetMessage msg) {
        if (loggedInPlayer == null) {
            sendError(NetType.NOT_LOGGED_IN, "Please LOGIN first");
            return;
        }
        if (msg.getPayload() == null || msg.getPayload().isBlank()) {
            sendError(NetType.ERROR, "payload missing");
            return;
        }

        Player updated = playerSer.deserializePlayer(msg.getPayload());
        if (updated == null) {
            sendError(NetType.ERROR, "payload not a Player json");
            return;
        }

        if (!loggedInPlayer.getUsername().equals(updated.getUsername())) {
            sendError(NetType.FORBIDDEN, "You can only update your own player");
            return;
        }

        loggedInPlayer.setPlayerColor(updated.getPlayerColor());
        loggedInPlayer.setOpponentColor(updated.getOpponentColor());

        playerService().registerOrUpdate(loggedInPlayer);
        playerService().safePlayersToDisk();

        NetMessage res = new NetMessage(NetType.PLAYER_UPDATE_SUCCESS);
        res.setUsername(loggedInPlayer.getUsername());
        out.println(msgSer.toJson(res));
    }

    private void handleHighscoreRequest() {
        List<Player> sorted = new ArrayList<>(playerService().getPlayers());
        sorted.sort((a, b) -> Integer.compare(b.getHighscore(), a.getHighscore()));

        int limit = Math.min(10, sorted.size());
        List<Player> top = sorted.subList(0, limit);

        NetMessage res = new NetMessage(NetType.HIGHSCORE_LIST);
        res.setPayload(new PlayerSerializer().serializePlayers(top));
        out.println(msgSer.toJson(res));
    }

    private void sendError(NetType code, String text) {
        NetMessage res = new NetMessage(code);
        res.setError(text);
        out.println(msgSer.toJson(res));
    }

    public void sendToUsername(String username) {
        if (loggedInPlayer == null || !loggedInPlayer.getUsername().equals(username)) return;

        NetMessage res = new NetMessage(NetType.SET_ALL_SESSIONS);
        res.setUsername(username);
        res.setPayload(playerSer.serializePlayer(loggedInPlayer));
        out.println(msgSer.toJson(res));
    }
}
