package fhtw.quattuor.server;



import fhtw.quattuor.common.model.Player;
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

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final ServerMain server;
        private BufferedReader in;
        private PrintWriter out;

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
            try{
                out.println("Welcome to Quattuor Vincit!");

                String input;

                while((input = in.readLine()) != null) {
                    System.out.println("Empfangen von: " + clientSocket.getInetAddress() + ": " + input);

                    //Game Logic
                    if(input.startsWith("{")){
                        // Catch JSON and interpret it
                        PlayerSerializer playerSerializer = new PlayerSerializer();
                        Player player = playerSerializer.deserializePlayer(input);
                        playerService.registerOrUpdate(player);
                        playerService.safePlayersToDisk();
                    }

                    server.broadcast(input, this);
                }
            }catch(IOException e) {
                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
            }finally {
                server.removeClients(this);
                try{
                    clientSocket.close();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
