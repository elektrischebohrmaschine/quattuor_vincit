package fhtw.quattuor.client;

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

            readerThread.setDaemon(true);

            readerThread.start();

            Scanner input = new Scanner(System.in);
            System.out.print("Enter your msg: (/quit to end)");

            while(true){
                String msg = input.nextLine();

                if(msg.equalsIgnoreCase("/quit")){
                    break;
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
