import javax.swing.*;
import java.awt.event.*;

public class GameFrame extends JFrame {
    GamePanel panel;

    public GameFrame() {
        panel = new GamePanel();
        this.add(panel);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Difficulty");
        JMenuItem easy = new JMenuItem("Easy");
        JMenuItem hard = new JMenuItem("Hard");

        easy.addActionListener(e -> panel.setDifficulty(GamePanel.Difficulty.EASY));
        hard.addActionListener(e -> panel.setDifficulty(GamePanel.Difficulty.HARD));

        menu.add(easy);
        menu.add(hard);
        menuBar.add(menu);
        this.setJMenuBar(menuBar);

        this.setTitle("Tic Tac Toe");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600,600);
        this.setResizable(false);
        this.setVisible(true);
    }
}
