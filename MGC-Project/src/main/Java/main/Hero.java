package main.java.main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Hero {
    private BufferedImage image;
    private int x, y, size;
    private int maxHealth, currentHealth;
    private int attaque, defense, chance;
    private int[] statBonus;
    private int moveSpeed;
    private ArrayList<Point> trailPositions;
    private static final int MAX_TRAIL_LENGTH = 8;
    
    public Hero(BufferedImage image, int size, int attaque, int defense, int chance) {
        this.image = image;
        this.size = size;
        this.x = -1;
        this.y = -1;
        this.maxHealth = 50;
        this.currentHealth = 50;
        this.attaque = attaque;
        this.defense = defense;
        this.chance = chance;
        this.statBonus = new int[]{0, 0, 0};
        this.moveSpeed = Math.max(4, 12 - defense);
        this.trailPositions = new ArrayList<>();
    }
    
    public void move(int dx, int dy, int screenWidth, int screenHeight) {
        int oldX = x, oldY = y;
        x += dx;
        y += dy;
        
        x = Math.max(0, Math.min(x, screenWidth - size));
        y = Math.max(0, Math.min(y, screenHeight - size));
        
        if (oldX != x || oldY != y) {
            trailPositions.add(new Point(oldX, oldY));
            if (trailPositions.size() > MAX_TRAIL_LENGTH) {
                trailPositions.remove(0);
            }
        }
    }
    
    public void undoLastMove(int oldX, int oldY) {
        this.x = oldX;
        this.y = oldY;
        if (!trailPositions.isEmpty()) {
            trailPositions.remove(trailPositions.size() - 1);
        }
    }
    
    public Rectangle getFeetCollision() {
        int h = (int)(size * 0.2);
        return new Rectangle(x, y + size - h, size, h);
    }
    
    public void heal() {
        if (currentHealth < maxHealth) {
            currentHealth = maxHealth;
        }
    }
    
    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth < 0) currentHealth = 0;
    }
    
    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        this.currentHealth = maxHealth;
    }
    
    public void upgradeStat(int statIndex) {
        statBonus[statIndex]++;
        if (statIndex == 1) { // Defense
            moveSpeed = Math.max(4, 12 - (defense + statBonus[1]));
        }
    }
    
    public void draw(Graphics2D g2d, Component observer) {
        // Draw trail
        for (int i = 0; i < trailPositions.size(); i++) {
            Point p = trailPositions.get(i);
            float opacity = (float)(i + 1) / (trailPositions.size() + 1) * 0.4f;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            if (image != null) {
                g2d.drawImage(image, p.x, p.y, size, size, observer);
            } else {
                g2d.setColor(Color.BLUE);
                g2d.fillRect(p.x, p.y, size, size);
            }
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        
        // Draw hero
        if (image != null) {
            g2d.drawImage(image, x, y, size, size, observer);
        } else {
            g2d.setColor(Color.BLUE);
            g2d.fillRect(x, y, size, size);
        }
    }
    
    // Getters and setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }
    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttaque() { return attaque; }
    public int getDefense() { return defense; }
    public int getChance() { return chance; }
    public int[] getStatBonus() { return statBonus; }
    public int getMoveSpeed() { return moveSpeed; }
    
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setCurrentHealth(int health) { this.currentHealth = health; }
}