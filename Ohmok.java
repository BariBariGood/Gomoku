//more board stuff

import java.util.Scanner;

public class Ohmok {
    private static Scanner scan = new Scanner(System.in);
    private static boolean slowMode = false;
    private static int p1 = 0, p2 = 0;
    private static int boardRows = 11, boardColumns = 11;

    public static void tournament() {
        String s = "";
        int numberOfPlayers = 0, numberOfRounds = 0;
        System.out.println("Strategies:\n" + Strategy.description());
        System.out.print("Number of rounds: ");
        numberOfRounds = Integer.parseInt(scan.nextLine());
        System.out.print("Number of players: ");
        numberOfPlayers = Integer.parseInt(scan.nextLine());
        String[] strategyNames = Strategy.getTeamNames();
        int[] strategies = new int[numberOfPlayers];
        int[][] results = new int[numberOfPlayers][numberOfPlayers];
        System.out.println("Enter strategy numbers");
        for (int i = 0; i < numberOfPlayers; i++) {
            System.out.print("Player " + i + ": ");
            strategies[i] = Integer.parseInt(scan.nextLine());
        }
        int winner = 0;
        for (int first = 0; first < numberOfPlayers; first++) {
            for (int second = 0; second < first; second++) {
                if (first != second) {
                    for (int i = 0; i < numberOfRounds; i++) {
                        winner = autoPlay(strategies[first], strategies[second]);
                        if (winner == 1)
                            results[first][second]++;
                        else
                            results[first][second]--;
                        winner = autoPlay(strategies[second], strategies[first]);
                        if (winner == 1)
                            results[second][first]++;
                        else
                            results[second][first]--;
                    }
                }
            }
        }
        String strategyName = "";
        System.out.print("\n" + Board.pad("", 13));
        for (int i = 0; i < numberOfPlayers; i++)
            System.out.print(Board.pad(i, 5));
        System.out.println("  Total Score");
        for (int first = 0; first < numberOfPlayers; first++) {
            strategyName = strategyNames[strategies[first]];
            System.out.print(Board.pad(first, 3) + Board.pad(strategyName, 10));
            for (int second = 0; second < numberOfPlayers; second++)
                System.out.print(Board.pad(results[first][second], 5));
            System.out.println("  " + (rowSum(results, first) - colSum(results, first)));
        }
    }

    public static int rowSum(int[][] a, int r) {
        int sum = 0;
        for (int c = 0; c < a[r].length; c++)
            sum += a[r][c];
        return sum;
    }

    public static int colSum(int[][] a, int c) {
        int sum = 0;
        for (int r = 0; r < a.length; r++)
            sum += a[r][c];
        return sum;
    }

    public static void start() {
        System.out.println("Type 'help' for commands.");
        String previousCommand = "";
        String s = "";
        String slow = "";
        while (!s.toLowerCase().equals("quit")) {
            if (slowMode)
                slow = "Slow";
            else
                slow = "Fast";
            System.out.println(slow + ", p1=" + p1 + ", p2=" + p2 +
                    ", rows=" + boardRows + ", columns=" + boardColumns);
            System.out.println("Strategies:\n" + Strategy.description());
            System.out.print("> ");
            s = scan.nextLine();
            if (s.equals(""))
                s = previousCommand;
            run(s);
            previousCommand = s;
        }
    }

    private static void run(String cmd) {
        if (cmd.equals("slow"))
            slowMode = true;
        if (cmd.equals("fast"))
            slowMode = false;
        if (cmd.length() > 2 && cmd.substring(0, 2).equals("p1"))
            p1 = Integer.parseInt(ac(cmd, " "));
        if (cmd.length() > 2 && cmd.substring(0, 2).equals("p2"))
            p2 = Integer.parseInt(ac(cmd, " "));
        if (cmd.length() > 4 && cmd.substring(0, 4).equals("rows"))
            boardRows = Integer.parseInt(ac(cmd, " "));
        if (cmd.length() > 7 && cmd.substring(0, 7).equals("columns"))
            boardColumns = Integer.parseInt(ac(cmd, " "));
        if (cmd.equals("play"))
            play(p1, p2);
        if (cmd.equals("help")) {
            System.out.println("Commands: slow, fast, p1 #, p2 #, play, help, tournament, rows #, columns #");
        }
        if (cmd.equals("tournament"))
            tournament();
    }

    // User input strategies
    private static int play() {
        String s = null, t = null;
        int p1 = -1, p2 = -1;
        System.out.println(Strategy.description());
        System.out.print("Player 1 Strategy: ");
        s = scan.nextLine();
        System.out.print("Player 2 Strategy: ");
        t = scan.nextLine();
        try {
            p1 = Integer.parseInt(s);
        } catch (Exception e) {
            p1 = -1;
        }
        try {
            p2 = Integer.parseInt(t);
        } catch (Exception e) {
            p2 = -1;
        }
        return play(p1, p2);
    }

