package fhtw.quattuor.client;

import fhtw.quattuor.common.model.GameSession;
import fhtw.quattuor.common.model.Player;
import fhtw.quattuor.common.serialization.PlayerSerializer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientJsonTester {
    public static void main(String[] args) {
        Player player = new Player("Max", "Password123");
        GameSession session = new GameSession("Daniel", 1, true);
        player.addGameSession(session);

        PlayerSerializer playerSerializer = new PlayerSerializer();
        String json = playerSerializer.serializePlayer(player);
        System.out.println(json);

        sendJson(json);
    }

    private static void sendJson(String json){
        String host = "localhost";
        int port = 5000;

        try{
            Socket socket = new Socket(host, port);
            System.out.println("Connected to " + host + ":" + port);
            System.out.println("Sending json: " + json);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(json);
            socket.close();
            System.out.println("Connection closed.");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
