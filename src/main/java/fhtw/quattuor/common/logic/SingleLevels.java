package fhtw.quattuor.common.logic;

import fhtw.quattuor.common.model.*;

public class SingleLevels {

    private Board board;
    private int maxMoves;
    private int level_selected;

    public SingleLevels(int level) {
        level_selected = level;
        switch (level) {
            case 1:
                Level1();
                break;
            case 2:
                Level2();
                break;
            case 3:
                Level3();
                break;
            default:
                return;
        }
    }

    private void Level1() {
        board = new Board(6, 7);
        board.setCellStatus(5, 6, CellStatus.PLAYER1);
        board.setCellStatus(5, 4, CellStatus.PLAYER1);
        board.setCellStatus(5, 3, CellStatus.PLAYER1);
        board.setCellStatus(5, 2, CellStatus.PLAYER1);

        board.setCellStatus(5, 5, CellStatus.PLAYER2);
        board.setCellStatus(4, 4, CellStatus.PLAYER2);
        board.setCellStatus(4, 3, CellStatus.PLAYER2);
        board.setCellStatus(3, 3, CellStatus.PLAYER2);

        maxMoves = 1;
    }

    private void Level2() {
        board = new Board(6, 7);
        board.setCellStatus(5, 5, CellStatus.PLAYER2);
        board.setCellStatus(4, 5, CellStatus.PLAYER2);
        board.setCellStatus(3, 5, CellStatus.PLAYER2);
        board.setCellStatus(5, 4, CellStatus.PLAYER2);
        board.setCellStatus(4, 4, CellStatus.PLAYER2);

        board.setCellStatus(5, 2, CellStatus.PLAYER1);
        board.setCellStatus(5, 3, CellStatus.PLAYER1);
        board.setCellStatus(2, 5, CellStatus.PLAYER1);
        board.setCellStatus(4, 3, CellStatus.PLAYER1);

        maxMoves = 1;
    }

    private void Level3() {
        board = new Board(6, 7);
        board.setCellStatus(5, 4, CellStatus.PLAYER1);
        board.setCellStatus(4, 1, CellStatus.PLAYER1);
        board.setCellStatus(4, 3, CellStatus.PLAYER1);
        board.setCellStatus(4, 4, CellStatus.PLAYER1);
        board.setCellStatus(4, 5, CellStatus.PLAYER1);
        board.setCellStatus(3, 1, CellStatus.PLAYER1);
        board.setCellStatus(2, 3, CellStatus.PLAYER2);
        board.setCellStatus(5, 2, CellStatus.PLAYER1);

        board.setCellStatus(5, 1, CellStatus.PLAYER2);
        board.setCellStatus(5, 3, CellStatus.PLAYER2);
        board.setCellStatus(3, 3, CellStatus.PLAYER2);
        board.setCellStatus(5, 5, CellStatus.PLAYER2);
        board.setCellStatus(3, 4, CellStatus.PLAYER2);
        board.setCellStatus(1, 1, CellStatus.PLAYER2);
        board.setCellStatus(4, 2, CellStatus.PLAYER2);
        board.setCellStatus(2, 1, CellStatus.PLAYER2);

        maxMoves = 2;
    }

    public Board enemyTurn(Board board) {
        switch (level_selected) {
            case 1:
                board.setCellStatus(5, 1, CellStatus.PLAYER2);
                break;
            case 2:
                board.setCellStatus(3, 4, CellStatus.PLAYER2);
                break;
            case 3:
                if (maxMoves == 1) {
                    if (board.getCellStatus(2, 4) == CellStatus.EMPTY) {
                        board.setCellStatus(2, 4, CellStatus.PLAYER2);
                    } else {
                        board.setCellStatus(5, 6, CellStatus.PLAYER2);
                    }
                } else {
                    board.setCellStatus(4, 6, CellStatus.PLAYER2);
                }
                break;
            default:
                break;
        }

        return board;
    }

    public Board getBoard() {
        return board;
    }

    public int getMaxMoves() {
        return maxMoves;
    }

    public void setMaxMoves(int maxMoves) {
        this.maxMoves = maxMoves;
    }
}
