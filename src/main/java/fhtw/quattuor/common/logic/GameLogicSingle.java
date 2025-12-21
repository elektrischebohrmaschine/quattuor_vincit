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

        if (board.getCellStatus(x, y) != CellStatus.EMPTY) {
            return valid;
        }

        if (x== board.getHeight()-1 ){
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

    public int checkWinCondition(Board board) {
        int player_one = 1;
        int player_two = 2;
        int none= 0;


        // vertikal
        for (int col = 0; col < board.getWidth(); col++) {
            int streak_p1 = 0;
            int streak_p2 = 0;
            for (int row = 0; row < board.getHeight(); row++) {

                CellStatus status = board.getCellStatus(row, col);

                if (status == CellStatus.PLAYER1) {
                    streak_p1++;
                    streak_p2 = 0;
                } else if (status == CellStatus.PLAYER2) {
                    streak_p2++;
                    streak_p1 = 0;
                } else {
                    streak_p1 = 0;
                    streak_p2 = 0;
                }

                if (streak_p1 == 4) {
                    return player_one;
                }else if(streak_p2 == 4){
                    return player_two;
                }
            }
        }
        // horizontal
        for (int row = 0; row < board.getHeight(); row++) {
            int streak_p1 = 0;
            int streak_p2 = 0;
            for (int col= 0; col < board.getWidth(); col++) {
                CellStatus status = board.getCellStatus(row, col);

                if (status == CellStatus.PLAYER1) {
                    streak_p1++;
                    streak_p2 = 0;
                } else if (status == CellStatus.PLAYER2) {
                    streak_p2++;
                    streak_p1 = 0;
                } else {
                    streak_p1 = 0;
                    streak_p2 = 0;
                }

                if (streak_p1 == 4) {
                    return player_one;
                }else if(streak_p2 == 4){
                    return player_two;
                }
            }
        }
        // diagonal (links-> rechts)
        for (int row = 0; row < board.getHeight() - 3; row++) {
            for (int col = 0; col < board.getWidth() - 3; col++) {

                int streak_p1 = 0;
                int streak_p2 = 0;
                for (int i = 0; i < 4; i++) {
                    CellStatus status = board.getCellStatus(row + i, col + i);

                    if (status == CellStatus.PLAYER1) {
                        streak_p1++;
                    } else if (status == CellStatus.PLAYER2) {
                        streak_p2++;
                    }
                }

                if (streak_p1 == 4){
                    return player_one;
                }else if(streak_p2 == 4){
                    return player_two;
                }
            }
        }

        for (int row = 3; row < board.getHeight(); row++) {
            for (int col = 0; col < board.getWidth()-3; col++) {

                int streak_p1 = 0;
                int streak_p2 = 0;

                for (int i = 0; i < 4; i++) {
                    CellStatus status = board.getCellStatus(row - i, col + i);

                    if (status == CellStatus.PLAYER1) {
                        streak_p1++;
                    } else if (status == CellStatus.PLAYER2) {
                        streak_p2++;
                    }
                }

                if (streak_p1 == 4){
                    return player_one;
                }else if(streak_p2 == 4){
                    return player_two;
                }
            }
        }
        return none;
    }
    public int getWinner(){
        return checkWinCondition(board);
    }
}
