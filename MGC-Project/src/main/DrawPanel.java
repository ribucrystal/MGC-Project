package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class DrawPanel extends JPanel implements KeyListener, ActionListener {

    private Fond fond;
    private Hero hero;
    private Porte porte;
    private Pnj pnj;
    private boolean inArena;

    private final Set<Integer> keysPressed = new HashSet<>();
    private Timer timer;

    public DrawPanel() {
        fond = new Fond("images/sceneprincipale.png");
        hero = new Hero("images/herosvuedudessus.png", 100, 50);
        inArena = false;

        addKeyListener(this);
        setFocusable(true);

        timer = new Timer(16, this);
        timer.start();

        porte = new Porte("images/porte.png");
        pnj = new Pnj("images/PNJ.png");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        fond.draw(g, w, h, this);

        porte.draw(g, this);
        pnj.draw(g, this);

        hero.draw(g, w, h, this); // héros toujours au-dessus
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int dx = 0, dy = 0;
        int step = hero.getSTEP(); // ajouter un getter pour STEP dans Hero

        if (keysPressed.contains(KeyEvent.VK_LEFT)) dx -= step;
        if (keysPressed.contains(KeyEvent.VK_RIGHT)) dx += step;
        if (keysPressed.contains(KeyEvent.VK_UP)) dy -= step;
        if (keysPressed.contains(KeyEvent.VK_DOWN)) dy += step;

        Rectangle futureBounds = hero.getFutureBounds(dx, dy, getWidth(), getHeight());

        if (!futureBounds.intersects(porte.getBounds(this)) &&
            !futureBounds.intersects(pnj.getBounds(this))) {
            hero.move(keysPressed, getWidth(), getHeight());
        }

        if (!inArena && hero.getBounds(getWidth(), getHeight()).intersects(porte.getBounds(this))) {
            inArena = true;
            fond = new Fond("images/arene.jpg");
            hero.changeSprite("images/heros.png");
            hero.setPosition(hero.getX(), 100);
        }

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
    public void keyTyped(KeyEvent e) {}
}
