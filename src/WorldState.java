import java.util.Arrays;
import java.util.LinkedList;

public class WorldState implements Comparable<WorldState> {
    private char[][] grid;
    private boolean[][] marked;
    private int[] wordLengths;
    private String[] words;
    int curRow; // current position in path we are making with word
    int curCol; // if we finish a word, this will be -1 (time to start a new word or be done)

    public WorldState(char[][] grid, int[] wordLengths) {
        this.grid = grid;

        this.marked = new boolean[this.grid.length][this.grid[0].length];
        for (boolean[] row : this.marked) {
            Arrays.fill(row, false);
        }

        this.wordLengths = wordLengths;
        this.words = new String[this.wordLengths.length];
        Arrays.fill(this.words, ""); // initialize array
    }

    public WorldState(WorldState ws) {
        char[][] wsGrid = ws.getGrid();
        this.grid = new char[wsGrid.length][wsGrid[0].length];
        for (int i = 0; i < wsGrid.length; i++) {
            this.grid[i] = Arrays.copyOf(wsGrid[i], wsGrid[i].length);
        }

        boolean[][] wsMarked = ws.getMarked();
        this.marked = new boolean[wsMarked.length][wsMarked[0].length];
        for (int i = 0; i < wsMarked.length; i++) {
            this.marked[i] = Arrays.copyOf(wsMarked[i], wsMarked[i].length);
        }

        this.wordLengths = Arrays.copyOf(ws.getWordLengths(), ws.getWordLengths().length);
        this.words = Arrays.copyOf(ws.getWords(), ws.getWords().length);

        this.curRow = ws.curRow;
        this.curCol = ws.curCol;
    }

    public char[][] getGrid() {
        return this.grid;
    }

    public char getGrid(int row, int column) {
        return this.grid[row][column];
    }

    public boolean[][] getMarked() {
        return this.marked;
    }

    public boolean getMarked(int row, int column) {
        return this.marked[row][column];
    }

    public void move(int row, int column) {
        this.marked[row][column] = true;
        this.curRow = row;
        this.curCol = column;
    }

    public int[] getWordLengths() {
        return this.wordLengths;
    }

    public String[] getWords() {
        return this.words;
    }

    public String getWord(int i) {
        return this.words[i];
    }

    public void append(int i, char c) {
        this.words[i] += c;
    }

    public char get() {
        return this.grid[this.curRow][this.curCol];
    }

    public int getCurRow() {
        return this.curRow;
    }

    public int getCurCol() {
        return this.curCol;
    }

    public String toString() {
        String rval = "";
        for (String word : this.words) {
            rval += word + " ";
        }
        return rval.trim();
    }

    public LinkedList<WorldState> getLegalMoves(LinkedList<String> dict) {
        LinkedList<WorldState> moves = new LinkedList<>();

        for (int i = 0; i < this.words.length; i++) {
            if (this.words[i].length() == 0) {
                // we are starting a new word here
                for (int r = 0; r < this.grid.length; r++) {
                    for (int c = 0; c < this.grid[0].length; c++) {
                        if (this.marked[r][c]) continue; // already been here

                        WorldState next = new WorldState(this);
                        next.move(r, c);
                        next.append(i, next.get());
                        // don't need to check against dictionary when we are starting a word
                        moves.add(next);
                    }
                }

                return moves;
            } else if (this.words[i].length() < this.wordLengths[i]) {
                // we are continuing an unfinished word here
                for (int r = -1; r <= 1; r++) {
                    for (int c = -1; c <= 1; c++) {
                        // a few cases where we don't want to travel a path
                        if (r == 0 && c == 0) continue; // already here
                        if (this.curRow + r >= this.grid.length || this.curRow + r < 0) continue; // out of bounds
                        if (this.curCol + c >= this.grid[0].length || this.curCol + c < 0) continue; // out of bounds
                        if (this.marked[this.curRow + r][this.curCol + c]) continue; // already been here)
                        if (this.grid[this.curRow + r][this.curCol + c] == '_') continue; // blank space

                        // make the path move
                        WorldState next = new WorldState(this);
                        next.move(next.getCurRow() + r, next.getCurCol() + c);
                        next.append(i, next.get());

                        // if any word in the dictionary matches what we have so far, add it to future moves
                        // this should (hopefully) cut out on a lot of states
                        String w = next.getWord(i);
                        if (dict.stream().anyMatch(s -> s.startsWith(w))) {
                            // if we just completed a word
                            if (next.getWord(i).length() == next.getWordLengths()[i]) {
                                // simulate tile dropping
                                boolean changed;
                                char[][] nextGrid = next.getGrid();
                                boolean[][] nextMarked = next.getMarked();
                                do {
                                    changed = false;
                                    // TODO: is there a better, more convention-y way to name these variables?
                                    // rr and cc sounds a little ridiculous but r and c are already in use in this scope
                                    for (int rr = 1; rr < nextGrid.length; rr++) {
                                        for (int cc = 0; cc < nextGrid[0].length; cc++) {
                                            if (!nextMarked[rr - 1][cc] && nextMarked[rr][cc]) {
                                                // swap the two tiles
                                                nextMarked[rr - 1][cc] = true;
                                                nextMarked[rr][cc] = false;
                                                char temp = nextGrid[rr - 1][cc];
                                                nextGrid[rr - 1][cc] = nextGrid[rr][cc];
                                                nextGrid[rr][cc] = temp;
                                                changed = true;
                                            }
                                        }
                                    }
                                } while (changed);
                            }

                            moves.add(next);
                        }
                    }
                }

                return moves;
            }
            // else the word has already been completed; onto the next one
        }

        // completed state
        return null;
    }

    @Override
    public int compareTo(WorldState o) {
        // allows for easier sorting of a list of worldstates
        return this.toString().compareTo(o.toString());
    }
}
