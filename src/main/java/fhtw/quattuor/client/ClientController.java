package fhtw.quattuor.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;

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
    private ColorPicker colorSelect;
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

        connectFourGrid = new ClientConnectFourGrid();
        VBox gridNode = connectFourGrid.generateGrid();
        boardContainer.getChildren().add(gridNode);
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

    public void callbackLoginSuccess() {
        connected = true;
        Platform.runLater(() -> {
           btn_login.setText("We are logged in baybeeeeeeeee! (Logout)");
        });
    }

    public void callbackLogoutSuccess() {
        connected = false;
        Platform.runLater(() -> {
            txt_username.clear();
            txt_password.clear();
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
}