
import java.util.ArrayList;

public class Strategy {
    private static ArrayList<StratFn> allStrats = new ArrayList<StratFn>();
    private static int junk = addStrats();

    private static String[] teamNames = { "Kim", "Sample", "Cheese", "CheeseBoard", "Cheese2" };

    public static int[] getMove(int strategyNumber, int[][] b, SavedData memory) {
        if (strategyNumber >= allStrats.size())
            return null;
        return allStrats.get(strategyNumber).run(b, memory);
    }

    public static int addStrats() {

        allStrats.add((b, m) -> strat2(b, m)); // Cheese
        allStrats.add((b, m) -> strat3(b, m)); // CheeseBoard
        allStrats.add((b, m) -> strat4(b, m)); // CheeseBoard
        return 0;
    }

    public static String[] getTeamNames() {
        String[] copy = new String[teamNames.length];
        for (int i = 0; i < teamNames.length; i++)
            copy[i] = teamNames[i];
        return copy;
    }

    public static String description() {
        String s = "-1: Player\n";
        for (int i = 0; i < teamNames.length; i++)
            s += i + ": " + teamNames[i] + "\n";
        return s;
    }

    // To the right of the last move every time.
    public static int[] strat2(int[][] b, SavedData memory) {
        return Cheese.strat(b, memory);
    }

    // To the right of the last move every time.
    public static int[] strat3(int[][] b, SavedData memory) {
        return CheeseBoard.strat(b, memory);
    }

    // To the right of the last move every time.
    public static int[] strat4(int[][] b, SavedData memory) {
        return Cheese2.strat(b, memory);
    }
}

interface StratFn {
    int[] run(int[][] b, SavedData memory);
}