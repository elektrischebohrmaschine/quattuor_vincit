package fhtw.quattuor.client;

import fhtw.quattuor.common.net.NetMessage;
import fhtw.quattuor.common.net.NetType;
import fhtw.quattuor.common.serialization.NetMessageSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static javafx.application.Platform.exit;

public class ClientTCP {

    private final ClientController clientController;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private NetMessageSerializer msgSer;

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

            Thread readerThread = new Thread(() -> serverListener());
            readerThread.setDaemon(true);
            readerThread.start();
        } catch (IOException e) {
            System.err.println("Could not connect to " + host + ":" + port);
            System.err.println(e.getMessage());
            exit();
        }
    }

    public void textClientTCPConnection() {

        try{
            Socket socket = new Socket(host, port);
            System.out.println("Connected to " + host + ":" + port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Thread readerThread = new Thread(() -> {
                try{
                    String line;
                    while((line = in.readLine()) != null){
                        System.out.println(">> " + line);
                    }
                }catch(Exception e){
                    System.out.println("Connection to Server lost.");
                }
            });

            NetMessageSerializer msgSer = new NetMessageSerializer();

            readerThread.setDaemon(true);

            readerThread.start();

            Scanner input = new Scanner(System.in);
            System.out.print("Enter your msg: (/quit to end)");

            while (true) {
                String msg = input.nextLine();

                if (msg.equalsIgnoreCase("/quit")) {
                    break;
                }

                if (msg.startsWith("login ")) {
                    String[] parts = msg.split(" ", 3);
                    if (parts.length < 3) {
                        System.out.println("Usage: login <user> <pass>");
                        continue;
                    }

                    NetMessage m = new NetMessage(NetType.LOGIN);
                    m.setUsername(parts[1]);
                    m.setPassword(parts[2]);
                    out.println(msgSer.toJson(m));
                    continue;
                }

                if (msg.startsWith("register ")) {
                    String[] parts = msg.split(" ", 3);
                    if (parts.length < 3) {
                        System.out.println("Usage: register <user> <pass>");
                        continue;
                    }

                    NetMessage m = new NetMessage(NetType.REGISTER);
                    m.setUsername(parts[1]);
                    m.setPassword(parts[2]);
                    out.println(msgSer.toJson(m));
                    continue;
                }

                out.println(msg);
            }

            socket.close();
            System.out.println("Connection closed.");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void serverListener() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                NetMessage msg = msgSer.fromJson(line);
                // Catch any messages that could not be converted into NetMessage
                if (msg == null || msg.getType() == null) {
                    System.out.println("Non JSON message received: " + line);
                    continue;
                }

                System.out.println("Received following message:");
                System.out.println(msg.getType());
                System.out.println(msg.getUsername());
                System.out.println(msg.getPassword());

                switch (msg.getType()) {
                    case LOGIN_SUCCESS:
                        clientController.callbackLoginSuccess();
                        break;
                    case LOGOUT_SUCCESS:
                        clientController.callbackLogoutSuccess();
                        break;
                    case ERROR:
                        System.out.println("Received Error from Server: " + msg.getError());
                        break;
                    default:
                        System.out.println("Received Message Type: " + msg.getType());
                        System.out.println("No action planned for this Type.");
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.out.println("Server Listener Thread exited: Connection to Server lost.");
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
    }
}
