public class Board {
    // 0 = empty
    // 1 = player 1 pieces
    // -1 = player 2 pieces
    private int[][] b;

    public Board(int rows, int columns) {
        b = new int[rows][columns];
    }

    public Board(int[][] board) {
        b = copy(board);
    }

    public Board(Board b) {
        this(b.b);
    }

    public static int[][] copy(int[][] b) {
        int[][] copy = null;
        if (b != null && b[0] != null) {
            copy = new int[b.length][b[0].length];
            for (int r = 0; r < b.length; r++)
                for (int c = 0; c < b[r].length; c++)
                    copy[r][c] = b[r][c];
        }
        return copy;
    }

    public int[][] getBoard() {
        return copy(b);
    }

    public int[][] getFlippedBoard() {
        int[][] board = copy(b);
        flip(board);
        return board;
    }

    public static void flip(int[][] b) {
        for (int r = 0; r < b.length; r++)
            for (int c = 0; c < b[r].length; c++)
                b[r][c] *= -1;
    }

    public static String pad(String s, int characters) {
        if (s.length() > characters)
            s = s.substring(0, characters);
        while (s.length() < characters)
            s = " " + s;
        return s;
    }

    public static String pad(int i, int characters) {
        return pad(i + "", characters);
    }

    public String toString() {
        String rv = "   ";
        for (int c = 0; c < b[0].length; c++)
            rv += pad(c, 3);
        rv += "\n";
        for (int r = 0; r < b.length; r++) {
            rv += pad(r, 3);
            for (int c = 0; c < b[r].length; c++) {
                rv += pad(xo(b[r][c]), 3);
            }
            rv += "\n";
        }
        return rv;
    }

    public int max(int a, int b) {
        if (a > b)
            return a;
        return b;
    }

    public String toString(int width, int rowSpace) {
        width = max(width, 3);
        String rv = pad("", width);
        for (int c = 0; c < b[0].length; c++)
            rv += pad(c, width);
        for (int i = 0; i < rowSpace; i++)
            rv += "\n       ";
        for (int c = 0; c < (width) * b[0].length; c++)
            rv += "_";
        rv += "\n";
        for (int r = 0; r < b.length; r++) {
            rv += pad("  " + r, width);
            for (int c = 0; c < b[r].length; c++) {
                rv += pad("| " + xo(b[r][c]), width);
            }
            for (int i = 0; i < rowSpace; i++)
                rv += "\n       ";
            for (int c = 0; c < (width) * b[0].length; c++)
                rv += "_";
            rv += "\n";
        }
        return rv;
    }

    public void printBoard() {
        // System.out.print("\033[H\033[2J");
        // System.out.flush();
        System.out.println(toString(5, 1));
    }

    public void printBoard(boolean clearScreen) {
        if (clearScreen) {
            // System.out.print("\033[H\033[2J");
            // System.out.flush();
        }
        System.out.println(toString(5, 1));
    }

    public static String xo(int player) {
        if (player == 0)
            return " ";
        if (player == 1)
            return "X";
        return "O";
    }

    public boolean place(int player, int row, int col) {
        if (b[row][col] == 0) {
            b[row][col] = player;
            return true;
        }
        return false;
    }

    public boolean place(int player, int[] coordinates) {
        if (coordinates == null)
            return false;
        return place(player, coordinates[0], coordinates[1]);
    }

}
