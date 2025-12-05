package fhtw.quattuor.client;

import fhtw.quattuor.common.logic.GameLogicSingle;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ClientConnectFourGrid {
    final private int GRID_SIZE_X = 6;
    final private int GRID_SIZE_Y = 7;
    Button[][] buttonArray = new Button[GRID_SIZE_X][GRID_SIZE_Y];
    GameLogicSingle logic = new GameLogicSingle(GRID_SIZE_X, GRID_SIZE_Y);

    final private int BTN_SIZE = 50;
    final private int BTN_SPACING = 2;

    public VBox generateGrid() {
        VBox outer = new VBox();
        HBox[] hBoxArray = new HBox[GRID_SIZE_X];

        for (int i = 0; i < GRID_SIZE_X; i++) {
            hBoxArray[i] = new HBox();
            for (int j = 0; j < GRID_SIZE_Y; j++) {
                Button btn = new Button(" ");
                btn.setPrefSize(BTN_SIZE, BTN_SIZE);
                btn.setStyle("-fx-background-color: lightgray;");

                int row = i;
                int col = j;
                btn.setOnAction(e -> btn_click(row, col));
                buttonArray[i][j] = btn;
                hBoxArray[i].getChildren().add(buttonArray[i][j]);
            }
            hBoxArray[i].setSpacing(BTN_SPACING);
            outer.getChildren().addAll(hBoxArray[i]);
        }

        outer.setSpacing(BTN_SPACING);
        return outer;
    }

    private void btn_click(int row, int col) {
        System.out.println("CLICKED: " + row + " " + col);
        // Call Gamelogic here

        if(logic.valid_move(row, col)) {
            if (logic.isPlayer_one_turn()) {
                buttonArray[row][col].setStyle("-fx-background-color: red;");
            } else {
                buttonArray[row][col].setStyle("-fx-background-color: yellow;");
            }
        }
    }
}
