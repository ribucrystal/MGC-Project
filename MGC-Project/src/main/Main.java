package main;

import javax.swing.*;

public class Main extends JFrame {

    private DrawPanel panel;

    public Main() {
        setTitle("Jeu avec images");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new DrawPanel();
        add(panel);

        panel.setFocusable(true);
        panel.requestFocusInWindow();

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
