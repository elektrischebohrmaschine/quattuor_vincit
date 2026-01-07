package fhtw.quattuor.client;

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


import java.util.Optional;

public class ClientController {

    private ClientConnectFourGrid connectFourGrid;
    private boolean connected = false;
    ClientTCP clientTCP = new ClientTCP(this);


    @FXML
    private TextField txt_username;
    @FXML
    private PasswordField txt_password;
    @FXML
    private CheckBox check_synchronisation;
    @FXML
    private Text txt_player;
    @FXML
    private VBox boardContainer;
    @FXML
    private ColorPicker colorPrimary;
    @FXML
    private ColorPicker colorFallback;
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
    public void initialize() {

        level1.setOnAction(e -> connectFourGrid.startLevel(1));
        level2.setOnAction(e -> connectFourGrid.startLevel(2));
        level3.setOnAction(e -> connectFourGrid.startLevel(3));

        colorPrimary.setOnAction(e -> sendMyColors());
        colorFallback.setOnAction(e -> sendMyColors());

        connectFourGrid = new ClientConnectFourGrid();
        VBox gridNode = connectFourGrid.generateGrid();
        boardContainer.getChildren().add(gridNode);
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
                    colorPrimary.setValue(javafx.scene.paint.Color.web(p.getPrimaryColor()));
                    colorFallback.setValue(javafx.scene.paint.Color.web(p.getFallbackColor()));
                }
            } catch (Exception ignored) {}
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

    private static String toHex(javafx.scene.paint.Color c) {
        int r = (int)Math.round(c.getRed() * 255);
        int g = (int)Math.round(c.getGreen() * 255);
        int b = (int)Math.round(c.getBlue() * 255);
        return String.format("#%02X%02X%02X", r, g, b);
    }

    private void sendMyColors() {
        if (!connected) return;
        String primary = toHex(colorPrimary.getValue());
        String fallback = toHex(colorFallback.getValue());
        clientTCP.updateMyColors(primary, fallback);
    }


}