package WordGuessingGame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Schedules the GUI creation to run on the Event Dispatch Thread (EDT)
        // This is the recommended way to create and update GUI components in Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Sets the look and feel of the GUI to match the system's native appearance
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Print the exception stack trace if setting the look and feel fails
                e.printStackTrace();
            }

            // Creates an instance of the GameGUI class and makes the window visible
            new GameGUI().setVisible(true);
        });
    }
}
