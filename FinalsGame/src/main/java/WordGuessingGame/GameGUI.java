package WordGuessingGame;

import java.awt.*;
import javax.swing.*;

public class GameGUI extends JFrame {
    private GameLogic game;
    private JPanel mainPanel, gamePanel;
    private JLabel wordLabel, attemptsLabel, guessedLettersLabel, hintsLabel, undosLabel;
    private JTextField inputField;
    private JTextArea historyArea;
    private JButton hintButton, undoButton, restartButton;

    public GameGUI() {
        game = new GameLogic(this);
        initializeUI();
    }

    private void initializeUI() {
        setTitle("🎯 GuessBreaker - Word Guessing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 550);
        setMinimumSize(new Dimension(650, 500));
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.decode("#f4f4f4"));

        createMenuPanel();
        createGamePanel();
        createHistoryPanel();

        add(mainPanel);
        setVisible(true);
    }

    private void createMenuPanel() {
        JPanel menuPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        menuPanel.setBackground(Color.decode("#f4f4f4"));

        JButton newGameButton = createStyledButton("🆕 New Game");
        JButton howToPlayButton = createStyledButton("📘 How to Play");
        JButton exitButton = createStyledButton("🚪 Exit");

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
        gamePanel.setBackground(Color.WHITE);
        gamePanel.setBorder(BorderFactory.createTitledBorder("Game Area"));

        wordLabel = createLabel("Word: ", 24, true);
        attemptsLabel = createLabel("Attempts left: 6", 16, false);
        guessedLettersLabel = createLabel("Guessed letters: ", 16, false);
        hintsLabel = createLabel("Hints remaining: ", 16, false);
        undosLabel = createLabel("Undos remaining: 3", 16, false);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        inputPanel.setBackground(Color.WHITE);

        inputField = new JTextField(12);
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        JButton guessButton = createStyledButton("🎯 Guess");

        guessButton.addActionListener(e -> {
            String input = inputField.getText().trim().toLowerCase();
            if (!input.isEmpty()) {
                game.processUserInput(input);
                inputField.setText("");
            }
        });

        inputField.addActionListener(e -> guessButton.doClick());

        inputPanel.add(new JLabel("Enter letter:"));
        inputPanel.add(inputField);
        inputPanel.add(guessButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        hintButton = createStyledButton("💡 Hint");
        undoButton = createStyledButton("↩️ Undo");
        restartButton = createStyledButton("🔁 Restart");

        hintButton.addActionListener(e -> game.processUserInput("hint"));
        undoButton.addActionListener(e -> game.processUserInput("undo"));
        restartButton.addActionListener(e -> game.processUserInput("restart"));

        buttonPanel.add(hintButton);
        buttonPanel.add(undoButton);
        buttonPanel.add(restartButton);

        gamePanel.add(Box.createVerticalStrut(10));
        gamePanel.add(wordLabel);
        gamePanel.add(Box.createVerticalStrut(10));
        gamePanel.add(attemptsLabel);
        gamePanel.add(guessedLettersLabel);
        gamePanel.add(hintsLabel);
        gamePanel.add(undosLabel);
        gamePanel.add(Box.createVerticalStrut(15));
        gamePanel.add(inputPanel);
        gamePanel.add(buttonPanel);

        mainPanel.add(gamePanel, BorderLayout.CENTER);
    }

    private void createHistoryPanel() {
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setBorder(BorderFactory.createTitledBorder("Game History"));

        historyArea = new JTextArea(6, 30);
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        historyPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(historyPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text, int size, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBackground(Color.decode("#e0e0e0"));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return button;
    }

    public void showInstructions() {
        String instructions = "=== How to Play ===\n\n" +
                "1. Choose a difficulty level (Easy, Medium, Hard)\n" +
                "2. Try to guess the hidden word by entering letters\n" +
                "3. You have 6 attempts to guess the word\n" +
                "4. Commands:\n" +
                "   - 'hint' - get a helpful hint\n" +
                "   - 'undo' - undo your last guess\n" +
                "   - 'restart' - start a new game\n" +
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
                "🎉 Congratulations! You guessed the word: " + word :
                "❌ Game over! The word was: " + word;

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
