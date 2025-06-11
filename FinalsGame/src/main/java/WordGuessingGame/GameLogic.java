package WordGuessingGame;

import java.util.*;
import javax.swing.JOptionPane;

public class GameLogic {
    private String wordToGuess;
    private char[] progress;
    private int undoAttempts = 0;
    private static final int MAX_UNDO_ATTEMPTS = 3;
    private int attemptsLeft;
    private final int maxAttempts = 6;
    private final WordBank wordBank = new WordBank();
    private boolean gameActive = true;
    private final Set<Character> guessedLetters = new HashSet<>();
    private final Stack<Character> guessHistory = new Stack<>();
    private final Queue<String> hintQueue = new LinkedList<>();
    private final HashMap<Character, List<Integer>> letterPositions = new HashMap<>();
    private final ArrayList<String> gameHistory = new ArrayList<>();
    private GameGUI gui;

    public GameLogic(GameGUI gui) {
        this.gui = gui;
    }

    public void startGameLoop() {
        gui.showDifficultyDialog();
        initializeGame();
        playGame();
    }

    public void chooseDifficulty(String difficulty) {
        wordToGuess = wordBank.getRandomWord(difficulty);
        hintQueue.addAll(wordBank.getHints(wordToGuess));
        mapLetterPositions();
    }

    private void mapLetterPositions() {
        for (int i = 0; i < wordToGuess.length(); i++) {
            char c = wordToGuess.charAt(i);
            letterPositions.computeIfAbsent(c, k -> new ArrayList<>()).add(i);
        }
    }

    private void initializeGame() {
        progress = new char[wordToGuess.length()];
        Arrays.fill(progress, '_');
        guessedLetters.clear();
        guessHistory.clear();
        attemptsLeft = maxAttempts;
        undoAttempts = 0;
        letterPositions.clear(); // Clear previous positions
        hintQueue.clear();       // Clear previous hints
        mapLetterPositions();    // Re-map for new word
        hintQueue.addAll(wordBank.getHints(wordToGuess)); // Re-add hints for new word
        gameHistory.add("New game started with word: " + wordToGuess);
        updateDisplay();
    }

    private void playGame() {
        updateDisplay();
    }

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
                    makeGuess(input.charAt(0));
                } else {
                    JOptionPane.showMessageDialog(gui, "Invalid input. Please enter a single letter or valid command.", 
                            "Invalid Input", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

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

    private void updateProgress(char guess) {
        for (int pos : letterPositions.get(guess)) {
            progress[pos] = guess;
        }
    }

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

        Arrays.fill(progress, '_');
        for (char c : guessedLetters) {
            if (letterPositions.containsKey(c)) {
                updateProgress(c);
            }
        }
        
        if (!letterPositions.containsKey(lastGuess)) {
            attemptsLeft++;
        }

        updateDisplay();
    }

    private boolean isGameOver() {
        return attemptsLeft <= 0 || String.valueOf(progress).equals(wordToGuess);
    }

    private void displayResult() {
        boolean won = String.valueOf(progress).equals(wordToGuess);
        gui.showGameOver(won, wordToGuess);
    }

    private void recordGameResult() {
        String result = "Game result: " +
                (String.valueOf(progress).equals(wordToGuess) ? "Won" : "Lost") +
                " with word: " + wordToGuess;
        gameHistory.add(result);
        gui.addToHistory(result);
    }

    private boolean askToPlayAgain() {
        return gui.askToPlayAgain();
    }

    private void updateDisplay() {
        String wordProgress = String.valueOf(progress);
        String guessedLettersStr = String.join(", ", getSortedGuesses());
        int hintsLeft = hintQueue.size();
        int undosLeft = MAX_UNDO_ATTEMPTS - undoAttempts;
        
        gui.updateGameState(wordProgress, guessedLettersStr, attemptsLeft, hintsLeft, undosLeft);
    }

    private List<String> getSortedGuesses() {
        return guessedLetters.stream().sorted().map(String::valueOf).toList();
    }
}