package main.java.main;

import java.awt.*;

public class GameStats {
    private int drachmes;
    private String currentRoom;
    private int screenWidth;
    private int screenHeight;
    
    public GameStats() {
        this.drachmes = 1;
        this.currentRoom = "fond1";
    }
    
    public void addDrachmes(int amount) {
        drachmes += amount;
    }
    
    public boolean spendDrachmes(int amount) {
        if (drachmes >= amount) {
            drachmes -= amount;
            return true;
        }
        return false;
    }
    
    public void drawHUD(Graphics2D g2d, Hero hero) {
        // Main box
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(10, 10, 180, 80, 15, 15);
        g2d.setColor(new Color(255, 215, 0));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(10, 10, 180, 80, 15, 15);
        
        // Drachmes
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString(drachmes + " Dr", 25, 35);
        
        // Health bar
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("VIE:", 25, 55);
        
        int hpW = 140, hpH = 18, hpX = 25, hpY = 60;
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(hpX, hpY, hpW, hpH);
        
        float hpPercent = (float) hero.getCurrentHealth() / hero.getMaxHealth();
        g2d.setColor(hpPercent > 0.5f ? Color.GREEN : 
                    hpPercent > 0.25f ? Color.ORANGE : Color.RED);
        g2d.fillRect(hpX, hpY, (int)(hpW * hpPercent), hpH);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String hpText = hero.getCurrentHealth() + "/" + hero.getMaxHealth();
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(hpText, hpX + (hpW - fm.stringWidth(hpText)) / 2, hpY + 13);
    }
    
    // Getters and setters
    public int getDrachmes() { return drachmes; }
    public String getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(String room) { this.currentRoom = room; }
    public int getScreenWidth() { return screenWidth; }
    public void setScreenWidth(int width) { this.screenWidth = width; }
    public int getScreenHeight() { return screenHeight; }
    public void setScreenHeight(int height) { this.screenHeight = height; }
}