package fhtw.quattuor.client;

import fhtw.quattuor.common.model.Board;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;

public class ClientController {

    private ClientConnectFourGrid connectFourGrid;

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
    private TextField txt_username;

    @FXML
    public void initialize() {

        connectFourGrid = new ClientConnectFourGrid();

        VBox gridNode = connectFourGrid.generateGrid();

        boardContainer.getChildren().add(gridNode);
    }
}