package fhtw.quattuor.server;



import fhtw.quattuor.common.model.Player;
import fhtw.quattuor.common.net.NetMessage;
import fhtw.quattuor.common.net.NetType;
import fhtw.quattuor.common.serialization.NetMessageSerializer;
import fhtw.quattuor.common.serialization.PlayerSerializer;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerMain {

    private final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static ServerPlayerService playerService;

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
                server.removeClients(this);
                try { clientSocket.close(); } catch (IOException ignored) {}
            }
        }

        private void handleLogout() {
            loggedInPlayer = null;
            NetMessage res = new NetMessage(NetType.LOGOUT_SUCCESS);
            out.println(msgSer.toJson(res));
        }

        private void handleLogin(NetMessage msg) {
            Player p = playerService.authenticate(msg.getUsername(), msg.getPassword());
            if (p == null) {
                NetMessage res = new NetMessage(NetType.LOGIN_FAIL);
                res.setError("Wrong username or password");
                out.println(msgSer.toJson(res));
                return;
            }

            loggedInPlayer = p;

            NetMessage res = new NetMessage(NetType.LOGIN_SUCCESS);
            res.setUsername(p.getUsername());
            res.setPassword(p.getPassword());
            // optional: gleich Player zurückgeben
            res.setPayload(playerSer.serializePlayer(p));
            out.println(msgSer.toJson(res));
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

            NetMessage res = new NetMessage(NetType.REGISTER_SUCCESS);
            res.setUsername(msg.getUsername());
            res.setPassword(msg.getPassword());
            out.println(msgSer.toJson(res));
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

            playerService.registerOrUpdate(updated);
            playerService.safePlayersToDisk();

            NetMessage res = new NetMessage(NetType.PLAYER_UPDATE_SUCCESS);
            res.setUsername(updated.getUsername());
            res.setPassword(updated.getPassword());
            out.println(msgSer.toJson(res));
        }

        private void sendError(NetType code, String text) {
            NetMessage res = new NetMessage(code);
            res.setError(text);
            out.println(msgSer.toJson(res));
        }
    }
}
