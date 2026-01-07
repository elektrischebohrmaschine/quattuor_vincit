package fhtw.quattuor.server;



import fhtw.quattuor.common.model.Player;
import fhtw.quattuor.common.net.NetMessage;
import fhtw.quattuor.common.net.NetType;
import fhtw.quattuor.common.serialization.NetMessageSerializer;
import fhtw.quattuor.common.serialization.PlayerSerializer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerMain {

    private final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static ServerPlayerService playerService;
    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        playerService = new ServerPlayerService();
        playerService.loadPlayersFromDisk();
        playerService.printPlayerUsernames();

        ServerMain server = new ServerMain();
        server.start(5000);
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
    public void removeClients(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    //only for testing
    public void broadcast(String msg, ClientHandler from) {
        for (ClientHandler handler : clients) {
            if (handler != from) {
                handler.send(msg);
            }
        }
    }

    //Testing end

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

    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final ServerMain server;
        private BufferedReader in;
        private PrintWriter out;

        private final NetMessageSerializer msgSer = new NetMessageSerializer();
        private final PlayerSerializer playerSer = new PlayerSerializer();

        private Player loggedInPlayer = null;

        public ClientHandler(Socket clientSocket, ServerMain server) {
            this.clientSocket = clientSocket;
            this.server = server;

            try{
                this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            }catch(IOException e) {
                e.printStackTrace();
            }
        }

        public void send(String msg){
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
                            case NetType.LOGIN:
                                handleLogin(msg);
                                break;
                            case NetType.REGISTER:
                                handleRegister(msg);
                                break;
                            case NetType.PLAYER_UPDATE:
                                handlePlayerUpdate(msg);
                                break;
                            case NetType.HIGHSCORE_REQUEST:
                                handleHighscoreRequest();
                                break;

                            case NetType.LOGOUT:
                                handleLogout();
                                break;
                            default:
                                sendError(NetType.ERROR, "Unknown message type: " + msg.getType());
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
                    server.onlineUsers.remove(u);
                    server.broadcastPresence(u, false);
                }
                server.removeClients(this);
                try { clientSocket.close(); } catch (IOException ignored) {}
            }
        }

        private void handleLogout() {
            if (loggedInPlayer != null) {
                String u = loggedInPlayer.getUsername();
                server.onlineUsers.remove(u);
                server.broadcastPresence(u, false);
            }
            loggedInPlayer = null;
            NetMessage res = new NetMessage(NetType.LOGOUT_SUCCESS);
            out.println(msgSer.toJson(res));
        }

        private void handleLogin(NetMessage msg) {
            Player p = playerService.authenticate(msg.getUsername(), msg.getPassword());
            if (p == null) {
                Player usernameFound = playerService.findByUsername(msg.getUsername());
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
            server.onlineUsers.add(p.getUsername());

            NetMessage res = new NetMessage(NetType.LOGIN_SUCCESS);
            res.setUsername(p.getUsername());
            res.setPassword(p.getPassword());
            res.setPayload(playerSer.serializePlayer(p));
            out.println(msgSer.toJson(res));

            try {
                NetMessage list = new NetMessage(NetType.ONLINE_LIST);
                list.setPayload(new ObjectMapper().writeValueAsString(server.onlineUsers));
                out.println(msgSer.toJson(list));
            } catch (Exception e) {
                sendError(NetType.ERROR, "Could not create ONLINE_LIST");
            }

            server.broadcastPresence(p.getUsername(), true);
        }

        private void handleRegister(NetMessage msg) {
            boolean ok = playerService.register(msg.getUsername(), msg.getPassword());
            if (!ok) {
                NetMessage res = new NetMessage(NetType.REGISTER_FAIL);
                res.setError("Username exists or invalid input");
                out.println(msgSer.toJson(res));
                return;
            }

            playerService.safePlayersToDisk();
            loggedInPlayer = playerService.authenticate(msg.getUsername(), msg.getPassword());

            NetMessage res = new NetMessage(NetType.REGISTER_SUCCESS);
            res.setUsername(msg.getUsername());
            res.setPassword(msg.getPassword());
            out.println(msgSer.toJson(res));

            if (loggedInPlayer == null) {
                return;
            }

            server.onlineUsers.add(loggedInPlayer.getUsername());

            try {
                NetMessage list = new NetMessage(NetType.ONLINE_LIST);
                list.setPayload(new ObjectMapper().writeValueAsString(server.onlineUsers));
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

            playerService.registerOrUpdate(loggedInPlayer);
            playerService.safePlayersToDisk();

            NetMessage res = new NetMessage(NetType.PLAYER_UPDATE_SUCCESS);
            res.setUsername(loggedInPlayer.getUsername());
            out.println(msgSer.toJson(res));
        }

        private void handleHighscoreRequest() {

            List<Player> sorted = new ArrayList<>(playerService.getPlayers());
            sorted.sort((a, b) -> Integer.compare(b.getHighscore(), a.getHighscore()));

            //Top 10
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
    }
}
