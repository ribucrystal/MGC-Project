package main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Set;
import javax.swing.ImageIcon;

public class Hero {
    private Image playerImage;

    private int x;
    private int y;

    private double scale = 1.0;
    private boolean facingLeft = false;

    private final int STEP = 5;

    public Hero(String path, int startX, int startY) {
        playerImage = new ImageIcon(path).getImage();
        x = startX;
        y = startY;
    }

    public void draw(Graphics g, int panelWidth, int panelHeight, Component observer) {
        int baseWidth = panelWidth / 10;
        int playerWidth = (int)(baseWidth * scale);
        double aspectRatio = (double) playerImage.getHeight(observer) / playerImage.getWidth(observer);
        int playerHeight = (int)(playerWidth * aspectRatio);

        Graphics2D g2d = (Graphics2D) g.create();

        if (facingLeft) {
            g2d.translate(x + playerWidth, y);
            g2d.scale(-1, 1);
            g2d.drawImage(playerImage, 0, 0, playerWidth, playerHeight, observer);
        } else {
            g2d.drawImage(playerImage, x, y, playerWidth, playerHeight, observer);
        }

        g2d.dispose();
    }

    public void move(Set<Integer> keysPressed, int panelWidth, int panelHeight) {
        int dx = 0, dy = 0;

        int baseWidth = panelWidth / 10;
        double aspectRatio = (double) playerImage.getHeight(null) / playerImage.getWidth(null);
        int playerWidth = (int)(baseWidth * scale);
        int playerHeight = (int)(playerWidth * aspectRatio);

        // Zone verticale autorisée (4ème 1/5)
        int zoneMinY = (int)(panelHeight * 3.0 / 5.0);
        int zoneMaxY = (int)(panelHeight * 4.0 / 5.0) - playerHeight;

        if (keysPressed.contains(KeyEvent.VK_LEFT)) {
            dx -= STEP;
            facingLeft = true;
        }
        if (keysPressed.contains(KeyEvent.VK_RIGHT)) {
            dx += STEP;
            facingLeft = false;
        }

        if (keysPressed.contains(KeyEvent.VK_UP)) {
            if (y - STEP >= zoneMinY) {
                dy -= STEP;
                scale -= 0.02;
                if (scale < 0.5) scale = 0.5;
            }
        }
        if (keysPressed.contains(KeyEvent.VK_DOWN)) {
            if (y + STEP <= zoneMaxY) {
                dy += STEP;
                scale += 0.02;
                if (scale > 2.0) scale = 2.0;
            }
        }

        int newX = x + dx;
        int newY = y + dy;

        // Limites
        if (newX < 0) newX = 0;
        if (newX + playerWidth > panelWidth) newX = panelWidth - playerWidth;

        if (newY < zoneMinY) newY = zoneMinY;
        if (newY > zoneMaxY) newY = zoneMaxY;

        x = newX;
        y = newY;
    }
}
