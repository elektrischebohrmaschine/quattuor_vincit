package fhtw.quattuor.common.logic;

import fhtw.quattuor.common.model.Board;
import fhtw.quattuor.common.model.CellStatus;

public class GameLogicSingle {
    private boolean player_one_turn = true;
    private Board board;

    public GameLogicSingle(int size_x, int size_y) {
        this.board = new Board(size_x, size_y);
    }

    public boolean valid_move(int x, int y) {
        boolean valid = false;
        if (board.getCellStatus(x, y) == CellStatus.EMPTY) {
            valid = true;

            // Set Cell depending on current player
            if (player_one_turn) {
                board.setCellStatus(x, y, CellStatus.PLAYER1);
            } else {
                board.setCellStatus(x, y, CellStatus.PLAYER2);
            }
            toggle_player_turn();
        }

        // DEBUG - Remove later :)
        board.print_board();
        return valid;
    }

    private void toggle_player_turn() {
        player_one_turn = !player_one_turn;
    }

    public boolean isPlayer_one_turn() {
        return player_one_turn;
    }
}
