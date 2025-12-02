package main.java.main;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Boss {
    private BufferedImage image;
    private int x, y, size;
    private int maxHealth, currentHealth;
    private int attaque, defense;
    private boolean defeated;
    private boolean exists;
    private Random rand;
    
    public Boss(int size) {
        this.size = size;
        this.x = -1;
        this.y = -1;
        this.defeated = false;
        this.exists = false;
        this.rand = new Random();
    }
    
    public void spawn(BufferedImage boss1Image, BufferedImage boss2Image) {
        this.image = rand.nextBoolean() ? boss1Image : boss2Image;
        this.maxHealth = 30 + rand.nextInt(41);
        this.currentHealth = maxHealth;
        this.attaque = 3 + rand.nextInt(4);
        this.defense = 2 + rand.nextInt(4);
        this.x = -1;
        this.y = -1;
        this.exists = true;
        this.defeated = false;
        System.out.println("Boss: HP=" + maxHealth + " ATK=" + attaque + " DEF=" + defense);
    }
    
    public void reset() {
        if (exists && !defeated) {
            currentHealth = maxHealth;
        }
    }
    
    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            defeated = true;
            exists = false;
        }
    }
    
    public int rollAttack() {
        int roll = rand.nextInt(6) + 1;
        return (roll <= attaque) ? roll : 0;
    }
    
    public void draw(Graphics2D g2d, Component observer, int screenWidth, int screenHeight) {
        if (!exists || defeated) return;
        
        if (x == -1) {
            x = screenWidth / 2 - size / 2;
            y = screenHeight / 2 - size / 2;
        }
        
        // Draw boss
        if (image != null) {
            g2d.drawImage(image, x, y, size, size, observer);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillRect(x, y, size, size);
        }
        
        // Health bar
        int barW = 200, barH = 20;
        int barX = x + (size - barW) / 2;
        int barY = y - 30;
        
        g2d.setColor(Color.BLACK);
        g2d.fillRect(barX - 2, barY - 2, barW + 4, barH + 4);
        g2d.setColor(Color.RED);
        g2d.fillRect(barX, barY, barW, barH);
        
        float hpPercent = (float) currentHealth / maxHealth;
        g2d.setColor(Color.GREEN);
        g2d.fillRect(barX, barY, (int)(barW * hpPercent), barH);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String hpText = currentHealth + "/" + maxHealth;
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(hpText, barX + (barW - fm.stringWidth(hpText)) / 2, barY + 14);
    }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }
    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttaque() { return attaque; }
    public int getDefense() { return defense; }
    public boolean isDefeated() { return defeated; }
    public boolean exists() { return exists; }
    
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}