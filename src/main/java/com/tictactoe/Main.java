package com.tictactoe;

import javax.swing.*;

/**
 * Application entry point.
 * Launches the game on the Swing Event Dispatch Thread for thread safety.
 *
 * @author Prashant
 */
public class Main {
    public static void main(String[] args) {
        // Use system look-and-feel for native file dialogs, then override with custom
        // painting
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(GameFrame::new);
    }
}
