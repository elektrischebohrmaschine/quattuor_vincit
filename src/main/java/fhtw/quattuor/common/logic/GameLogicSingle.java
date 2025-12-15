package fhtw.quattuor.common.logic;

import fhtw.quattuor.common.model.Board;
import fhtw.quattuor.common.model.CellStatus;
import javafx.scene.control.Cell;

public class GameLogicSingle {
    private boolean player_one_turn = true;
    private Board board;

    public GameLogicSingle(int size_x, int size_y) {
        this.board = new Board(size_x, size_y);
    }


    public boolean valid_move(int x, int y) {
        boolean valid = false;

        if (board.getCellStatus(x, y) != CellStatus.EMPTY) {
            return valid;
        }

        if (x== board.getWidth()-1 ){
            valid= true;
        } else if (board.getCellStatus(x+1, y) == CellStatus.EMPTY){
                return false;
            }


            // Set Cell depending on current player
            if (player_one_turn) {
                board.setCellStatus(x, y, CellStatus.PLAYER1);
            } else {
                board.setCellStatus(x, y, CellStatus.PLAYER2);
            }
            //checkWinCondition(board);
            valid= true;
            toggle_player_turn();

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

//    public boolean checkWinCondition(Board board) {
//        boolean win = false;
//        int streak = 0;
//
//        for (int row = 0; row < board.getHeight(); row++) {
//            for (int col = 0; col < board.getWidth(); col++) {
//
//                CellStatus status = board.getCellStatus(row, col);
//
//                if (streak == 4) {
//                    win = true;
//                    return win;
//                }
//            }
//        }
//        return win;
//    }


}
