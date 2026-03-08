package com.tictactoe;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * The main game panel with a modern dark theme UI.
 * Uses {@link GameLogic} for all game state management.
 *
 * @author Prashant
 */
public class GamePanel extends JPanel {

    // ─── Color Palette ────────────────────────────────────────
    private static final Color BG_DARK = new Color(0x1A, 0x1A, 0x2E);
    private static final Color BG_CARD = new Color(0x16, 0x21, 0x3E);
    private static final Color BG_CELL = new Color(0x0F, 0x3D, 0x62);
    private static final Color BG_CELL_HOVER = new Color(0x1A, 0x55, 0x7F);
    private static final Color COLOR_X = new Color(0x00, 0xD4, 0xFF); // cyan
    private static final Color COLOR_O = new Color(0xFF, 0x00, 0x6E); // magenta
    private static final Color COLOR_WIN = new Color(0x00, 0xFF, 0x87); // green glow
    private static final Color TEXT_PRIMARY = new Color(0xE9, 0xEC, 0xEF);
    private static final Color TEXT_SECONDARY = new Color(0xAD, 0xB5, 0xBD);
    private static final Color BTN_ACTIVE = new Color(0x00, 0xD4, 0xFF);
    private static final Color BTN_INACTIVE = new Color(0x2D, 0x2D, 0x44);

    // ─── Fonts ────────────────────────────────────────────────
    private static final Font FONT_CELL = new Font("Segoe UI", Font.BOLD, 72);
    private static final Font FONT_SCORE = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_BTN = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_STATUS = new Font("Segoe UI", Font.ITALIC, 15);

    // ─── Game State ───────────────────────────────────────────
    private final GameLogic logic = new GameLogic();
    private final CellButton[] cells = new CellButton[9];
    private final JLabel scorePlayerLabel = new JLabel("0");
    private final JLabel scoreAILabel = new JLabel("0");
    private final JLabel scoreDrawLabel = new JLabel("0");
    private final JLabel statusLabel = new JLabel("Your turn — place X");
    private final JButton[] diffButtons = new JButton[3];
    private boolean gameOver = false;

