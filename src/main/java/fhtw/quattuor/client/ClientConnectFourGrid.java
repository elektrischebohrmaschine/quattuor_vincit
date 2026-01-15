package fhtw.quattuor.client;

import fhtw.quattuor.common.logic.GameLogic;
import fhtw.quattuor.common.logic.GameLogicSingle;
import fhtw.quattuor.common.logic.SingleLevels;
import fhtw.quattuor.common.model.CellStatus;
import fhtw.quattuor.common.model.GameSession;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ClientConnectFourGrid {
    final private ClientController controller;
    final private int GRID_HEIGHT_X = 6;
    final private int GRID_WIDTH_Y = 7;

    private Button[][] buttonArray = new Button[GRID_HEIGHT_X][GRID_WIDTH_Y];
    private GameLogic logic = new GameLogic(GRID_HEIGHT_X, GRID_WIDTH_Y);
    private TextField showsWinner = new TextField();
    final private String ONGOING_GAME_TEXT  = "WHO IS WINNING?";
    final private String WIN_TEXT = "The Winner is: ";
    final private String DRAW_TEXT = "It's a draw!";

    final private int BTN_SIZE = 50;
    final private int BTN_SPACING = 2;

    final private String COLOR_BTN_ENABLE = "-fx-background-color: lightgray;";
    final private String COLOR_BTN_DISABLE = "-fx-background-color: #ececec;";
    private String playerOneColor = "-fx-background-color: yellow;";
    private String playerTwoColor = "-fx-background-color: red;";

    public ClientConnectFourGrid(ClientController controller) {
        this.controller = controller;
    }

    public VBox generateGrid() {
        VBox outer = new VBox();
        HBox[] hBoxArray = new HBox[GRID_HEIGHT_X];

        outer.getChildren().add(showsWinner);

        for (int i = 0; i < GRID_HEIGHT_X; i++) {
            hBoxArray[i] = new HBox();
            for (int j = 0; j < GRID_WIDTH_Y; j++) {
                Button btn = new Button(" ");
                btn.setPrefSize(BTN_SIZE, BTN_SIZE);
                if (i == GRID_HEIGHT_X - 1) {
                    btn.setStyle(COLOR_BTN_ENABLE);
                } else {
                    btn.setStyle(COLOR_BTN_DISABLE);
                }

                int row = i;
                int col = j;
                btn.setOnAction(e -> btn_click(row, col));
                buttonArray[i][j] = btn;
                hBoxArray[i].getChildren().add(buttonArray[i][j]);
            }
            hBoxArray[i].setSpacing(BTN_SPACING);
            hBoxArray[i].setAlignment(Pos.CENTER);
            showsWinner.setEditable(false);
            showsWinner.setText(ONGOING_GAME_TEXT);
            showsWinner.setAlignment(Pos.CENTER);

            outer.getChildren().addAll(hBoxArray[i]);
        }

        outer.setSpacing(BTN_SPACING);
        return outer;
    }

    private void btn_click(int row, int col) {
        // DEBUG OUTPUT
        System.out.println("CLICKED: " + row + " " + col);

        // GAMELOGIC
        if (logic.valid_move(row, col)) {
            if (logic.isPlayer_one_turn()) {
                buttonArray[row][col].setStyle(playerOneColor);
            } else {
                buttonArray[row][col].setStyle(playerTwoColor);
            }
            logic.toggle_player_turn();

            colorValidButton(row, col);


            int winner = logic.getWinner();
            if (logic.getLevelMode()) {
                // Singleplayer Level Logic
                if (winner == 1) {
                    showsWinner.setText("Yaaay you won");
                    disableButtons();
                } else {
                    logic.decreaseMaxMoves();
                    logic.enemyTurn();
                    setAllColours();
                    logic.toggle_player_turn();

                    if (logic.getMaxMoves() <= 0 || logic.getWinner() == 2) {
                        showsWinner.setText("Ohno you lost");
                        disableButtons();
                    } else {
                        showsWinner.setText("Moves remaining: " + logic.getMaxMoves());
                    }
                }
            } else {
                // Multiplayer Logic
                if (winner != 0) {
                    setWinnerText(winner);
                    logic.getGameSession().setFinished(true);
                }

                controller.onMoveCommitted(logic.getGameSession());
                disableButtons();
            }
        }
    }

    private void setWinnerText(int winner) {
        if (winner == 1 || winner == 2) {
            if (winner == 1) {
                showsWinner.setText(WIN_TEXT + "You!");
            } else {
                showsWinner.setText(WIN_TEXT + getOpponentName());
            }
        } else if (winner == 3) {
            showsWinner.setText(DRAW_TEXT);
        }
    }

    public void disableButtons() {
        for (Button[] row: buttonArray) {
            for (Button button: row) {
                button.setDisable(true);
            }
        }
    }

    private void resetButtons() {
        for (Button[] row : buttonArray) {
            for (Button button : row) {
                button.setDisable(false);
            }
        }
    }

    private void colorValidButton(int row, int col) {
        if (row - 1 >= 0) {
            buttonArray[row - 1][col].setStyle(COLOR_BTN_ENABLE);
        }
    }

    private void setAllColours() {
        for (int row = 0; row < buttonArray.length; row++) {
            for (int col = 0; col < buttonArray[0].length; col++) {
                CellStatus status = logic.getBoard().getCellStatus(row, col);
                if (status == CellStatus.PLAYER1) {
                    buttonArray[row][col].setStyle(playerOneColor);
                } else if (status == CellStatus.PLAYER2) {
                    buttonArray[row][col].setStyle(playerTwoColor);
                } else {
                    if (row == buttonArray.length - 1) {
                        buttonArray[row][col].setStyle(COLOR_BTN_ENABLE);
                    } else if (row + 1 < buttonArray.length && logic.getBoard().getCellStatus(row + 1, col) != CellStatus.EMPTY) {
                        buttonArray[row][col].setStyle(COLOR_BTN_ENABLE);
                    } else {
                        buttonArray[row][col].setStyle(COLOR_BTN_DISABLE);
                    }
                }
            }
        }
    }

    public void startLevel(int level) {
        resetButtons();
        logic.setPlayer_one_turn(true);
        showsWinner.setText(ONGOING_GAME_TEXT);
        logic.setLevelMode(true);

        SingleLevels lvl = new SingleLevels(level);

        logic.LevelLaden(lvl);
        controller.setOpponentText(getOpponentName());
        controller.toggleTurnButton(logic.getGameSession());
        showsWinner.setText("Moves remaining: " + logic.getMaxMoves());

        setAllColours();
    }

    public void setPlayerColors(String playerColor, String opponentColor) {
        playerOneColor = "-fx-background-color: " + playerColor + ";";
        playerTwoColor = "-fx-background-color: " + opponentColor + ";";
        setAllColours();
    }

    public void loadGameSession(GameSession gameSession) {
        logic.setGameSession(gameSession);
        if (gameSession.isFinished()) {
            setWinnerText(logic.getWinner());
            disableButtons();
            setAllColours();
            return;
        }

        showsWinner.setText("Now playing against: " + gameSession.getOpponent() + " (Session ID: " + gameSession.getSessionNumber() + ")");

        if (logic.isPlayer_one_turn()) {
            resetButtons();
        } else {
            disableButtons();
        }

        setAllColours();
    }

    public String getOpponentName() {
        return logic.getGameSession().getOpponent();
    }

    public int getGameSessionNumber() {
        return logic.getGameSession().getSessionNumber();
    }

    public boolean getLevelMode() {
        return logic.getLevelMode();
    }
}
