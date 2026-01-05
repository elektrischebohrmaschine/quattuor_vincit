package fhtw.quattuor.common.model;


public class GameSession {
    private final int row = 6;
    private final int col = 7;

    private Board board;
    private String opponent;
    private int sessionNumber;
    private boolean isFinished;
    private boolean yourTurn;
    private int moveCount;

    public GameSession() {
        this.board = new Board(row, col);
    }


    public GameSession(String opponent, int sessionNumber, boolean yourTurn) {
        this.board = new Board(row, col);
        this.opponent = opponent;
        this.sessionNumber = sessionNumber;
        this.yourTurn = yourTurn;
        this.isFinished = false;
        this.moveCount = 0;
    }

    public Board getBoard() {
        return board;
    }

    public String getOpponent() {
        return opponent;
    }

    public int getSessionNumber() {
        return sessionNumber;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public void toggleTurn() {
        yourTurn = !yourTurn;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setSessionNumber(int sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public void setYourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }
}
