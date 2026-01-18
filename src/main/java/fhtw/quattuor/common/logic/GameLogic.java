package fhtw.quattuor.common.logic;

import fhtw.quattuor.common.model.Board;
import fhtw.quattuor.common.model.CellStatus;
import fhtw.quattuor.common.model.GameSession;

public class GameLogic {
    private boolean levelMode = false;
    private SingleLevels singleLevels;
    private GameSession gameSession;

    public GameLogic(int row, int col) {
        this.gameSession = new GameSession(row, col);
    }

    public GameLogic(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public boolean valid_move(int x, int y) {
        boolean valid = false;

        if (gameSession.getBoard().getCellStatus(x, y) != CellStatus.EMPTY) {
            return valid;
        }

        if (x == gameSession.getBoard().boardHeight() - 1) {
        } else if (gameSession.getBoard().getCellStatus(x + 1, y) == CellStatus.EMPTY) {
            return valid;
        }

        if (gameSession.getYourTurn()) {
            gameSession.getBoard().setCellStatus(x, y, CellStatus.PLAYER1);
        } else {
            gameSession.getBoard().setCellStatus(x, y, CellStatus.PLAYER2);
        }

        valid = true;
        gameSession.increaseMoveCount();

        // DEBUG - Remove later :)
        //gameSession.getBoard().print_board();

        return valid;
    }

    public void toggle_player_turn() {
        gameSession.toggleTurn();
    }

    public boolean isPlayer_one_turn() {
        return gameSession.getYourTurn();
    }

    public void setPlayer_one_turn(boolean player_one_turn) {
        this.gameSession.setYourTurn(player_one_turn);
    }

    public int checkWinCondition(Board board) {
        int player_one = 1;
        int player_two = 2;
        int draw = 3;
        int continues = 0;


        // vertikal
        for (int col = 0; col < board.boardWidth(); col++) {
            int streak_p1 = 0;
            int streak_p2 = 0;
            for (int row = 0; row < board.boardHeight(); row++) {

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
                } else if (streak_p2 == 4) {
                    return player_two;
                }
            }
        }

        // horizontal
        for (int row = 0; row < board.boardHeight(); row++) {
            int streak_p1 = 0;
            int streak_p2 = 0;
            for (int col = 0; col < board.boardWidth(); col++) {
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
                } else if (streak_p2 == 4) {
                    return player_two;
                }
            }
        }

        // diagonal (links-> rechts)
        for (int row = 0; row < board.boardHeight() - 3; row++) {
            for (int col = 0; col < board.boardWidth() - 3; col++) {

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

                if (streak_p1 == 4) {
                    return player_one;
                } else if (streak_p2 == 4) {
                    return player_two;
                }
            }
        }

        // diagonal (rechts -> links)
        for (int row = 3; row < board.boardHeight(); row++) {
            for (int col = 0; col < board.boardWidth() - 3; col++) {

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

                if (streak_p1 == 4) {
                    return player_one;
                } else if (streak_p2 == 4) {
                    return player_two;
                }
            }
        }

        //draw
        for (int row = 0; row < board.boardHeight(); ) {
            for (int col = 0; col < board.boardWidth(); col++) {
                if (board.getCellStatus(row, col) == CellStatus.EMPTY) {
                    return continues;
                }
            }
            return draw;
        }
        return continues;
    }

    public int getWinner() {
        int result = checkWinCondition(gameSession.getBoard());
        if (result != 0) {
            gameSession.setFinished(true);
        }
        return result;
    }

    public void LevelLaden(SingleLevels level) {
        this.gameSession.setBoard(level.getBoard());
        this.gameSession.setOpponent("Computer " + level.getLevelSelected());
        this.singleLevels = level;
    }

    public void decreaseMaxMoves() {
        singleLevels.setMaxMoves(singleLevels.getMaxMoves() - 1);
    }

    public int getMaxMoves() {
        return singleLevels.getMaxMoves();
    }

    public void enemyTurn() {
        this.gameSession.setBoard(singleLevels.enemyTurn(gameSession.getBoard()));
    }

    public Board getBoard() {
        return this.gameSession.getBoard();
    }

    public boolean getLevelMode() {
        return levelMode;
    }

    public void setLevelMode(boolean levelMode) {
        this.levelMode = levelMode;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
        levelMode = false;
    }

    public GameSession getGameSession() {
        return gameSession;
    }
}
