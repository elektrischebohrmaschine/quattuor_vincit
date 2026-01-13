package fhtw.quattuor.common.model;

public class Board {
    private CellStatus[][] board;

    public Board() {}

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

    public CellStatus[][] getBoard() {
        return board;
    }

    public void setBoard(CellStatus[][] board) {
        this.board = board;
    }

    public int boardHeight(){
        return board.length;
    }

    public int boardWidth(){
        return board[0].length;
    }

    public Board flippedBoard() {
        int height = boardHeight();
        int width = boardWidth();

        Board flipped = new Board(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                CellStatus current = getCellStatus(i, j);

                if (current == CellStatus.PLAYER1) {
                    flipped.setCellStatus(i, j, CellStatus.PLAYER2);
                } else if (current == CellStatus.PLAYER2) {
                    flipped.setCellStatus(i, j, CellStatus.PLAYER1);
                } else {
                    flipped.setCellStatus(i, j, CellStatus.EMPTY);
                }
            }
        }

        return flipped;
    }
}
