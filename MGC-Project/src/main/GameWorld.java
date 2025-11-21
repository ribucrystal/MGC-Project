package main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GameWorld {
    private BufferedImage fond1Image;
    private BufferedImage fond2Image;
    private Rectangle healingZone;
    
    public GameWorld(BufferedImage fond1Image, BufferedImage fond2Image) {
        this.fond1Image = fond1Image;
        this.fond2Image = fond2Image;
        this.healingZone = new Rectangle(0, 0, 150, 150);
    }
    
    public void updateHealingZonePosition(int screenWidth, int screenHeight) {
        healingZone.x = (screenWidth - healingZone.width) / 2;
        healingZone.y = (screenHeight - healingZone.height) / 2;
    }
    
    public boolean isInHealingZone(Hero hero) {
        return healingZone.intersects(hero.getX(), hero.getY(), hero.getSize(), hero.getSize());
    }
    
    public void drawBackground(Graphics2D g2d, String currentRoom, int screenWidth, int screenHeight, Component observer) {
        BufferedImage background = currentRoom.equals("fond1") ? fond1Image : fond2Image;
        
        if (background != null) {
            g2d.drawImage(background, 0, 0, screenWidth, screenHeight, observer);
        } else {
            g2d.setColor(currentRoom.equals("fond1") ? 
                new Color(34, 139, 34) : 
                new Color(60, 30, 30));
            g2d.fillRect(0, 0, screenWidth, screenHeight);
        }
    }
    
    public void drawHealingZone(Graphics2D g2d) {
        g2d.setColor(new Color(0, 255, 0, 100));
        g2d.fillRect(healingZone.x, healingZone.y, healingZone.width, healingZone.height);
        g2d.setColor(Color.GREEN);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(healingZone.x, healingZone.y, healingZone.width, healingZone.height);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("SOIN", healingZone.x + 45, healingZone.y + 80);
    }
    
    // Getters
    public Rectangle getHealingZone() { return healingZone; }
}