import java.util.*;

// 0 we make a move
// 1 opponent reacts
// 2 we rate the outcome

public class Cheese {
    // Board constants
    static final int SIZE = 11;

    static final int EMPTY = 0;
    static final int BLACK = 1;
    static final int WHITE = -1;

    // Heuristic function constants
    private static final int FIVE_IN_A_ROW = 6;
    private static final int LIVE_FOUR = 5;
    private static final int DEAD_FOUR = 4;
    private static final int LIVE_THREE = 3;
    private static final int DEAD_THREE = 2;
    private static final int LIVE_TWO = 1;
    private static final int DEAD_TWO = 0;

    private static final int NONE = 0;
    private static final int SAME = 1;
    private static final int DIFF = 2;

    // Solver parameters
    private static int range = 1;
    private static int maxDepth = 3;

    // Categories and corresponding masks
    private static final int checks[][] = {
            // Five-in-a-Row
            { FIVE_IN_A_ROW, SAME, SAME, SAME, SAME, SAME },

            // maybe its messed up because of the "none" padding at the end
            // meaning that if it doesn't see these exact scenarios with the
            // empty spaces at the end it'll just default to the minor
            // diagnoal cuz thats first?

            // the original heuristic function with the pads
            // are in the heuristicRev.txt file

            // Live four
            { LIVE_FOUR, SAME, SAME, SAME, SAME, NONE },
            { LIVE_FOUR, NONE, SAME, SAME, SAME, SAME },
            { LIVE_FOUR, SAME, NONE, SAME, SAME, SAME },
            { LIVE_FOUR, SAME, SAME, SAME, NONE, SAME },
            { LIVE_FOUR, SAME, SAME, NONE, SAME, SAME },

            // Dead four
            { DEAD_FOUR, NONE, NONE, SAME, SAME, SAME, DIFF },
            { DEAD_FOUR, DIFF, SAME, SAME, SAME, NONE, NONE },
            { DEAD_FOUR, NONE, SAME, NONE, SAME, SAME, DIFF },
            { DEAD_FOUR, DIFF, SAME, SAME, NONE, SAME, NONE },
            { DEAD_FOUR, NONE, SAME, SAME, NONE, SAME, DIFF },
            { DEAD_FOUR, DIFF, SAME, NONE, SAME, SAME, NONE },

            // Live three
            { LIVE_THREE, NONE, SAME, NONE, SAME, SAME, NONE },
            { LIVE_THREE, NONE, SAME, SAME, NONE, SAME, NONE },
            { LIVE_THREE, NONE, SAME, SAME, SAME, NONE },

            // Dead three
            { DEAD_THREE, SAME, NONE, NONE, NONE, SAME },
            { DEAD_THREE, NONE, SAME, NONE, SAME, NONE },
            { DEAD_THREE, SAME, NONE, NONE, SAME, SAME },
            { DEAD_THREE, SAME, SAME, NONE, NONE, SAME },
            { DEAD_THREE, SAME, NONE, SAME, NONE, SAME },

            // Live two
            { LIVE_TWO, NONE, SAME, NONE, NONE, SAME, NONE },
            { LIVE_TWO, NONE, SAME, SAME, NONE, NONE },
            { LIVE_TWO, NONE, NONE, SAME, SAME, NONE },

            // Dead two
            { DEAD_TWO, NONE, SAME, NONE, NONE, SAME, DIFF },
            { DEAD_TWO, DIFF, SAME, NONE, NONE, SAME, NONE },
            { DEAD_TWO, NONE, SAME, SAME, DIFF },
            { DEAD_TWO, DIFF, SAME, SAME, NONE },
            { DEAD_TWO, NONE, SAME, NONE, SAME, DIFF },
            { DEAD_TWO, DIFF, SAME, NONE, SAME, NONE },
    };

    public static int[][] copyBoard(int[][] board) {
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                copy[i][j] = board[i][j];
        return copy;
    }

    public static void setRange(int r) {
        range = r;
    }

    public static void setMaxDepth(int d) {
        maxDepth = d;
    }

    private static int getIndex(int r, int c) {
        return r * SIZE + c;
    }

