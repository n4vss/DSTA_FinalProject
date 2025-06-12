package WordGuessingGame;

import java.util.*;
import javax.swing.JOptionPane;

public class GameLogic {
    
    private String wordToGuess;                      // The word the player has to guess
    private char[] progress;                         // Player's progress represented by '_' and guessed letters
    
    private int undoAttempts = 0;                    // Number of undo actions used
    private static final int MAX_UNDO_ATTEMPTS = 3;  // Max undos allowed per game
    private int attemptsLeft;                        // Remaining incorrect guesses allowed
    private final int maxAttempts = 6;               // Total allowed wrong attempts per game
    private final WordBank wordBank = new WordBank(); // Provides words and hints
    private boolean gameActive = true;               // Is the game currently active?
    
    private final HashSet<Character> guessedLetters = new HashSet<>(); // Stores already guessed letters
    private final Stack<Character> guessHistory = new Stack<>();       // Stores guess history for undo functionality
    private final Queue<String> hintQueue = new LinkedList<>();        // Stores hints for the current word
    private final HashMap<Character, List<Integer>> letterPositions = new HashMap<>(); // Letter positions in word
    private final ArrayList<String> gameHistory = new ArrayList<>();   // Game logs (guesses, results, etc.)
    
    private GameGUI gui; // Reference to GUI object

    // Constructor takes the GUI object to control the UI
    public GameLogic(GameGUI gui) {
        this.gui = gui;
    }

    // Starts the full game loop
    public void startGameLoop() {
        gui.showDifficultyDialog(); // Ask player to choose difficulty
        initializeGame();           // Set up new game state
        playGame();                 // Begin game (initial display)
    }

    // Selects word and hints based on difficulty level
    public void chooseDifficulty(String difficulty) {
        wordToGuess = wordBank.getRandomWord(difficulty);
        hintQueue.addAll(wordBank.getHints(wordToGuess));
        mapLetterPositions(); // Map letters to their positions
    }

    // Prepares the letter position map for the chosen word
    private void mapLetterPositions() {
        for (int i = 0; i < wordToGuess.length(); i++) {
            char c = wordToGuess.charAt(i);
            letterPositions.computeIfAbsent(c, k -> new ArrayList<>()).add(i);
        }
    }

    // Resets game state for a new game
    private void initializeGame() {
        progress = new char[wordToGuess.length()];
        Arrays.fill(progress, '_');              // Fill progress with underscores
        guessedLetters.clear();
        guessHistory.clear();
        attemptsLeft = maxAttempts;
        undoAttempts = 0;
        letterPositions.clear();
        hintQueue.clear();
        mapLetterPositions();
        hintQueue.addAll(wordBank.getHints(wordToGuess));
        gameHistory.add("New game started with word: " + wordToGuess);
        updateDisplay();
    }

    // Displays the current game state
    private void playGame() {
        updateDisplay();
    }

    // Processes all player input (letters or commands)
    public void processUserInput(String input) {
        if (input.isEmpty()) return;

        switch (input) {
            case "hint" -> showHint();
            case "undo" -> undoGuess();
            case "restart" -> {
                gameHistory.add("Game restarted");
                initializeGame();
            }
            case "quit" -> {
                gameActive = false;
                System.exit(0);
            }
            default -> {
                if (input.length() == 1 && Character.isLetter(input.charAt(0))) {
                    makeGuess(input.charAt(0)); // Process single character guess
                } else {
                    JOptionPane.showMessageDialog(gui, "Invalid input. Please enter a single letter or valid command.", 
                            "Invalid Input", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    // Handles guessing a letter
    private void makeGuess(char guess) {
        if (guessedLetters.contains(guess)) {
            JOptionPane.showMessageDialog(gui, "You already guessed '" + guess + "'", 
                    "Duplicate Guess", JOptionPane.WARNING_MESSAGE);
            return;
        }

        guessedLetters.add(guess);
        guessHistory.push(guess);
        gameHistory.add("Guessed: " + guess);
        gui.addToHistory("Guessed: " + guess);

        if (letterPositions.containsKey(guess)) {
            updateProgress(guess);
            gui.addToHistory("Correct guess!");
        } else {
            attemptsLeft--;
            gui.addToHistory("Wrong guess! Attempts left: " + attemptsLeft);
        }

        updateDisplay();

        if (isGameOver()) {
            displayResult();
            recordGameResult();
            if (askToPlayAgain()) {
                startGameLoop();
            } else {
                System.exit(0);
            }
        }
    }

    // Updates the visible progress with the correct letter positions
    private void updateProgress(char guess) {
        for (int pos : letterPositions.get(guess)) {
            progress[pos] = guess;
        }
    }

    // Displays the next hint, if available
    private void showHint() {
        if (!hintQueue.isEmpty()) {
            String hint = hintQueue.poll();
            gui.addToHistory("Hint: " + hint);
            JOptionPane.showMessageDialog(gui, "Hint: " + hint, "Hint", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(gui, "No more hints available.", "No Hints", JOptionPane.WARNING_MESSAGE);
        }
        updateDisplay();
    }

    // Undoes the last guess (if within the limit)
    private void undoGuess() {
        if (undoAttempts >= MAX_UNDO_ATTEMPTS) {
            JOptionPane.showMessageDialog(gui, "Undo limit reached. You can only undo 3 times per game.", 
                    "Undo Limit", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (guessHistory.isEmpty()) {
            JOptionPane.showMessageDialog(gui, "Nothing to undo.", "Nothing to Undo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        char lastGuess = guessHistory.pop();
        guessedLetters.remove(lastGuess);
        gameHistory.add("Undid guess: " + lastGuess);
        gui.addToHistory("Undid guess: " + lastGuess);
        undoAttempts++;

        // Rebuild progress after undo
        Arrays.fill(progress, '_');
        for (char c : guessedLetters) {
            if (letterPositions.containsKey(c)) {
                updateProgress(c);
            }
        }
        
        if (!letterPositions.containsKey(lastGuess)) {
            attemptsLeft++; // Restore attempt if the guess was wrong
        }

        updateDisplay();
    }

    // Checks if the game is over
    private boolean isGameOver() {
        return attemptsLeft <= 0 || String.valueOf(progress).equals(wordToGuess);
    }

    // Shows win/lose dialog
    private void displayResult() {
        boolean won = String.valueOf(progress).equals(wordToGuess);
        gui.showGameOver(won, wordToGuess);
    }

    // Logs the game result in the history
    private void recordGameResult() {
        String result = "Game result: " +
                (String.valueOf(progress).equals(wordToGuess) ? "Won" : "Lost") +
                " with word: " + wordToGuess;
        gameHistory.add(result);
        gui.addToHistory(result);
    }

    // Prompts user if they want to play again
    private boolean askToPlayAgain() {
        return gui.askToPlayAgain();
    }

    // Updates the GUI with current progress, guesses, attempts, etc.
    private void updateDisplay() {
        String wordProgress = String.valueOf(progress);
        String guessedLettersStr = String.join(", ", getSortedGuesses());
        int hintsLeft = hintQueue.size();
        int undosLeft = MAX_UNDO_ATTEMPTS - undoAttempts;
        
        gui.updateGameState(wordProgress, guessedLettersStr, attemptsLeft, hintsLeft, undosLeft);
    }

    // Returns a sorted list of guessed letters
    private List<String> getSortedGuesses() {
        return guessedLetters.stream().sorted().map(String::valueOf).toList();
    }
}
