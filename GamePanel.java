import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {

    JButton[] buttons = new JButton[9];
    boolean playerXTurn = true; // true = player, false = AI
    Random random = new Random();

    public enum Difficulty { EASY, HARD }
    Difficulty currentDifficulty = Difficulty.EASY;

    public GamePanel() {
        this.setLayout(new GridLayout(3,3));
        for(int i=0;i<9;i++){
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Arial", Font.BOLD, 100));
            buttons[i].setFocusPainted(false);
            buttons[i].addActionListener(this);
            this.add(buttons[i]);
        }
    }

    public void setDifficulty(Difficulty diff){
        this.currentDifficulty = diff;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clicked = (JButton)e.getSource();
        if(clicked.getText().equals("") && playerXTurn){
            clicked.setForeground(Color.BLUE);
            clicked.setText("X");
            playerXTurn = false;
            checkWinner();
            startAIMove();
        }
    }

    private void startAIMove(){
        if(!playerXTurn){
            Timer t = new Timer(200, evt -> {
                aiMove();
                playerXTurn = true;
                checkWinner();
            });
            t.setRepeats(false);
            t.start();
        }
    }

    private void aiMove(){
        int move = -1;
        if(currentDifficulty == Difficulty.EASY){
            // Simple AI: try to win or block, else random
            move = findBestMove("O");
            if(move==-1) move=findBestMove("X");
            if(move==-1){
                ArrayList<Integer> empty = new ArrayList<>();
                for(int i=0;i<9;i++)
                    if(buttons[i].getText().equals("")) empty.add(i);
                if(!empty.isEmpty()) move = empty.get(random.nextInt(empty.size()));
            }
        } else {
            // Hard: proper Minimax
            char[] board = new char[9];
            for(int i=0;i<9;i++){
                board[i] = buttons[i].getText().isEmpty() ? '-' : buttons[i].getText().charAt(0);
            }
            move = minimaxBestMove(board);
        }

        if(move!=-1){
            buttons[move].setForeground(Color.RED);
            buttons[move].setText("O");
        }
    }

    private int minimaxBestMove(char[] board){
        int bestScore = Integer.MIN_VALUE;
        int move = -1;
        for(int i=0;i<9;i++){
            if(board[i]=='-'){
                board[i]='O';
                int score = minimax(board,false);
                board[i]='-';
                if(score>bestScore){
                    bestScore = score;
                    move = i;
                }
            }
        }
        return move;
    }

    private int minimax(char[] board, boolean isAITurn){
        String result = evaluate(board);
        if(result!=null){
            if(result.equals("O")) return 1;
            else if(result.equals("X")) return -1;
            else return 0;
        }

        if(isAITurn){
            int bestScore = Integer.MIN_VALUE;
            for(int i=0;i<9;i++){
                if(board[i]=='-'){
                    board[i]='O';
                    int score = minimax(board,false);
                    board[i]='-';
                    bestScore = Math.max(score,bestScore);
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for(int i=0;i<9;i++){
                if(board[i]=='-'){
                    board[i]='X';
                    int score = minimax(board,true);
                    board[i]='-';
                    bestScore = Math.min(score,bestScore);
                }
            }
            return bestScore;
        }
    }

    private String evaluate(char[] b){
        int[][] lines = {
            {0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}
        };
        for(int[] line:lines){
            if(b[line[0]]!='-' && b[line[0]]==b[line[1]] && b[line[1]]==b[line[2]])
                return String.valueOf(b[line[0]]);
        }
        for(char c:b) if(c=='-') return null; // moves left
        return "Draw";
    }

    private int findBestMove(String mark){
        int[][] lines = {
            {0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}
        };
        for(int[] line:lines){
            String a = buttons[line[0]].getText();
            String b = buttons[line[1]].getText();
            String c = buttons[line[2]].getText();
            if(a.equals(mark)&&b.equals(mark)&&c.equals("")) return line[2];
            if(a.equals(mark)&&c.equals(mark)&&b.equals("")) return line[1];
            if(b.equals(mark)&&c.equals(mark)&&a.equals("")) return line[0];
        }
        return -1;
    }

    private void checkWinner(){
        int[][] lines = {
            {0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}
        };
        for(int[] line:lines){
            String a = buttons[line[0]].getText();
            String b = buttons[line[1]].getText();
            String c = buttons[line[2]].getText();
            if(!a.equals("") && a.equals(b) && b.equals(c)){
                showWinner(a);
                return;
            }
        }
        boolean draw = true;
        for(JButton b:buttons) if(b.getText().equals("")) draw=false;
        if(draw) showWinner("Draw");
    }

    private void showWinner(String winner){
        String message = winner.equals("Draw")?"It's a draw!":winner+" wins!";
        int res = JOptionPane.showOptionDialog(this,message+"\nPlay again?",
                "Game Over",JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE,
                null,new String[]{"Yes","No"},"Yes");
        if(res==JOptionPane.YES_OPTION) resetBoard();
        else System.exit(0);
    }

    private void resetBoard(){
        for(JButton b:buttons) b.setText("");
        playerXTurn=true;
    }
}
