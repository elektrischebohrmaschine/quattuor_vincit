package fhtw.quattuor.client;

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
}