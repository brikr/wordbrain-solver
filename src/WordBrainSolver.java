import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class WordBrainSolver {
    public static void main(String[] args) {
        // initial world setup
        Scanner scan = new Scanner(System.in);

        System.out.print("Size of grid? ");
        int gridSize = scan.nextInt(); // NxN grid
        char[][] grid = new char[gridSize][gridSize];

        System.out.print("Number of words in solution? ");
        int numWords = scan.nextInt();

        System.out.println("Enter the length of each word, separated by whitespace");
        int[] wordLengths = new int[numWords];
        for (int i = 0; i < numWords; i++) {
            wordLengths[i] = scan.nextInt();
        }

        System.out.println("Enter the grid with each letter separated by a whitespace");
        for (int i = 0; i < gridSize * gridSize; i++) {
            // grid is stored in [row][column] format, top left is 0
            grid[i / gridSize][i % gridSize] = scan.next().toLowerCase().charAt(0); // fill in each letter in the grid
        }

        LinkedList<String> dict = new LinkedList<>();
        initializeDict(dict, args[0]);

        System.out.println("Optimizing dictionary. Old size: " + dict.size());
        // filter out by length initially to speed up program
        dict = dict.stream().filter(s -> {
            // remove all words from dictionary that will not appear in puzzle
            // more optimizations can be performed, but there will be a diminishing return on runtime
            for (int i = 0; i < wordLengths.length; i++) {
                if (s.length() == wordLengths[i]) {
                    for (int r = 0; r < grid.length; r++) {
                        for (int c = 0; c < grid[0].length; c++) {
                            if (s.contains("" + grid[r][c])) return true;
                        }
                    }
                }
            }
            return false;
        }).collect(Collectors.toCollection(LinkedList::new));
        System.out.println("Dictionary optimized. New size: " + dict.size());

        Stack<WorldState> stack = new Stack<>();
        WorldState initialState = new WorldState(grid, wordLengths);
        stack.push(initialState);
        int solutions, searched;
        solutions = searched = 0;
        long startTime = System.currentTimeMillis();
        while (!stack.isEmpty()) {
            WorldState state = stack.pop();
            searched++;

            LinkedList<WorldState> moves = state.getLegalMoves(dict);
            if (moves == null) {
                solutions++;
                System.out.println(state); // possible solution
            } else stack.addAll(moves);
        }

        System.out.printf("Searched %d states. %d possible solutions found in %.1f seconds.\n", searched, solutions, (System.currentTimeMillis() - startTime) / 1000.0);
    }

    private static void initializeDict(LinkedList<String> dict, String filename) {
        try {
            Scanner scan = new Scanner(new File(filename));
            while (scan.hasNextLine()) {
                dict.add(scan.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