    // Pass in strategies.
    // player: -1 = player, 0-... = strategy#.
    public static int play(int p1, int p2) {
        Board b = new Board(boardRows, boardColumns);
        int[][] tempBoard = null;
        int turn = 1;
        int[] move = null;
        int[] playerStrategy = { p1, p2 };
        SavedData[] savedArray = { new SavedData(), new SavedData() };
        while (winner(b) == 0) {
            savedArray[(turn - 1) / -2].lastMove = copy(move);
            if (playerStrategy[(turn - 1) / -2] == -1) {
                move = getMove(b, turn);
            } else {
                // Always make strategy player 1.
                if (turn == 1)
                    tempBoard = b.getBoard();
                else
                    tempBoard = b.getFlippedBoard();
                move = Strategy.getMove(playerStrategy[(turn - 1) / -2], tempBoard, savedArray[(turn - 1) / -2]);
            }
            if (!b.place(turn, move)) {
                b.printBoard(!slowMode);
                // Force loss here:
                if (move == null)
                    System.out.println("Failed to place move: " +
                            Board.xo(turn) + "null");
                else
                    System.out.println("Failed to place move: " +
                            Board.xo(turn) + "[" + move[0] + "," + move[1] + "]");
                System.out.println(Board.xo(turn * -1) + " wins");
                return turn * -1;
            }
            if (slowMode) {
                b.printBoard(false);
                System.out.println(Board.xo(turn) + " placed at " + move[0] + "," + move[1]);
                System.out.println("Press enter to continue...");
                scan.nextLine();
            }
            turn *= -1;
        }
        b.printBoard(!slowMode);
        System.out.println(Board.xo(winner(b)) + " wins");
        System.out.println("Press enter to continue...");
        scan.nextLine();
        return winner(b);
    }

    public static int autoPlay(int p1, int p2) {
        Board b = new Board(boardRows, boardColumns);
        int[][] tempBoard = null;
        int turn = 1;
        int[] move = null;
        int[] playerStrategy = { p1, p2 };
        SavedData[] savedArray = { new SavedData(), new SavedData() };
        while (winner(b) == 0) {
            savedArray[(turn - 1) / -2].lastMove = copy(move);
            if (playerStrategy[(turn - 1) / -2] == -1) {
                move = getMove(b, turn);
            } else {
                // Always make strategy player 1.
                if (turn == 1)
                    tempBoard = b.getBoard();
                else
                    tempBoard = b.getFlippedBoard();
                move = Strategy.getMove(playerStrategy[(turn - 1) / -2], tempBoard, savedArray[(turn - 1) / -2]);
            }
            if (!b.place(turn, move)) {
                // Force loss here:
                return turn * -1;
            }
            turn *= -1;
        }
        return winner(b);
    }

    public static int[] copy(int[] a) {
        if (a == null)
            return null;
        int[] b = new int[a.length];
        for (int i = 0; i < b.length; i++)
            b[i] = a[i];
        return b;
    }

    // Prints board. Asks user to choose a move.
    private static int[] getMove(Board b, int player) {
        String s = null, t, u;
        int[] rv = new int[2];
        b.printBoard();
        System.out.print("\n" + Board.xo(player) + ", enter row,col: ");
        s = scan.nextLine();
        t = bc(s, ",");
        u = ac(s, ",");
        if (t.length() * u.length() == 0)
            return null;
        try {
            rv[0] = Integer.parseInt(t);
            rv[1] = Integer.parseInt(u);
        } catch (Exception e) {
            return null;
        }
        return rv;
    }

    // Before Character
    public static String bc(String s, String c) {
        int index = s.indexOf(c);
        if (index == -1)
            return s;
        return s.substring(0, index);
    }

    // After Character
    public static String ac(String s, String c) {
        int index = s.indexOf(c);
        if (index == -1)
            return "";
        return s.substring(index + 1);
    }

    // Checks for winner on board. Returns 1,0,-1.
    public static int winner(Board board) {
        int[][] b = board.getBoard();
        int sum = 0;
        // Check horizontal
        for (int r = 0; r < b.length; r++) {
            for (int c = 0; c < b[r].length - 4; c++) {
                if (b[r][c] != 0) {
                    sum = 0;
                    for (int i = 0; i < 5; i++)
                        sum += b[r][c + i];
                    if (sum == 5 * b[r][c])
                        return b[r][c];
                }
            }
        }
        // Check vertical
        for (int r = 0; r < b.length - 4; r++) {
            for (int c = 0; c < b[r].length; c++) {
                if (b[r][c] != 0) {
                    sum = 0;
                    for (int i = 0; i < 5; i++)
                        sum += b[r + i][c];
                    if (sum == 5 * b[r][c])
                        return b[r][c];
                }
            }
        }
        // Check \
        for (int r = 0; r < b.length - 4; r++) {
            for (int c = 0; c < b[r].length - 4; c++) {
                if (b[r][c] != 0) {
                    sum = 0;
                    for (int i = 0; i < 5; i++)
                        sum += b[r + i][c + i];
                    if (sum == 5 * b[r][c])
                        return b[r][c];
                }
            }
        }
        // Check /
        for (int r = 0; r < b.length - 4; r++) {
            for (int c = 0; c < b[r].length - 4; c++) {
                if (b[r + 4][c] != 0) {
                    sum = 0;
                    for (int i = 0; i < 5; i++)
                        sum += b[r + 4 - i][c + i];
                    if (sum == 5 * b[r + 4][c])
                        return b[r + 4][c];
                }
            }
        }
        return 0;
    }
}
