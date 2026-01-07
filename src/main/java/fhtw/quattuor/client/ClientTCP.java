package fhtw.quattuor.client;

import fhtw.quattuor.common.model.Player;
import fhtw.quattuor.common.net.NetMessage;
import fhtw.quattuor.common.net.NetType;
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
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private NetMessageSerializer msgSer;
    private String currentUsername;
    private String currentPassword;
    private final PlayerSerializer playerSer = new PlayerSerializer();

    private final String host = "localhost";
    private final int port = 5000;

    public ClientTCP(ClientController clientController) {
        this.clientController = clientController;



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
        currentUsername = username;
        currentPassword = password;
        NetMessage m = new NetMessage(NetType.LOGIN);
        m.setUsername(username);
        m.setPassword(password);
        out.println(msgSer.toJson(m));
    }

    public void userLogout() {
        NetMessage m = new NetMessage(NetType.LOGOUT);
        out.println(msgSer.toJson(m));
        currentUsername = null;
        currentPassword = null;
    }

    public void userRegister(String username, String password) {
        currentUsername = username;
        currentPassword = password;
        NetMessage m = new NetMessage(NetType.REGISTER);
        m.setUsername(username);
        m.setPassword(password);
        out.println(msgSer.toJson(m));
    }

    public void updateMyColors(String primaryHex, String fallbackHex) {
        if (currentUsername == null || currentPassword == null) return;

        Player p = new Player();
        p.setUsername(currentUsername);
        p.setPassword(currentPassword);
        p.setPlayerColor(primaryHex);
        p.setOpponentColor(fallbackHex);

        NetMessage m = new NetMessage(NetType.PLAYER_UPDATE);
        m.setUsername(currentUsername);
        m.setPassword(currentPassword);
        m.setPayload(playerSer.serializePlayer(p));

        out.println(msgSer.toJson(m));
    }

}
