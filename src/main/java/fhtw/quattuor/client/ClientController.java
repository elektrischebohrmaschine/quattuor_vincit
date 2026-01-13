package fhtw.quattuor.client;

import fhtw.quattuor.common.model.GameSession;
import fhtw.quattuor.common.model.Player;
import fhtw.quattuor.common.serialization.PlayerSerializer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.shape.Circle;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import java.util.List;
import java.util.Optional;

public class ClientController {

    private ClientConnectFourGrid connectFourGrid;
    private boolean connected = false;
    ClientTCP clientTCP = new ClientTCP(this);

    private final ObservableList<String> highscoreItems = FXCollections.observableArrayList();
    private final PlayerSerializer playerSer = new PlayerSerializer();

    @FXML
    private TextField txt_username;
    @FXML
    private PasswordField txt_password;
    @FXML
    private CheckBox check_synchronisation;
    @FXML
    private VBox boardContainer;
    @FXML
    private ColorPicker playerColor;
    @FXML
    private ColorPicker opponentColor;
    @FXML
    private Button btn_login;
    @FXML
    private ToggleButton btn_turn;
    @FXML
    private SplitMenuButton splitLevels;
    @FXML
    private MenuItem level1;
    @FXML
    private MenuItem level2;
    @FXML
    private MenuItem level3;
    @FXML
    private ListView<GameSession> sessionList;
    @FXML
    private Text txt_opponent;
    @FXML
    private ListView<String> list_highscore;



