package main;

import java.awt.*;
import javax.swing.*;

public class Porte {
    private Image porteImage;

    public Porte(String path) {
        porteImage = new ImageIcon(path).getImage();
    }

    public void draw(Graphics g, Component observer) {
        int originalWidth = porteImage.getWidth(null);
        int originalHeight = porteImage.getHeight(null);

        int width = (int)(originalWidth * 0.5);
        int height = (int)(originalHeight * 0.5);

        int x = observer.getWidth() - width - 20;
        int y = 20;

        g.drawImage(porteImage, x, y, width, height, observer);
    }

    public Rectangle getBounds(Component observer) {
        int width = (int)(porteImage.getWidth(null) * 0.5);
        int height = (int)(porteImage.getHeight(null) * 0.5);

        int x = observer.getWidth() - width - 20;
        int y = 20;

        return new Rectangle(x, y, width, height);
    }
}
