package main;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private DrawPanel panel;

    public Main() {
        setTitle("Jeu avec images");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.6);
        int height = (int) (screenSize.height * 0.6);

        setSize(width, height);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
