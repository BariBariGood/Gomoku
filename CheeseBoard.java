import java.util.*;

// 0 we make a move
// 1 opponent reacts
// 2 we make another move
// 3 we rate the outcome

class CheeseBoard {
    // Static variables
    private static int width = 11;
    private static int height = 11;
    private static int maxDepth = 3;

    // Board contants
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = -1;

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

    private static final int affects[] = {
            0, 1, 3, 11, 107,
            1000000009, 1000000009, 1000000009, 1000000009, 1000000009
    };

    private static final int checks[][] = {
            // Five-in-a-Row
            { FIVE_IN_A_ROW, SAME, SAME, SAME, SAME, SAME },

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

    private static boolean validRow(int row) {
        return row >= 0 && row < width;
    }

    private static boolean validCol(int col) {
        return col >= 0 && col < height;
    }

    private static int moveCompress(int row, int col, int weight) {
        return weight * width * height + row * width + col;
    }

    private static int moveRow(int move) {
        return (move % (width * height)) / width;
    }

    private static int moveCol(int move) {
        return move % width;
    }

    private static int moveWeight(int move) {
        return move / (width * height);
    }

    private static int scoreTile(int[][] board, int r, int c, int piece) {
        if (validRow(r) && validCol(c)) {
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

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
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

    private static void getWeights(int[][] board, int[][] weights,
            int piece) {
        { // Count along minor diagonals
            for (int i = -height; i < width; i++) {
                int rowEnd = Math.max(i, 0) - i,
                        colEnd = Math.max(i, 0);
                while (rowEnd < height && colEnd < width) {
                    int count = 0,
                            rowBeg = rowEnd - 1,
                            colBeg = colEnd - 1;
                    while (rowEnd < height && colEnd < width &&
                            board[rowEnd][colEnd] == piece) {
                        count++;
                        rowEnd++;
                        colEnd++;
                    }
                    if (count != 0) {
                        int affect = affects[count];
                        if (rowBeg >= 0 && colBeg >= 0 &&
                                board[rowBeg][colBeg] == EMPTY)
                            weights[rowBeg][colBeg] += affect;
                        if (rowEnd < height && colEnd < width &&
                                board[rowEnd][colEnd] == EMPTY)
                            weights[rowEnd][colEnd] += affect;
                    }
                    rowEnd++;
                    colEnd++;
                }
            }
        }
        { // Count along major diagonals
            for (int i = 0; i < width + height; i++) {
                int rowEnd = i - Math.min(i, width - 1),
                        colEnd = Math.min(i, width - 1);
                while (rowEnd < height && colEnd >= 0) {
                    int count = 0,
                            rowBeg = rowEnd - 1,
                            colBeg = colEnd + 1;
                    while (rowEnd < height && colEnd >= 0 &&
                            board[rowEnd][colEnd] == piece) {
                        count++;
                        rowEnd++;
                        colEnd--;
                    }
                    if (count != 0) {
                        int affect = affects[count];
                        if (rowBeg >= 0 && colBeg < width &&
                                board[rowBeg][colBeg] == EMPTY)
                            weights[rowBeg][colBeg] += affect;
                        if (rowEnd < height && colEnd >= 0 &&
                                board[rowEnd][colEnd] == EMPTY)
                            weights[rowEnd][colEnd] += affect;
                    }
                    rowEnd++;
                    colEnd--;
                }
            }
        }
        { // Count along rows
            for (int row = 0; row < height; row++) {
                int colEnd = 0;
                while (colEnd < width) {
                    int count = 0,
                            colBeg = colEnd - 1;
                    while (colEnd < width &&
                            board[row][colEnd] == piece) {
                        count++;
                        colEnd++;
                    }
                    if (count != 0) {
                        int affect = affects[count];
                        if (colBeg >= 0 &&
                                board[row][colBeg] == EMPTY)
                            weights[row][colBeg] += affect;
                        if (colEnd < width &&
                                board[row][colEnd] == EMPTY)
                            weights[row][colEnd] += affect;
                    }
                    colEnd++;
                }
            }
        }
        { // Count along columns
            for (int col = 0; col < width; col++) {
                int rowEnd = 0;
                while (rowEnd < height) {
                    int count = 0,
                            rowBeg = rowEnd - 1;
                    while (rowEnd < height &&
                            board[rowEnd][col] == piece) {
                        count++;
                        rowEnd++;
                    }
                    if (count != 0) {
                        int affect = affects[count];
                        if (rowBeg >= 0 &&
                                board[rowBeg][col] == EMPTY)
                            weights[rowBeg][col] += affect;
                        if (rowEnd < height &&
                                board[rowEnd][col] == EMPTY)
                            weights[rowEnd][col] += affect;
                    }
                    rowEnd++;
                }
            }
        }
    }

    private static void updateWeights(int[][] board, int[][] weights,
            int row, int col, int piece) {
        board[row][col] = piece;
        weights[row][col] = 0;
        { // Update along minor diagonal
            int countDsc = 0, countAsc = 0,
                    rowDsc = row, rowAsc = row,
                    colDsc = col, colAsc = col;
            while (--rowDsc >= 0 && --colDsc >= 0 &&
                    board[rowDsc][colDsc] == piece)
                countDsc++;
            while (++rowAsc < height && ++colAsc < width &&
                    board[rowAsc][colAsc] == piece)
                countAsc++;
            int affect = affects[countDsc + countAsc + 1];
            if (rowDsc >= 0 && colDsc >= 0 &&
                    board[rowDsc][colDsc] == EMPTY)
                weights[rowDsc][colDsc] += affect - affects[countDsc];
            if (rowAsc < height && colAsc < width &&
                    board[rowAsc][colAsc] == EMPTY)
                weights[rowAsc][colAsc] += affect - affects[countAsc];
        }
        { // Update along major diagonal
            int countDsc = 0, countAsc = 0,
                    rowDsc = row, rowAsc = row,
                    colDsc = col, colAsc = col;
            while (--rowDsc >= 0 && ++colDsc < width &&
                    board[rowDsc][colDsc] == piece)
                countDsc++;
            while (++rowAsc < height && --colAsc >= 0 &&
                    board[rowAsc][colAsc] == piece)
                countAsc++;
            int affect = affects[countDsc + countAsc + 1];
            if (rowDsc >= 0 && colDsc < width &&
                    board[rowDsc][colDsc] == EMPTY)
                weights[rowDsc][colDsc] += affect - affects[countDsc];
            if (rowAsc < height && colAsc >= 0 &&
                    board[rowAsc][colAsc] == EMPTY)
                weights[rowAsc][colAsc] += affect - affects[countAsc];
        }
        { // Update along row
            int countDsc = 0, countAsc = 0,
                    colDsc = col, colAsc = col;
            while (--colDsc >= 0 &&
                    board[row][colDsc] == piece)
                countDsc++;
            while (++colAsc < width &&
                    board[row][colAsc] == piece)
                countAsc++;
            int affect = affects[countDsc + countAsc + 1];
            if (colDsc >= 0 &&
                    board[row][colDsc] == EMPTY)
                weights[row][colDsc] += affect - affects[countDsc];
            if (colAsc < width &&
                    board[row][colAsc] == EMPTY)
                weights[row][colAsc] += affect - affects[countAsc];
        }
        { // Update along column
            int countDsc = 0, countAsc = 0,
                    rowDsc = row, rowAsc = row;
            while (--rowDsc >= 0 &&
                    board[rowDsc][col] == piece)
                countDsc++;
            while (++rowAsc < height &&
                    board[rowAsc][col] == piece)
                countAsc++;
            int affect = affects[countDsc + countAsc + 1];
            if (rowDsc >= 0 &&
                    board[rowDsc][col] == EMPTY)
                weights[rowDsc][col] += affect - affects[countDsc];
            if (rowAsc < width &&
                    board[rowAsc][col] == EMPTY)
                weights[rowAsc][col] += affect - affects[countAsc];
        }
    }

    private static int minimax(int[][] board, int[][] weights,
            int piece, int alpha, int beta,
            int depth) {
        if (depth == maxDepth)
            return scoreBoard(board, BLACK) + scoreBoard(board, WHITE);
        ArrayList<Integer> moves = new ArrayList<>();
        for (int i = 0; i < height; i++)
            for (int j = 0; j < height; j++)
                if (weights[i][j] != 0)
                    moves.add(moveCompress(i, j, weights[i][j]));
        Collections.sort(moves, Collections.reverseOrder());
        int max = piece == BLACK ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int nextPiece = -piece;
        int nextDepth = depth + 1;
        for (int move : moves) {
            int[][] nextBoard = new int[height][width];
            int[][] nextWeights = new int[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    nextBoard[i][j] = board[i][j];
                    nextWeights[i][j] = weights[i][j];
                }
            }
            int row = moveRow(move);
            int col = moveCol(move);
            updateWeights(nextBoard, nextWeights, row, col, piece);
            int score = minimax(nextBoard, nextWeights, nextPiece, alpha, beta, nextDepth);
            if (piece == BLACK) {
                max = Math.max(max, score);
                alpha = Math.max(alpha, score);
            } else {
                max = Math.min(max, score);
                beta = Math.min(beta, score);
            }
            if (beta <= alpha)
                break;
        }
        return max;
    }

    public static int[] strat(int[][] board, SavedData memory) {
        int max = Integer.MIN_VALUE;
        int[] result = new int[] { 5, 5 };
        int[][] weights = new int[height][width];
        getWeights(board, weights, BLACK);
        getWeights(board, weights, WHITE);
        ArrayList<Integer> moves = new ArrayList<>();
        for (int i = 0; i < height; i++)
            for (int j = 0; j < height; j++)
                if (weights[i][j] != 0)
                    moves.add(moveCompress(i, j, weights[i][j]));
        Collections.sort(moves, Collections.reverseOrder());
        for (int move : moves) {
            int[][] nextBoard = new int[height][width];
            int[][] nextWeights = new int[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    nextBoard[i][j] = board[i][j];
                    nextWeights[i][j] = weights[i][j];
                }
            }
            int row = moveRow(move);
            int col = moveCol(move);
            updateWeights(nextBoard, nextWeights, row, col, BLACK);
            int score = minimax(nextBoard, nextWeights, WHITE, max, Integer.MAX_VALUE, 0);
            if (score > max) {
                max = score;
                result[0] = row;
                result[1] = col;
            }
        }
        return result;
    }
}