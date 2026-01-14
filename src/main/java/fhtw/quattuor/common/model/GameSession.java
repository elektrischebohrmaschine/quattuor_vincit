package fhtw.quattuor.common.model;


public class GameSession {
    private Board board;
    private String opponent;
    private int sessionNumber;
    private boolean isFinished;
    private boolean yourTurn;
    private int moveCount;

    public GameSession() {
    }

    public GameSession(int row, int col) {
        this.board = new Board(row, col);
    }

    public GameSession(String opponent, int sessionNumber, boolean yourTurn, int row, int col) {
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

    public boolean getYourTurn() {
        return yourTurn;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public void increaseMoveCount() {
        this.moveCount++;
    }

    public Board flippedBoard() {
        return board.flippedBoard();
    }

    @Override
    public String toString() {
        String returnString = "";
        if (isFinished) {
            returnString =
                    "Game finished!\n" +
                    "Opponent: " + opponent + "\n" +
                    "Session Nr: " + getSessionNumber() + "\n";
        } else {
            String turn = isYourTurn() ? "Yes" : "No";
            returnString = "Opponent: " + opponent + "\n" +
                    "Moves Made: " + getMoveCount() + "\n" +
                    "Session Nr: " + getSessionNumber() + "\n" +
                    "Your Turn? " + turn;
        }
        return returnString;
    }
}
