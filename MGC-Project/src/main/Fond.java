package main;

import java.awt.*;
import javax.swing.*;

public class Fond {
    private Image backgroundImage;

    public Fond(String path) {
        backgroundImage = new ImageIcon(path).getImage();
    }

    public void draw(Graphics g, int width, int height, Component observer) {
        g.drawImage(backgroundImage, 0, 0, width, height, observer);
    }
}
