package main;

import java.awt.*;
import javax.swing.*;

public class Pnj {
    private Image pnjImage;

    public Pnj(String path) {
        pnjImage = new ImageIcon(path).getImage();
    }

    public void draw(Graphics g, Component observer) {
        int originalWidth = pnjImage.getWidth(null);
        int originalHeight = pnjImage.getHeight(null);

        int width = (int)(originalWidth * 0.2); // un peu plus petit
        int height = (int)(originalHeight * 0.2);

        int x = 100; // en haut à gauche
        int y = 20;

        g.drawImage(pnjImage, x, y, width, height, observer);
    }

    public Rectangle getBounds(Component observer) {
        int width = (int)(pnjImage.getWidth(null) * 0.2);
        int height = (int)(pnjImage.getHeight(null) * 0.2);

        int x = 20;
        int y = 20;

        return new Rectangle(x, y, width, height);
    }
}
