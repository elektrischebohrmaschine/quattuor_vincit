package fhtw.quattuor.common.model;

public class Board {
    private CellStatus[][] board;

    public Board(int size_x, int size_y) {
        this.board = new CellStatus[size_x][size_y];
        for (int i = 0; i < size_x; i++) {
            for (int j = 0; j < size_y; j++) {
                board[i][j] = CellStatus.EMPTY;
            }
        }
    }

    public void print_board() {
        for (CellStatus[] row : board) {
            for (CellStatus status : row) {
                if (status == CellStatus.EMPTY) {
                    System.out.print("_");
                } else if (status == CellStatus.PLAYER1) {
                    System.out.print("1");
                } else if (status == CellStatus.PLAYER2) {
                    System.out.print("2");
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public CellStatus getCellStatus(int x, int y) {
        return board[x][y];
    }

    public void setCellStatus(int x, int y, CellStatus status) {
        board[x][y] = status;
    }
}
