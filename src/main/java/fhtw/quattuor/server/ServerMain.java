package fhtw.quattuor.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import fhtw.quattuor.common.model.Player;
import fhtw.quattuor.common.net.NetMessage;
import fhtw.quattuor.common.net.NetType;
import fhtw.quattuor.common.serialization.NetMessageSerializer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerMain {

    private final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static ServerPlayerService playerService;
    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        playerService = new ServerPlayerService();
        playerService.loadPlayersFromDisk();
        playerService.printPlayerUsernames();

        new ServerMain().start(5000);
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server runs on Port " + port);

            while (true) {
                System.out.println("Waiting for Client....");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted Client: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ServerPlayerService getPlayerService() {
        return playerService;
    }

    public Set<String> getOnlineUsers() {
        return onlineUsers;
    }

    public void removeClients(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    // only for testing
    public void broadcast(String msg, ClientHandler from) {
        for (ClientHandler handler : clients) {
            if (handler != from) handler.send(msg);
        }
    }

    public void broadcastNet(NetMessage msg) {
        NetMessageSerializer ser = new NetMessageSerializer();
        String json = ser.toJson(msg);
        for (ClientHandler handler : clients) {
            handler.send(json);
        }
    }

    public void broadcastPresence(String username, boolean online) {
        NetMessage msg = new NetMessage(NetType.PRESENCE_UPDATE);
        msg.setUsername(username);
        msg.setPayload(online ? "ONLINE" : "OFFLINE");
        broadcastNet(msg);
    }

    public void broadcastAllSessionsToUser(String username) {
        for (ClientHandler handler : clients) {
            handler.sendToUsername(username);
        }
    }

    public void broadcastCompLevelsSnapshot() {
        try {
            Map<String, List<Integer>> map = new HashMap<>();

            for (String user : onlineUsers) {
                Player player = playerService.findByUsername(user);
                if (player != null) {
                    map.put(user, player.getCompletedLevels());
                }
            }

            NetMessage msg = new NetMessage(NetType.UPDATE_COMP_LEVELS);
            msg.setPayload(new ObjectMapper().writeValueAsString(map));
            broadcastNet(msg);

        } catch (Exception e) {
            System.out.println("broadcastCompLevelsSnapshot failed: " + e.getMessage());
        }
    }
}