    public GamePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildBoardPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);
    }

    // ─── UI Construction ──────────────────────────────────────

    private JPanel buildTopPanel() {
        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Title
        JLabel title = new JLabel("TIC TAC TOE", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        top.add(title, BorderLayout.NORTH);

        // Score panel
        JPanel scorePanel = new JPanel(new GridLayout(1, 3, 12, 0));
        scorePanel.setOpaque(false);

        scorePanel.add(buildScoreCard("YOU (X)", scorePlayerLabel, COLOR_X));
        scorePanel.add(buildScoreCard("DRAWS", scoreDrawLabel, TEXT_SECONDARY));
        scorePanel.add(buildScoreCard("AI (O)", scoreAILabel, COLOR_O));

        top.add(scorePanel, BorderLayout.CENTER);

        // Difficulty selector
        JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        diffPanel.setOpaque(false);

        String[] labels = { "Easy", "Medium", "Hard" };
        GameLogic.Difficulty[] diffs = GameLogic.Difficulty.values();
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            diffButtons[i] = createPillButton(labels[i]);
            diffButtons[i].addActionListener(e -> {
                logic.setDifficulty(diffs[idx]);
                updateDiffButtons();
            });
            diffPanel.add(diffButtons[i]);
        }
        updateDiffButtons();

        top.add(diffPanel, BorderLayout.SOUTH);
        return top;
    }

    private JPanel buildBoardPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(10, 30, 10, 30));

        JPanel board = new JPanel(new GridLayout(3, 3, 8, 8));
        board.setOpaque(false);

        for (int i = 0; i < 9; i++) {
            cells[i] = new CellButton(i);
            board.add(cells[i]);
        }

        wrapper.add(board, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Status label
        statusLabel.setFont(FONT_STATUS);
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottom.add(statusLabel, BorderLayout.NORTH);

        // Restart button
        JButton restart = createPillButton("⟳  New Game");
        restart.setFont(FONT_BTN.deriveFont(14f));
        restart.addActionListener(e -> resetGame());

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnWrap.setOpaque(false);
        btnWrap.add(restart);
        bottom.add(btnWrap, BorderLayout.CENTER);

        return bottom;
    }

    // ─── Score Card ───────────────────────────────────────────

    private JPanel buildScoreCard(String label, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 8, 10, 8));

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        valueLabel.setFont(FONT_SCORE);
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);
        return card;
    }

    // ─── Pill Button ──────────────────────────────────────────

    private JButton createPillButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(BTN_INACTIVE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));

        btn.addMouseListener(new MouseAdapter() {
            Color prevBg;

            @Override
            public void mouseEntered(MouseEvent e) {
                prevBg = btn.getBackground();
                btn.setBackground(prevBg.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(prevBg);
            }
        });
        return btn;
    }

    // ─── Difficulty Button State ──────────────────────────────

    private void updateDiffButtons() {
        GameLogic.Difficulty current = logic.getDifficulty();
        GameLogic.Difficulty[] diffs = GameLogic.Difficulty.values();
        for (int i = 0; i < 3; i++) {
            if (diffs[i] == current) {
                diffButtons[i].setBackground(BTN_ACTIVE);
                diffButtons[i].setForeground(BG_DARK);
            } else {
                diffButtons[i].setBackground(BTN_INACTIVE);
                diffButtons[i].setForeground(TEXT_PRIMARY);
            }
        }
    }

    // ─── Game Actions ─────────────────────────────────────────

    private void handleCellClick(int index) {
        if (gameOver || !logic.isPlayerTurn())
            return;

        if (logic.makePlayerMove(index)) {
            cells[index].setMark("X");
            cells[index].repaint();

            GameLogic.GameResult result = logic.getGameResult();
            if (result != GameLogic.GameResult.IN_PROGRESS) {
                endGame(result);
                return;
            }

            statusLabel.setText("AI is thinking...");

            // Slight delay for AI move to feel natural
            Timer aiTimer = new Timer(300, evt -> {
                int aiMove = logic.makeAIMove();
                if (aiMove != -1) {
                    cells[aiMove].setMark("O");
                    cells[aiMove].repaint();
                }

                GameLogic.GameResult afterAI = logic.getGameResult();
                if (afterAI != GameLogic.GameResult.IN_PROGRESS) {
                    endGame(afterAI);
                } else {
                    statusLabel.setText("Your turn — place X");
                }
            });
            aiTimer.setRepeats(false);
            aiTimer.start();
        }
    }

    private void endGame(GameLogic.GameResult result) {
        gameOver = true;
        logic.recordResult(result);
        updateScores();

        // Highlight winning line
        int[] winLine = logic.getWinningLine();
        if (winLine != null) {
            for (int idx : winLine) {
                cells[idx].setWinning(true);
            }
        }

        String message = switch (result) {
            case X_WINS -> "🎉 You win!";
            case O_WINS -> "AI wins — try again!";
            case DRAW -> "It's a draw!";
            default -> "";
        };
        statusLabel.setText(message);
        statusLabel.setForeground(result == GameLogic.GameResult.X_WINS ? COLOR_WIN
                : result == GameLogic.GameResult.O_WINS ? COLOR_O : TEXT_SECONDARY);

        // Auto-prompt for new game after a moment
        Timer promptTimer = new Timer(1500, evt -> {
            int res = JOptionPane.showOptionDialog(this,
                    message + "\nPlay again?",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null, new String[] { "Rematch", "Quit" }, "Rematch");
            if (res == JOptionPane.YES_OPTION) {
                resetGame();
            } else {
                System.exit(0);
            }
        });
        promptTimer.setRepeats(false);
        promptTimer.start();
    }

    private void resetGame() {
        logic.resetBoard();
        gameOver = false;
        for (CellButton cell : cells) {
            cell.setMark(null);
            cell.setWinning(false);
        }
        statusLabel.setText("Your turn — place X");
        statusLabel.setForeground(TEXT_SECONDARY);
        repaint();
    }

    private void updateScores() {
        scorePlayerLabel.setText(String.valueOf(logic.getPlayerWins()));
        scoreAILabel.setText(String.valueOf(logic.getAIWins()));
        scoreDrawLabel.setText(String.valueOf(logic.getDraws()));
    }

    // ═══════════════════════════════════════════════════════════
    // Custom-painted cell button with hover, mark, and win glow
    // ═══════════════════════════════════════════════════════════
    private class CellButton extends JPanel {
        private String mark; // "X", "O", or null
        private boolean hovering;
        private boolean winning;

        CellButton(int index) {
            setOpaque(false);
            setPreferredSize(new Dimension(120, 120));
            setMinimumSize(new Dimension(80, 80));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (mark == null && !gameOver) {
                        hovering = true;
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovering = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    handleCellClick(index);
                }
            });
        }

        void setMark(String m) {
            this.mark = m;
            hovering = false;
        }

        void setWinning(boolean w) {
            this.winning = w;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // Background
            Color bg = winning ? COLOR_WIN.darker().darker() : (hovering ? BG_CELL_HOVER : BG_CELL);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 18, 18));

            // Winning glow border
            if (winning) {
                g2.setColor(new Color(COLOR_WIN.getRed(), COLOR_WIN.getGreen(), COLOR_WIN.getBlue(), 120));
                g2.setStroke(new BasicStroke(3));
                g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 18, 18));
            }

            // Draw mark
            if (mark != null) {
                g2.setFont(FONT_CELL);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(mark)) / 2;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();

                // Glow effect
                Color markColor = mark.equals("X") ? COLOR_X : COLOR_O;
                g2.setColor(new Color(markColor.getRed(), markColor.getGreen(), markColor.getBlue(), 50));
                g2.setFont(FONT_CELL.deriveFont(78f));
                g2.drawString(mark, tx - 1, ty + 1);

                // Main mark
                g2.setColor(markColor);
                g2.setFont(FONT_CELL);
                g2.drawString(mark, tx, ty);
            }

            // Hover indicator
            if (hovering && mark == null) {
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 18, 18));
            }

            g2.dispose();
        }
    }
}
