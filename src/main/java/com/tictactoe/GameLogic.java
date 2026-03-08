package com.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Core game logic for Tic Tac Toe, completely separated from UI.
 * Implements Minimax algorithm with alpha-beta pruning for the unbeatable AI.
 *
 * @author Prashant
 */
public class GameLogic {

    /** All possible winning line combinations (row, col, diagonal indices). */
    private static final int[][] WIN_LINES = {
            { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, // rows
            { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, // columns
            { 0, 4, 8 }, { 2, 4, 6 } // diagonals
    };

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    public enum CellState {
        EMPTY, X, O
    }

    public enum GameResult {
        X_WINS, O_WINS, DRAW, IN_PROGRESS
    }

    private final CellState[] board = new CellState[9];
    private final Random random = new Random();
    private boolean playerTurn = true;
    private int playerWins = 0;
    private int aiWins = 0;
    private int draws = 0;
    private Difficulty difficulty = Difficulty.EASY;
    private int[] winningLine = null;

    public GameLogic() {
        resetBoard();
    }

    // ─── Public API ───────────────────────────────────────────

    /**
     * Attempts to place the player's mark at the given position.
     *
     * @param index board position (0-8)
     * @return true if the move was valid and placed
     */
    public boolean makePlayerMove(int index) {
        if (index < 0 || index > 8 || board[index] != CellState.EMPTY || !playerTurn) {
            return false;
        }
        board[index] = CellState.X;
        playerTurn = false;
        return true;
    }

    /**
     * Computes and places the AI's move based on the current difficulty.
     *
     * @return the index where the AI placed its mark, or -1 if no move possible
     */
    public int makeAIMove() {
        if (playerTurn || getGameResult() != GameResult.IN_PROGRESS) {
            return -1;
        }

        int move = switch (difficulty) {
            case EASY -> easyMove();
            case MEDIUM -> mediumMove();
            case HARD -> hardMove();
        };

        if (move != -1) {
            board[move] = CellState.O;
            playerTurn = true;
        }
        return move;
    }

    /**
     * Checks the current game state and returns the result.
     * Also updates win/loss/draw counters on game end.
     *
     * @return the current game result
     */
    public GameResult getGameResult() {
        // Check all winning lines
        for (int[] line : WIN_LINES) {
            if (board[line[0]] != CellState.EMPTY
                    && board[line[0]] == board[line[1]]
                    && board[line[1]] == board[line[2]]) {
                winningLine = line;
                return board[line[0]] == CellState.X ? GameResult.X_WINS : GameResult.O_WINS;
            }
        }

        // Check for draw (no empty cells)
        for (CellState cell : board) {
            if (cell == CellState.EMPTY)
                return GameResult.IN_PROGRESS;
        }
        return GameResult.DRAW;
    }

    /** Updates score counters based on the game result. */
    public void recordResult(GameResult result) {
        switch (result) {
            case X_WINS -> playerWins++;
            case O_WINS -> aiWins++;
            case DRAW -> draws++;
            default -> {
            }
        }
    }

    /** Resets the board for a new game. Scores are preserved. */
    public void resetBoard() {
        for (int i = 0; i < 9; i++) {
            board[i] = CellState.EMPTY;
        }
        playerTurn = true;
        winningLine = null;
    }

    /** Resets everything including scores. */
    public void resetAll() {
        resetBoard();
        playerWins = 0;
        aiWins = 0;
        draws = 0;
    }

    // ─── Getters / Setters ────────────────────────────────────

    public CellState getCell(int index) {
        return board[index];
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public int getPlayerWins() {
        return playerWins;
    }

    public int getAIWins() {
        return aiWins;
    }

    public int getDraws() {
        return draws;
    }

    public int[] getWinningLine() {
        return winningLine;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty d) {
        this.difficulty = d;
    }

    // ─── AI Strategies ────────────────────────────────────────

    /** Easy: tries to win or block, otherwise random. */
    private int easyMove() {
        int move = findWinningMove(CellState.O);
        if (move != -1)
            return move;

        move = findWinningMove(CellState.X);
        if (move != -1)
            return move;

        return randomMove();
    }

    /** Medium: 60% chance of playing optimal (minimax), 40% random. */
    private int mediumMove() {
        if (random.nextDouble() < 0.6) {
            return hardMove();
        }
        return easyMove();
    }

    /** Hard: full Minimax with alpha-beta pruning — unbeatable. */
    private int hardMove() {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < 9; i++) {
            if (board[i] == CellState.EMPTY) {
                board[i] = CellState.O;
                int score = minimax(false, Integer.MIN_VALUE, Integer.MAX_VALUE);
                board[i] = CellState.EMPTY;
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }
        return bestMove;
    }

    /**
     * Minimax algorithm with alpha-beta pruning.
     *
     * @param isMaximizing true if it's the AI's turn (maximizing)
     * @param alpha        best score the maximizer can guarantee
     * @param beta         best score the minimizer can guarantee
     * @return the evaluated score of the board state
     */
    private int minimax(boolean isMaximizing, int alpha, int beta) {
        GameResult result = evaluateBoard();
        if (result != GameResult.IN_PROGRESS) {
            return switch (result) {
                case O_WINS -> 10;
                case X_WINS -> -10;
                case DRAW -> 0;
                default -> 0;
            };
        }

        if (isMaximizing) {
            int maxScore = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == CellState.EMPTY) {
                    board[i] = CellState.O;
                    int score = minimax(false, alpha, beta);
                    board[i] = CellState.EMPTY;
                    maxScore = Math.max(score, maxScore);
                    alpha = Math.max(alpha, score);
                    if (beta <= alpha)
                        break; // prune
                }
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == CellState.EMPTY) {
                    board[i] = CellState.X;
                    int score = minimax(true, alpha, beta);
                    board[i] = CellState.EMPTY;
                    minScore = Math.min(score, minScore);
                    beta = Math.min(beta, score);
                    if (beta <= alpha)
                        break; // prune
                }
            }
            return minScore;
        }
    }

    /** Evaluates the board without considering moves remaining (for minimax). */
    private GameResult evaluateBoard() {
        for (int[] line : WIN_LINES) {
            if (board[line[0]] != CellState.EMPTY
                    && board[line[0]] == board[line[1]]
                    && board[line[1]] == board[line[2]]) {
                return board[line[0]] == CellState.X ? GameResult.X_WINS : GameResult.O_WINS;
            }
        }
        for (CellState cell : board) {
            if (cell == CellState.EMPTY)
                return GameResult.IN_PROGRESS;
        }
        return GameResult.DRAW;
    }

    /** Finds a move that immediately wins for the given mark. */
    private int findWinningMove(CellState mark) {
        for (int[] line : WIN_LINES) {
            CellState a = board[line[0]], b = board[line[1]], c = board[line[2]];
            if (a == mark && b == mark && c == CellState.EMPTY)
                return line[2];
            if (a == mark && c == mark && b == CellState.EMPTY)
                return line[1];
            if (b == mark && c == mark && a == CellState.EMPTY)
                return line[0];
        }
        return -1;
    }

    /** Picks a random empty cell. */
    private int randomMove() {
        List<Integer> empty = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (board[i] == CellState.EMPTY)
                empty.add(i);
        }
        return empty.isEmpty() ? -1 : empty.get(random.nextInt(empty.size()));
    }
}
