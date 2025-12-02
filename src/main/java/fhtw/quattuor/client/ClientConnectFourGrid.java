package fhtw.quattuor.client;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ClientConnectFourGrid {
    final private int GRID_SIZE_X = 6;
    final private int GRID_SIZE_Y = 7;
    Button[][] buttonArray = new Button[GRID_SIZE_X][GRID_SIZE_Y];

    final private int BTN_SIZE = 50;

    public VBox generateGrid() {
        VBox outer = new VBox();
        HBox[] hBoxArray = new HBox[GRID_SIZE_X];

        for (int i = 0; i < GRID_SIZE_X; i++) {
            hBoxArray[i] = new HBox();
            for (int j = 0; j < GRID_SIZE_Y; j++) {
                buttonArray[i][j] = new Button("  ");
                buttonArray[i][j].setPrefSize(BTN_SIZE, BTN_SIZE);

                int row = i;
                int col = j;
                buttonArray[i][j].setOnAction(e -> btn_click(row, col));
                hBoxArray[i].getChildren().add(buttonArray[i][j]);
            }
            outer.getChildren().addAll(hBoxArray[i]);
        }

        return outer;
    }

    private void btn_click(int row, int col) {
        System.out.println("CLICKED: " + row + " " + col);
        // Call Gamelogic here
        change_btn(row, col);
    }

    public void change_btn(int row, int col) {
        buttonArray[row][col].setText("X");
    }
}
