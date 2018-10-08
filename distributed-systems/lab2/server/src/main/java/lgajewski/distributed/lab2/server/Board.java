package lgajewski.distributed.lab2.server;

import lgajewski.distributed.lab2.common.GameState;

import java.rmi.RemoteException;

public class Board {

    // dimensions
    public static final int ROWS = 3;
    public static final int COLS = 3;

    // board of cells
    private Cell[][] cells;
    private int currentRow, currentCol;

    private GameState gameState;


    public Board() {
        cells = new Cell[ROWS][COLS];
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col] = new Cell(row, col);
            }
        }

        init();
        gameState = GameState.PLAYING;
    }

    public void init() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].clear();
            }
        }
    }

    public boolean isDraw() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == Seed.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean hasWon(Seed theSeed) {
        return (cells[currentRow][0].content == theSeed
                && cells[currentRow][1].content == theSeed
                && cells[currentRow][2].content == theSeed
                || cells[0][currentCol].content == theSeed
                && cells[1][currentCol].content == theSeed
                && cells[2][currentCol].content == theSeed
                || currentRow == currentCol
                && cells[0][0].content == theSeed
                && cells[1][1].content == theSeed
                && cells[2][2].content == theSeed
                || currentRow + currentCol == 2
                && cells[0][2].content == theSeed
                && cells[1][1].content == theSeed
                && cells[2][0].content == theSeed);
    }

    public String paint() throws RemoteException {
        StringBuilder stringBuffer = new StringBuilder();
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                stringBuffer.append(cells[row][col].paint());
                if (col < COLS - 1) stringBuffer.append("|");
            }
            stringBuffer.append("\n");
            if (row < ROWS - 1) {
                stringBuffer.append("-----------\n");
            }
        }
        return stringBuffer.toString();
    }

    public boolean isValidMove(int row, int col) throws RemoteException {
        return row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                && cells[row][col].content == Seed.EMPTY;
    }

    public void setSeed(int row, int col, Seed theSeed) {
        cells[row][col].content = theSeed;
        currentRow = row;
        currentCol = col;

        updateGame(theSeed);
    }

    public void updateGame(Seed theSeed) {
        if (hasWon(theSeed)) {  // check for win
            gameState = (theSeed == Seed.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;
        } else if (isDraw()) {  // check for draw
            gameState = GameState.DRAW;
        }
    }

    public GameState getGameState() {
        return gameState;
    }
}