    @FXML
    public void initialize() {

        connectFourGrid = new ClientConnectFourGrid(this);

        level1.setOnAction(e -> connectFourGrid.startLevel(1));
        level2.setOnAction(e -> connectFourGrid.startLevel(2));
        level3.setOnAction(e -> connectFourGrid.startLevel(3));

        playerColor.setOnAction(e -> sendMyColors());
        opponentColor.setOnAction(e -> sendMyColors());

        VBox gridNode = connectFourGrid.generateGrid();
        boardContainer.getChildren().add(gridNode);

        connectFourGrid.startLevel(1);

        if (list_online != null) {
            list_online.setItems(onlineItems);
            list_online.setCellFactory(lv -> new ListCell<>() {
                private final Circle statusDot = new Circle(6);
                private final Label nameLabel = new Label();
                private final HBox container = new HBox(8);

                {
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.getChildren().addAll(statusDot, nameLabel);
                }

                @Override
                protected void updateItem(String username, boolean empty) {
                    super.updateItem(username, empty);

                    if (empty || username == null) {
                        setGraphic(null);
                    } else {
                        nameLabel.setText(username);

                        statusDot.setStyle("-fx-fill: #2ecc71;");

                        setGraphic(container);
                    }
                }
            });

        }
        if (list_highscore != null) list_highscore.setItems(highscoreItems);



        check_synchronisation.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Synchronisation");
                alert.setContentText("Hiiii, just so u know... synchro is now on =D");
                alert.showAndWait();
                clientTCP.requestAllSessions();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Synchronisation");
                alert.setContentText("Hiiii, just so u know... synchro is now off");
                alert.showAndWait();
            }
        });
    }

    @FXML
    public void onLoginButtonClick() {
        if (connected) {
            clientTCP.userLogout();
            return;
        }

        // Check if Username and Password fields are set
        boolean valid = true;
        if (txt_username.getText().trim().isEmpty()) {
            txt_username.setStyle("-fx-border-color: red;");
            valid = false;
        } else {
            txt_username.setStyle("");
        }

        if (txt_password.getText().trim().isEmpty()) {
            txt_password.setStyle("-fx-border-color: red;");
            valid = false;
        } else {
            txt_password.setStyle("");
        }

        if (!valid) {
            System.out.println("No username or password entered");
            return;
        }

        clientTCP.userLogin(txt_username.getText().trim(), txt_password.getText().trim());
    }

    @FXML
    private ListView<String> list_online;

    private final ObservableList<String> onlineItems = FXCollections.observableArrayList();
    private final ObjectMapper om = new ObjectMapper();


    public void callbackLoginSuccess(String playerJson) {
        connected = true;
        Platform.runLater(() -> {
           btn_login.setText("We are logged in baybeeeeeeeee! (Logout)");

            try {
                Player p = new PlayerSerializer().deserializePlayer(playerJson);
                if (p != null) {
                    playerColor.setValue(javafx.scene.paint.Color.web(p.getPlayerColor()));
                    opponentColor.setValue(javafx.scene.paint.Color.web(p.getOpponentColor()));
                    clientTCP.setPlayer(p);
                    loadPausedSessionList(p.getGameSessions());
                }
            } catch (Exception ignored) {}

            clientTCP.requestHighscores();
        });
    }

    public void callbackLogoutSuccess() {
        connected = false;
        Platform.runLater(() -> {
            txt_username.clear();
            txt_password.clear();
            onlineItems.clear();
            btn_login.setText("Go!");
        });
    }

    public void callbackLoginFailureUsername() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Login failed");
            alert.setHeaderText("Username unknown");
            alert.setContentText("Would you like to register it instead?\nUsing the following:\nUsername: " + txt_username.getText() + "\n Password: " + txt_password.getText());

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                clientTCP.userRegister(txt_username.getText().trim(), txt_password.getText().trim());
            }
        });
    }

    public void callbackLoginFailurePassword() {
        Platform.runLater(() -> {
            btn_login.setText("Wrong Password! Try again?");
        });
    }

    public void callbackRegisterSuccess() {
        connected = true;
        Platform.runLater(() -> {
            btn_login.setText("Register Success! Logout?");
        });
    }

    public void callbackRegisterFail() {
        connected = false;
        Platform.runLater(() -> {
            btn_login.setText("Registration Failed. Try again?");
        });
    }

    public void loadPausedSessionList(List<GameSession> sessions) {
        Platform.runLater(() -> {
            sessionList.getItems().clear();
            sessionList.getItems().setAll(sessions);
        });

    }

    @FXML
    public void onPausedGamesClick() {
        GameSession selectedSession = sessionList.getSelectionModel().getSelectedItem();
        System.out.println("clicked on " + selectedSession);

        connectFourGrid.loadGameSession(selectedSession);
        txt_opponent.setText(selectedSession.getOpponent());
    }

    public void onMoveCommitted(GameSession gameSession) {
        clientTCP.sessionUpdate(gameSession);
    }

    public void callbackSessionUpdate(GameSession gameSession) {
        connectFourGrid.loadGameSession(gameSession);
        txt_opponent.setText(gameSession.getOpponent());
    }

    public void callbackOnlineList(String jsonPayload) {
        Platform.runLater(() -> {
            try {
                String[] arr = om.readValue(jsonPayload, String[].class);
                onlineItems.setAll(arr);
                onlineItems.sort(String::compareToIgnoreCase);
            } catch (Exception e) {
                System.out.println("ONLINE_LIST parse failed: " + e.getMessage());
            }
        });
    }

    public void callbackPresenceUpdate(String username, String status) {
        Platform.runLater(() -> {
            boolean online = "ONLINE".equalsIgnoreCase(status);

            if (online) {
                if (!onlineItems.contains(username)) onlineItems.add(username);
            } else {
                onlineItems.remove(username);
            }

            onlineItems.sort(String::compareToIgnoreCase);
        });
    }

    public void callbackHighscoreList(String payloadJson) {
        Platform.runLater(() -> {
            try {
                var players = playerSer.deserializePlayers(payloadJson);
                highscoreItems.clear();

                int rank = 1;
                for (Player p : players) {
                    highscoreItems.add(rank + ". " + p.getUsername() + " — " + p.getHighscore());
                    rank++;
                }
            } catch (Exception e) {
                System.out.println("HIGHSCORE_LIST parse failed: " + e.getMessage());
            }
        });
    }


    private static String toHex(javafx.scene.paint.Color c) {
        int r = (int)Math.round(c.getRed() * 255);
        int g = (int)Math.round(c.getGreen() * 255);
        int b = (int)Math.round(c.getBlue() * 255);
        return String.format("#%02X%02X%02X", r, g, b);
    }

    private void sendMyColors() {
        if (!connected) return;
        String playerCl = toHex(playerColor.getValue());
        String opponentCl = toHex(opponentColor.getValue());
        clientTCP.updateMyColors(playerCl, opponentCl);
    }
}