package main;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class UpgradeSystem {
    private BufferedImage deesseImage;
    private int deesseX, deesseY, deesseSize;
    private boolean nearDeesse;
    private boolean showUpgradeMenu;
    private int selectedStat;
    private String upgradeMessage;
    private Timer upgradeMessageTimer;
    private Color[] diceColors;
    
    public UpgradeSystem(BufferedImage deesseImage, int deesseSize) {
        this.deesseImage = deesseImage;
        this.deesseSize = deesseSize;
        this.deesseX = 1000;
        this.deesseY = 100;
        this.nearDeesse = false;
        this.showUpgradeMenu = false;
        this.selectedStat = 0;
        this.upgradeMessage = "";
        this.diceColors = new Color[]{Color.RED, new Color(70, 130, 180), Color.GREEN};
    }
    
    public void checkProximity(Hero hero) {
        double distance = Math.sqrt(
            Math.pow(hero.getX() - deesseX, 2) + 
            Math.pow(hero.getY() - deesseY, 2)
        );
        nearDeesse = distance < 120;
    }
    
    public void openMenu() {
        if (nearDeesse && !showUpgradeMenu) {
            showUpgradeMenu = true;
            selectedStat = 0;
        }
    }
    
    public void closeMenu() {
        showUpgradeMenu = false;
    }
    
    public void selectPrevious() {
        if (showUpgradeMenu) {
            selectedStat = (selectedStat - 1 + 3) % 3;
        }
    }
    
    public void selectNext() {
        if (showUpgradeMenu) {
            selectedStat = (selectedStat + 1) % 3;
        }
    }
    
    public boolean purchaseUpgrade(Hero hero, GameStats stats) {
        if (!showUpgradeMenu) return false;
        
        if (stats.spendDrachmes(3)) {
            hero.upgradeStat(selectedStat);
            
            String[] statNames = {"ATTAQUE +1 !", "DÉFENSE +1 !", "CHANCE +1 !"};
            upgradeMessage = statNames[selectedStat];
            
            if (upgradeMessageTimer != null) {
                upgradeMessageTimer.stop();
            }
            upgradeMessageTimer = new Timer(2000, e -> {
                upgradeMessage = "";
            });
            upgradeMessageTimer.setRepeats(false);
            upgradeMessageTimer.start();
            
            showUpgradeMenu = false;
            return true;
        } else {
            upgradeMessage = "Pas assez de Drachmes !";
            if (upgradeMessageTimer != null) {
                upgradeMessageTimer.stop();
            }
            upgradeMessageTimer = new Timer(2000, e -> {
                upgradeMessage = "";
            });
            upgradeMessageTimer.setRepeats(false);
            upgradeMessageTimer.start();
            return false;
        }
    }
    
    public void draw(Graphics2D g2d, Hero hero, GameStats stats, int screenWidth, int screenHeight, Component observer) {
        if (deesseX == -1) {
            deesseX = screenWidth - deesseSize - 20;
        }
        
        // Dessiner la déesse
        if (deesseImage != null) {
            g2d.drawImage(deesseImage, deesseX, deesseY, deesseSize, deesseSize, observer);
        } else {
            g2d.setColor(new Color(255, 215, 0));
            g2d.fillRect(deesseX, deesseY, deesseSize, deesseSize);
        }
        
        // Bulle d'information si proche
        if (nearDeesse && !showUpgradeMenu) {
            g2d.setColor(new Color(255, 255, 255, 240));
            int bx = deesseX - 100, by = deesseY - 80;
            g2d.fillRoundRect(bx, by, 280, 70, 20, 20);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(bx, by, 280, 70, 20, 20);
            g2d.setColor(new Color(100, 50, 150));
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("ENTRÉE: 3Dr = +1 stat", bx + 15, by + 40);
        }
        
        // Menu d'amélioration
        if (showUpgradeMenu) {
            drawUpgradeMenu(g2d, hero, stats, screenWidth, screenHeight);
        }
        
        // Message d'amélioration
        if (!upgradeMessage.isEmpty()) {
            g2d.setColor(new Color(0, 255, 0));
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(upgradeMessage, 
                screenWidth / 2 - fm.stringWidth(upgradeMessage) / 2, 
                screenHeight / 2 - 200);
        }
    }
    
    private void drawUpgradeMenu(Graphics2D g2d, Hero hero, GameStats stats, int screenWidth, int screenHeight) {
        int menuX = screenWidth / 2 - 200;
        int menuY = screenHeight / 2 - 150;
        
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(menuX, menuY, 400, 300, 20, 20);
        g2d.setColor(new Color(255, 215, 0));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRoundRect(menuX, menuY, 400, 300, 20, 20);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("Bénédiction Divine", menuX + 70, menuY + 40);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Coût: 3Dr (Vous: " + stats.getDrachmes() + "Dr)", 
            menuX + 60, menuY + 80);
        
        String[] options = {"ATTAQUE", "DÉFENSE", "CHANCE"};
        int[] stats_values = {
            hero.getAttaque() + hero.getStatBonus()[0],
            hero.getDefense() + hero.getStatBonus()[1],
            hero.getChance() + hero.getStatBonus()[2]
        };
        
        for (int i = 0; i < 3; i++) {
            int optionY = menuY + 120 + (i * 60);
            
            if (i == selectedStat) {
                g2d.setColor(new Color(255, 215, 0, 100));
                g2d.fillRoundRect(menuX + 20, optionY - 25, 360, 45, 10, 10);
            }
            
            g2d.setColor(diceColors[i]);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString(options[i] + ": " + stats_values[i], menuX + 40, optionY);
            
            if (hero.getStatBonus()[i] > 0) {
                g2d.setColor(Color.GREEN);
                g2d.setFont(new Font("Arial", Font.BOLD, 18));
                g2d.drawString("(+" + hero.getStatBonus()[i] + ")", menuX + 220, optionY);
            }
        }
        
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.drawString("ENTRÉE acheter | ÉCHAP annuler", 
            menuX + 40, menuY + 270);
    }
    
    // Getters
    public boolean isNearDeesse() { return nearDeesse; }
    public boolean isShowUpgradeMenu() { return showUpgradeMenu; }
    public int getSelectedStat() { return selectedStat; }
}