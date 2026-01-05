package fhtw.quattuor.common.logic;

import fhtw.quattuor.common.model.*;

public class SingleLevels {

    Board board;
    int maxMoves;


    public SingleLevels(Board board, int maxMoves) {
        this.board = board;
        this.maxMoves = maxMoves;
    }

    public static SingleLevels Level1(Board board) {
        board.setCellStatus(5,6,CellStatus.PLAYER1);
        board.setCellStatus(5,4,CellStatus.PLAYER1);
        board.setCellStatus(5,3,CellStatus.PLAYER1);
        board.setCellStatus(5,2,CellStatus.PLAYER1);

        board.setCellStatus(5,5,CellStatus.PLAYER2);
        board.setCellStatus(4,4,CellStatus.PLAYER2);
        board.setCellStatus(4,3,CellStatus.PLAYER2);
        board.setCellStatus(3,3,CellStatus.PLAYER2);

        return new SingleLevels(board,1);
    }

    public static SingleLevels Level2(Board board) {
        board.setCellStatus(5,5,CellStatus.PLAYER2);
        board.setCellStatus(4,5,CellStatus.PLAYER2);
        board.setCellStatus(3,5,CellStatus.PLAYER2);
        board.setCellStatus(5,4,CellStatus.PLAYER2);
        board.setCellStatus(4,4,CellStatus.PLAYER2);

        board.setCellStatus(5,2,CellStatus.PLAYER1);
        board.setCellStatus(5,3,CellStatus.PLAYER1);
        board.setCellStatus(2,5,CellStatus.PLAYER1);
        board.setCellStatus(4,3,CellStatus.PLAYER1);

        return new SingleLevels(board,1);
    }
    public static SingleLevels Level3(Board board) {
        board.setCellStatus(5,4,CellStatus.PLAYER1);
        board.setCellStatus(4,1,CellStatus.PLAYER1);
        board.setCellStatus(4,3,CellStatus.PLAYER1);
        board.setCellStatus(4,4,CellStatus.PLAYER1);
        board.setCellStatus(4,5,CellStatus.PLAYER1);
        board.setCellStatus(3,1,CellStatus.PLAYER1);
        board.setCellStatus(2,1,CellStatus.PLAYER1);
        board.setCellStatus(5,2,CellStatus.PLAYER1);

        board.setCellStatus(5,1,CellStatus.PLAYER2);
        board.setCellStatus(5,3,CellStatus.PLAYER2);
        board.setCellStatus(3,3,CellStatus.PLAYER2);
        board.setCellStatus(5,5,CellStatus.PLAYER2);
        board.setCellStatus(3,4,CellStatus.PLAYER2);
        board.setCellStatus(2,3,CellStatus.PLAYER2);
        board.setCellStatus(1,1,CellStatus.PLAYER2);
        board.setCellStatus(4,2,CellStatus.PLAYER2);


        return new SingleLevels(board,1);
    }
}
