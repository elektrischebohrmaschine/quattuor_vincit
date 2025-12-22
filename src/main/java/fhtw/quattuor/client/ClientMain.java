package fhtw.quattuor.client;

import fhtw.quattuor.common.net.NetMessage;
import fhtw.quattuor.common.serialization.NetMessageSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {

        String host = "localhost";
        int port = 5000;

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

                    NetMessage m = new NetMessage("LOGIN");
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

                    NetMessage m = new NetMessage("REGISTER");
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
}
