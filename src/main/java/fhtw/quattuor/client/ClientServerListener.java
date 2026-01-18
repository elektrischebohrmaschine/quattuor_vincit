package fhtw.quattuor.client;

import fhtw.quattuor.common.net.NetMessage;
import fhtw.quattuor.common.serialization.GameSessionSerializer;
import fhtw.quattuor.common.serialization.NetMessageSerializer;

import java.io.BufferedReader;

public class ClientServerListener implements Runnable {

    private final BufferedReader in;
    private final NetMessageSerializer msgSer;
    private final ClientController clientController;
    private final ClientTCP clientTCP;
    private final GameSessionSerializer gameSessionSer= new GameSessionSerializer();

    public ClientServerListener(BufferedReader in, NetMessageSerializer msgSer, ClientController clientController,  ClientTCP clientTCP) {
        this.in = in;
        this.msgSer = msgSer;
        this.clientController = clientController;
        this.clientTCP = clientTCP;
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
                        clientController.callbackLoginSuccess(msg.getPayload());
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
                        clientController.callbackRegisterSuccess(msg.getPayload());
                        break;
                    case REGISTER_FAIL:
                        clientController.callbackRegisterFail();
                        break;
                    case SESSION_UPDATE:
                        clientController.callbackSessionUpdate(gameSessionSer.deserializeSession(msg.getPayload()));
                        break;
                    case SET_ALL_SESSIONS:
                        clientController.callbackSetAllSessions(msg.getPayload());
                        break;
                    case ONLINE_LIST:
                        clientController.callbackOnlineList(msg.getPayload());
                        break;
                    case PRESENCE_UPDATE:
                        clientController.callbackPresenceUpdate(msg.getUsername(), msg.getPayload());
                        break;
                    case HIGHSCORE_LIST:
                        clientController.callbackHighscoreList(msg.getPayload());
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
            clientTCP.serverListenerCrash();
        }
    }

}
