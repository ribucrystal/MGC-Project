package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class DrawPanel extends JPanel implements KeyListener, ActionListener {

    private Fond fond;
    private Hero hero;

    private final Set<Integer> keysPressed = new HashSet<>();
    private Timer timer;

    public DrawPanel() {
        fond = new Fond("images/arene.jpg");
        hero = new Hero("images/heros.png", 100, 100);

        addKeyListener(this);

        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = getWidth();
        int h = getHeight();

        fond.draw(g, w, h, this);
        hero.draw(g, w, h, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        hero.move(keysPressed, getWidth(), getHeight());
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysPressed.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // non utilisé
    }
}
