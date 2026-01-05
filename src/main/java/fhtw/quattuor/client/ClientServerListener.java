package fhtw.quattuor.client;

import fhtw.quattuor.common.net.NetMessage;
import fhtw.quattuor.common.serialization.NetMessageSerializer;

import java.io.BufferedReader;

public class ClientServerListener implements Runnable {

    private final BufferedReader in;
    private final NetMessageSerializer msgSer;
    private final ClientController clientController;

    public ClientServerListener(BufferedReader in, NetMessageSerializer msgSer, ClientController clientController) {
        this.in = in;
        this.msgSer = msgSer;
        this.clientController = clientController;
    }

    @Override
    public void run() {
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
                    case LOGIN_FAIL_USERNAME:
                        clientController.callbackLoginFailureUsername();
                        break;
                    case LOGIN_FAIL_PASSWORD:
                        clientController.callbackLoginFailurePassword();
                        break;
                    case LOGOUT_SUCCESS:
                        clientController.callbackLogoutSuccess();
                        break;
                    case REGISTER_SUCCESS:
                        clientController.callbackRegisterSuccess();
                        break;
                    case REGISTER_FAIL:
                        clientController.callbackRegisterFail();
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

}
