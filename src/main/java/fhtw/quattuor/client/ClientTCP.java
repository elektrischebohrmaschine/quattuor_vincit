package fhtw.quattuor.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import fhtw.quattuor.common.model.GameSession;
import fhtw.quattuor.common.model.Player;
import fhtw.quattuor.common.net.NetMessage;
import fhtw.quattuor.common.net.NetType;
import fhtw.quattuor.common.serialization.GameSessionSerializer;
import fhtw.quattuor.common.serialization.NetMessageSerializer;
import fhtw.quattuor.common.serialization.PlayerSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static javafx.application.Platform.exit;

public class ClientTCP {

    private final ClientController clientController;
    private final PlayerSerializer playerSerializer;
    private final GameSessionSerializer gameSessionSerializer;
    private Player player;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private NetMessageSerializer msgSer;

    private final String host = "localhost";
    private final int port = 5000;

    public ClientTCP(ClientController clientController) {
        this.clientController = clientController;
        this.playerSerializer = new PlayerSerializer();
        this.gameSessionSerializer = new GameSessionSerializer();

        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            msgSer = new NetMessageSerializer();
            System.out.println("Connected to " + host + ":" + port);

            Thread readerThread = new Thread(new ClientServerListener(in, msgSer, clientController));
            readerThread.setDaemon(true);
            readerThread.start();
        } catch (IOException e) {
            System.err.println("Could not connect to " + host + ":" + port);
            System.err.println(e.getMessage());
            exit();
        }
    }

    public void userLogin(String username, String password) {
        NetMessage m = new NetMessage(NetType.LOGIN);
        m.setUsername(username);
        m.setPassword(password);
        out.println(msgSer.toJson(m));
    }

    public void userLogout() {
        NetMessage m = new NetMessage(NetType.LOGOUT);
        out.println(msgSer.toJson(m));
        player = null;
    }

    public void userRegister(String username, String password) {
        NetMessage m = new NetMessage(NetType.REGISTER);
        m.setUsername(username);
        m.setPassword(password);
        out.println(msgSer.toJson(m));
    }
    public void requestAllSessions() {
        NetMessage m = new NetMessage(NetType.GET_ALL_SESSIONS);
        out.println(msgSer.toJson(m));
    }

    public void sessionUpdate(GameSession gameSession) {
        NetMessage m = new NetMessage(NetType.SESSION_UPDATE);
        m.setUsername(player.getUsername());
        m.setPayload(gameSessionSerializer.serializeSession(gameSession));
        out.println(msgSer.toJson(m));
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void updateMyColors(String primaryHex, String fallbackHex) {
        if (player == null) return;

        player.setPlayerColor(primaryHex);
        player.setOpponentColor(fallbackHex);

        NetMessage m = new NetMessage(NetType.PLAYER_UPDATE);
        m.setUsername(player.getUsername());
        m.setPassword(player.getPassword());
        m.setPayload(playerSerializer.serializePlayer(player));

        out.println(msgSer.toJson(m));
    }

    public void requestHighscores() {
        NetMessage m = new NetMessage(NetType.HIGHSCORE_REQUEST);
        out.println(msgSer.toJson(m));
    }

    public void startSession(String opponentName) {
        NetMessage m = new NetMessage(NetType.CREATE_SESSION);
        m.setUsername(player.getUsername());
        m.setPassword(player.getPassword());
        m.setPayload(opponentName);
        out.println(msgSer.toJson(m));
    }

    public void sessionUpdateRequest(int sessionNumber) {
        NetMessage m = new NetMessage(NetType.SESSION_UPDATE_REQUEST);
        m.setUsername(player.getUsername());
        m.setPassword(player.getPassword());
        m.setPayload(Integer.toString(sessionNumber));
        out.println(msgSer.toJson(m));
    }

    public void reportLevelCompleted(int levelId) {
        if (player == null) return;
        if (levelId <= 0) return;

        player.markLevelCompleted(levelId);

        NetMessage m = new NetMessage(NetType.PLAYER_UPDATE);
        m.setUsername(player.getUsername());
        m.setPassword(player.getPassword());
        m.setPayload(playerSerializer.serializePlayer(player));
        out.println(msgSer.toJson(m));
    }

}
