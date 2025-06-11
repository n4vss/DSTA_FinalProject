package WordGuessingGame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GameGUI extends JFrame {
    private GameLogic game;
    private JPanel mainPanel, gamePanel;
    private JLabel wordLabel, attemptsLabel, guessedLettersLabel, hintsLabel, undosLabel;
    private JTextField inputField;
    private JTextArea historyArea;
    private JButton hintButton, undoButton, restartButton, quitButton;

    public GameGUI() {
        game = new GameLogic(this);
        initializeUI();
    }

    private void initializeUI() {
        setTitle("GuessBreaker - Word Guessing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        createMenuPanel();
        createGamePanel();
        createHistoryPanel();

        add(mainPanel);
    }

    private void createMenuPanel() {
        JPanel menuPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        
        JButton newGameButton = new JButton("New Game");
        JButton howToPlayButton = new JButton("How to Play");
        JButton exitButton = new JButton("Exit");

        newGameButton.addActionListener(e -> game.startGameLoop());
        howToPlayButton.addActionListener(e -> showInstructions());
        exitButton.addActionListener(e -> System.exit(0));

        menuPanel.add(newGameButton);
        menuPanel.add(howToPlayButton);
        menuPanel.add(exitButton);

        mainPanel.add(menuPanel, BorderLayout.NORTH);
    }

    private void createGamePanel() {
        gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        gamePanel.setBorder(BorderFactory.createTitledBorder("Game"));

        wordLabel = new JLabel("Word: ");
        wordLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        wordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        attemptsLabel = new JLabel("Attempts left: 6");
        guessedLettersLabel = new JLabel("Guessed letters: ");
        hintsLabel = new JLabel("Hints remaining: ");
        undosLabel = new JLabel("Undos remaining: 3");

        JPanel inputPanel = new JPanel();
        inputField = new JTextField(15);
        JButton guessButton = new JButton("Guess");
        
        guessButton.addActionListener(e -> {
            String input = inputField.getText().trim().toLowerCase();
            if (!input.isEmpty()) {
                game.processUserInput(input);
                inputField.setText("");
            }
        });

        inputField.addActionListener(e -> guessButton.doClick());

        inputPanel.add(new JLabel("Enter letter: "));
        inputPanel.add(inputField);
        inputPanel.add(guessButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        hintButton = new JButton("Hint");
        undoButton = new JButton("Undo");
        restartButton = new JButton("Restart");
        quitButton = new JButton("Quit");

        hintButton.addActionListener(e -> game.processUserInput("hint"));
        undoButton.addActionListener(e -> game.processUserInput("undo"));
        restartButton.addActionListener(e -> game.processUserInput("restart"));
        quitButton.addActionListener(e -> game.processUserInput("quit"));

        buttonPanel.add(hintButton);
        buttonPanel.add(undoButton);
        buttonPanel.add(restartButton);
        buttonPanel.add(quitButton);

        gamePanel.add(Box.createVerticalStrut(10));
        gamePanel.add(wordLabel);
        gamePanel.add(Box.createVerticalStrut(10));
        gamePanel.add(attemptsLabel);
        gamePanel.add(guessedLettersLabel);
        gamePanel.add(hintsLabel);
        gamePanel.add(undosLabel);
        gamePanel.add(Box.createVerticalStrut(20));
        gamePanel.add(inputPanel);
        gamePanel.add(Box.createVerticalStrut(10));
        gamePanel.add(buttonPanel);

        mainPanel.add(gamePanel, BorderLayout.CENTER);
    }

    private void createHistoryPanel() {
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Game History"));
        
        historyArea = new JTextArea(8, 30);
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(historyArea);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(historyPanel, BorderLayout.SOUTH);
    }

    private void showInstructions() {
        String instructions = "=== How to Play ===\n\n" +
                "1. Choose a difficulty level (Easy, Medium, Hard)\n" +
                "2. Try to guess the hidden word by entering letters\n" +
                "3. You have 6 attempts to guess the word\n" +
                "4. Commands:\n" +
                "   - 'hint' - get a helpful hint\n" +
                "   - 'undo' - undo your last guess\n" +
                "   - 'restart' - start a new game\n" +
                "   - 'quit' - exit the game\n\n" +
                "Good luck!";
                
        JOptionPane.showMessageDialog(this, instructions, "How to Play", JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateGameState(String wordProgress, String guessedLetters, int attemptsLeft, int hintsLeft, int undosLeft) {
        wordLabel.setText("Word: " + wordProgress);
        attemptsLabel.setText("Attempts left: " + attemptsLeft);
        guessedLettersLabel.setText("Guessed letters: " + guessedLetters);
        hintsLabel.setText("Hints remaining: " + hintsLeft);
        undosLabel.setText("Undos remaining: " + undosLeft);
    }

    public void addToHistory(String message) {
        historyArea.append(message + "\n");
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }

    public void showDifficultyDialog() {
        String[] options = {"Easy", "Medium", "Hard"};
        int choice = JOptionPane.showOptionDialog(this,
                "Choose difficulty level:",
                "Difficulty",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
                
        String difficulty = switch (choice) {
            case 0 -> "easy";
            case 1 -> "medium";
            case 2 -> "hard";
            default -> "medium";
        };
        
        game.chooseDifficulty(difficulty);
    }

    public void showGameOver(boolean won, String word) {
        String message = won ? 
                "Congratulations! You guessed the word: " + word :
                "Game over! The word was: " + word;
                
        JOptionPane.showMessageDialog(this, message, "Game Over", 
                won ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    public boolean askToPlayAgain() {
        int response = JOptionPane.showConfirmDialog(this,
                "Would you like to play again?",
                "Play Again",
                JOptionPane.YES_NO_OPTION);
                
        return response == JOptionPane.YES_OPTION;
    }
}