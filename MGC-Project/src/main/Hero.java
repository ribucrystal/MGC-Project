package main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Set;
import javax.swing.ImageIcon;

public class Hero {
    private Image playerImage;
    private int x, y;
    private double scale = 1.0;
    private boolean facingLeft = false;
    private final int STEP = 5;

    // Animation
    private double walkPhase = 0;
    private boolean moving = false;

    public Hero(String path, int startX, int startY) {
        playerImage = new ImageIcon(path).getImage();
        x = startX;
        y = startY;
    }

    //  Permet de changer l’image sans recréer le héros
    public void changeSprite(String path) {
        playerImage = new ImageIcon(path).getImage();
    }

    public void setPosition(int newX, int newY) {
        x = newX;
        y = newY;
    }

    public void draw(Graphics g, int panelWidth, int panelHeight, Component observer) {
        int baseWidth = panelWidth / 10;
        int playerWidth = (int)(baseWidth * scale);
        double aspectRatio = (double) playerImage.getHeight(observer) / playerImage.getWidth(observer);
        int playerHeight = (int)(playerWidth * aspectRatio);

        Graphics2D g2d = (Graphics2D) g.create();

        int offsetY = 0;
        if (moving) {
            walkPhase += 0.2;
            offsetY = (int) (Math.sin(walkPhase) * 3);
        } else {
            walkPhase = 0;
        }

        double legSwing = moving ? Math.sin(walkPhase * 2) * 2 : 0;

        int legStart = (int)(playerHeight * 0.9);
        int legHeight = playerHeight - legStart;

        if (facingLeft) {
            g2d.translate(x + playerWidth, y + offsetY);
            g2d.scale(-1, 1);
        } else {
            g2d.translate(x, y + offsetY);
        }

        // Haut du corps
        g2d.drawImage(
            playerImage,
            0, 0, playerWidth, legStart,
            0, 0, playerImage.getWidth(null), (int)(playerImage.getHeight(null) * 0.8),
            observer
        );

        // Bas du corps (animé)
        g2d.drawImage(
            playerImage,
            (int) legSwing, legStart, playerWidth, playerHeight,
            0, (int)(playerImage.getHeight(null) * 0.8), playerImage.getWidth(null), playerImage.getHeight(null),
            observer
        );

        g2d.dispose();
    }

    public void move(Set<Integer> keysPressed, int panelWidth, int panelHeight) {
        int dx = 0, dy = 0;

        int baseWidth = panelWidth / 10;
        double aspectRatio = (double) playerImage.getHeight(null) / playerImage.getWidth(null);
        int playerWidth = (int)(baseWidth * scale);
        int playerHeight = (int)(playerWidth * aspectRatio);

        int marginX = panelWidth / 10;
        int marginY = panelHeight / 10;

        int zoneMinX = marginX;
        int zoneMaxX = panelWidth - marginX - playerWidth;
        int zoneMinY = marginY;
        int zoneMaxY = panelHeight - marginY - playerHeight;

        moving = false;

        if (keysPressed.contains(KeyEvent.VK_LEFT)) {
            dx -= STEP;
            facingLeft = true;
            moving = true;
        }
        if (keysPressed.contains(KeyEvent.VK_RIGHT)) {
            dx += STEP;
            facingLeft = false;
            moving = true;
        }
        if (keysPressed.contains(KeyEvent.VK_UP)) {
            dy -= STEP;
            moving = true;
        }
        if (keysPressed.contains(KeyEvent.VK_DOWN)) {
            dy += STEP;
            moving = true;
        }

        int newX = x + dx;
        int newY = y + dy;

        if (newX < zoneMinX) newX = zoneMinX;
        if (newX > zoneMaxX) newX = zoneMaxX;
        if (newY < zoneMinY) newY = zoneMinY;
        if (newY > zoneMaxY) newY = zoneMaxY;

        x = newX;
        y = newY;
    }
    
    public Rectangle getBounds(int panelWidth, int panelHeight) {
        int baseWidth = panelWidth / 10;
        double aspectRatio = (double) playerImage.getHeight(null) / playerImage.getWidth(null);
        int playerWidth = (int)(baseWidth * scale);
        int playerHeight = (int)(playerWidth * aspectRatio);
        return new Rectangle(x, y, playerWidth, playerHeight);
    }
    
    public int getSTEP() {
        return STEP;
    }

    public Rectangle getFutureBounds(int dx, int dy, int panelWidth, int panelHeight) {
        int baseWidth = panelWidth / 10;
        double aspectRatio = (double) playerImage.getHeight(null) / playerImage.getWidth(null);
        int playerWidth = (int)(baseWidth * scale);
        int playerHeight = (int)(playerWidth * aspectRatio);

        int newX = x + dx;
        int newY = y + dy;

        if (newX < 0) newX = 0;
        if (newX + playerWidth > panelWidth) newX = panelWidth - playerWidth;
        if (newY < 0) newY = 0;
        if (newY + playerHeight > panelHeight) newY = panelHeight - playerHeight;

        return new Rectangle(newX, newY, playerWidth, playerHeight);
    }




    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
}