    private static int getIndexRow(int index) {
        return index / SIZE;
    }

    private static int getIndexColumn(int index) {
        return index % SIZE;
    }

    private static boolean inBound(int i) {
        return i >= 0 && i < SIZE;
    }

    private static boolean inRange(int[][] board, int r, int c) {
        for (int i = -range; i <= range; i++)
            for (int j = -range; j <= range; j++)
                if (inBound(r + i) && inBound(c + j) && board[r + i][c + j] != EMPTY)
                    return true;
        return false;
    }

    private static int scoreTile(int[][] board, int r, int c, int piece) {
        if (inBound(r) && inBound(c)) {
            int tile = board[r][c];
            if (tile == EMPTY)
                return NONE;
            if (tile == piece)
                return SAME;
        }
        return DIFF;
    }

    private static int scoreBoard(int[][] board, int piece) {
        int[] counts = { 0, 0, 0, 0, 0, 0, 0 };

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                for (int[] check : checks) {
                    int type = check[0];
                    int[] mask = check;
                    int length = mask.length - 1;
                    int count;

                    // Score along minor diagonal
                    count = 0;
                    for (int k = 0; k < length; k++)
                        if (scoreTile(board, i + k, j + k, piece) == mask[k + 1])
                            count++;
                    if (count == length)
                        counts[type]++;

                    // Score along major diagonal
                    count = 0;
                    for (int k = 0; k < length; k++)
                        if (scoreTile(board, i + k, j + length - 1 - k, piece) == mask[k + 1])
                            count++;
                    if (count == length)
                        counts[type]++;

                    // Score along row
                    count = 0;
                    for (int k = 0; k < length; k++)
                        if (scoreTile(board, i, j + k, piece) == mask[k + 1])
                            count++;
                    if (count == length)
                        counts[type]++;

                    // Score along minor column
                    count = 0;
                    for (int k = 0; k < length; k++)
                        if (scoreTile(board, i + k, j, piece) == mask[k + 1])
                            count++;
                    if (count == length)
                        counts[type]++;
                }
            }
        }

        // Scoring based on categorization
        int result = 0;

        // Todo: improve heuristic function
        result += counts[FIVE_IN_A_ROW] * 1000000007;
        result += counts[LIVE_FOUR] * 307;
        result += counts[DEAD_FOUR] * 17;
        result += counts[LIVE_THREE] * 97;
        result += counts[DEAD_THREE] * 3;
        result += counts[LIVE_TWO] * 7;
        result += counts[DEAD_TWO] * 2;

        if (piece == WHITE)
            result *= -1;
        return result;
    }

    public static int getScore(int[][] board, int depth, int piece, int alpha, int beta) {
        if (depth >= maxDepth)
            return scoreBoard(board, BLACK) + scoreBoard(board, WHITE);

        // Prepare next minimaxed state
        int result = Integer.MAX_VALUE;
        int nextDepth = depth + 1;
        int nextPiece = BLACK;
        if (piece == BLACK) {
            result = Integer.MIN_VALUE;
            nextPiece = WHITE;
        }

        // Alpha-beta pruning of children
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY && inRange(board, i, j)) {
                    int[][] copy = copyBoard(board);
                    copy[i][j] = piece;
                    int score = getScore(copy, nextDepth, nextPiece, alpha, beta);
                    if (piece == BLACK) {
                        result = Math.max(result, score);
                        alpha = Math.max(alpha, score);
                    } else {
                        result = Math.min(result, score);
                        beta = Math.min(beta, score);
                    }
                    if (beta <= alpha)
                        return result;
                }
            }
        }

        return result;
    }

    public static int[] strat(int[][] board, SavedData memory) {
        int maxScore = Integer.MIN_VALUE;
        int[] result = new int[] { 5, 5 };

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY && inRange(board, i, j)) {
                    int[][] copy = copyBoard(board);
                    copy[i][j] = BLACK;
                    int score = getScore(copy, 0, WHITE, maxScore, Integer.MAX_VALUE);
                    if (score > maxScore) {
                        maxScore = score;
                        result[0] = i;
                        result[1] = j;
                    }
                }
            }
        }

        return result;
    }
}