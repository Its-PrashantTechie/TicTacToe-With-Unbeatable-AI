package com.tictactoe;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window for the Tic Tac Toe game.
 * Sets up the frame with the game panel.
 *
 * @author Prashant
 */
public class GameFrame extends JFrame {

    private static final int WIDTH = 520;
    private static final int HEIGHT = 700;

    public GameFrame() {
        setTitle("Tic Tac Toe — Unbeatable AI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setResizable(false);
        setLocationRelativeTo(null); // center on screen

        // Set dark title bar background where supported
        getContentPane().setBackground(new Color(0x1A, 0x1A, 0x2E));

        add(new GamePanel());
        setVisible(true);
    }
}
