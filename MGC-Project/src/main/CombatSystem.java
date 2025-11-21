package main;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CombatSystem {
    private boolean inCombat;
    private int combatPhase; // 1=attaque, 2=défense, 3=chance
    private int playerRoll, bossRoll;
    private int damageDealt, damageBlocked, coinsGained;
    private String combatMessage;
    private Random rand;
    
    private BufferedImage[] diceImages;
    private int[] diceValues;
    private Color[] diceColors;
    private String[] diceLabels;
    private int diceSize;
    
    public CombatSystem(BufferedImage[] diceImages) {
        this.inCombat = false;
        this.combatPhase = 0;
        this.combatMessage = "";
        this.rand = new Random();
        
        this.diceImages = diceImages;
        this.diceValues = new int[3];
        this.diceSize = 80;
        this.diceColors = new Color[]{Color.RED, new Color(70, 130, 180), Color.GREEN};
        this.diceLabels = new String[]{"ATTAQUE", "DÉFENSE", "CHANCE"};
    }
    
    public void startCombat() {
        if (inCombat) return;
        inCombat = true;
        combatPhase = 1;
        combatMessage = "Phase ATTAQUE - Cliquez dé rouge !";
    }
    
    public void processCombatRoll(int diceIndex, Hero hero, Boss boss, GameStats stats) {
        if (!inCombat) return;
        
        if (combatPhase == 1 && diceIndex == 0) {
            processAttackPhase(hero, boss);
            if (boss.isDefeated()) {
                endCombatWithVictory(stats);
            } else {
                scheduleDefensePhase();
            }
        } else if (combatPhase == 2 && diceIndex == 1) {
            processDefensePhase(hero, boss);
            if (hero.getCurrentHealth() <= 0) {
                endCombatWithDefeat(hero, stats);
            } else {
                scheduleChancePhase();
            }
        } else if (combatPhase == 3 && diceIndex == 2) {
            processChancePhase(hero, stats);
        }
    }
    
    private void processAttackPhase(Hero hero, Boss boss) {
        playerRoll = rand.nextInt(6) + 1;
        diceValues[0] = playerRoll - 1;
        int totalAttaque = hero.getAttaque() + hero.getStatBonus()[0];
        
        if (playerRoll <= totalAttaque) {
            damageDealt = playerRoll;
            boss.takeDamage(damageDealt);
            combatMessage = "Attaque réussie ! -" + damageDealt + " PV au boss !";
        } else {
            combatMessage = "Attaque ratée ! (dé " + playerRoll + " > stat " + totalAttaque + ")";
            damageDealt = 0;
        }
    }
    
    private void processDefensePhase(Hero hero, Boss boss) {
        playerRoll = rand.nextInt(6) + 1;
        diceValues[1] = playerRoll - 1;
        
        bossRoll = rand.nextInt(6) + 1;
        int bossDamage = (bossRoll <= boss.getAttaque()) ? bossRoll : 0;
        
        int totalDefense = hero.getDefense() + hero.getStatBonus()[1];
        int blocked = (playerRoll <= totalDefense) ? Math.min(playerRoll, bossDamage) : 0;
        int finalDamage = Math.max(0, bossDamage - blocked);
        
        hero.takeDamage(finalDamage);
        
        if (bossDamage == 0) {
            combatMessage = "Le boss rate !";
        } else if (blocked > 0) {
            combatMessage = "Bloqué " + blocked + ", reçu -" + finalDamage + " PV";
        } else {
            combatMessage = "Défense ratée ! -" + finalDamage + " PV";
        }
    }
    
    private void processChancePhase(Hero hero, GameStats stats) {
        playerRoll = rand.nextInt(6) + 1;
        diceValues[2] = playerRoll - 1;
        int totalChance = hero.getChance() + hero.getStatBonus()[2];
        
        if (playerRoll <= totalChance && damageDealt > 0) {
            coinsGained = playerRoll;
            stats.addDrachmes(coinsGained);
            combatMessage = "+" + coinsGained + " Drachmes !";
        } else {
            combatMessage = "Pas de bonus.";
        }
        
        Timer t = new Timer(2000, e -> endCombatTurn());
        t.setRepeats(false);
        t.start();
    }
    
    private void scheduleDefensePhase() {
        combatPhase = 2;
        Timer t = new Timer(1500, e -> {
            combatMessage = "Phase DÉFENSE - Le boss attaque ! Dé bleu";
        });
        t.setRepeats(false);
        t.start();
    }
    
    private void scheduleChancePhase() {
        combatPhase = 3;
        Timer t = new Timer(1500, e -> {
            combatMessage = "Phase CHANCE - Dé vert !";
        });
        t.setRepeats(false);
        t.start();
    }
    
    private void endCombatWithVictory(GameStats stats) {
        combatMessage = "VICTOIRE ! +10 Drachmes";
        stats.addDrachmes(10);
        Timer t = new Timer(3000, e -> {
            inCombat = false;
            combatPhase = 0;
            combatMessage = "";
        });
        t.setRepeats(false);
        t.start();
    }
    
    private void endCombatWithDefeat(Hero hero, GameStats stats) {
        combatMessage = "DÉFAITE !";
        Timer t = new Timer(3000, e -> {
            stats.setCurrentRoom("fond1");
            hero.reset(stats.getScreenWidth() / 2, stats.getScreenHeight() / 2);
            inCombat = false;
            combatPhase = 0;
            combatMessage = "";
        });
        t.setRepeats(false);
        t.start();
    }
    
    private void endCombatTurn() {
        inCombat = false;
        combatPhase = 0;
        combatMessage = "";
        damageDealt = 0;
        damageBlocked = 0;
        coinsGained = 0;
    }
    
    public void drawCombatButton(Graphics2D g2d, int screenWidth, int screenHeight) {
        if (inCombat) return;
        
        int btnX = screenWidth / 2 - 100;
        int btnY = screenHeight - 250;
        
        g2d.setColor(new Color(200, 0, 0));
        g2d.fillRoundRect(btnX, btnY, 200, 60, 15, 15);
        g2d.setColor(Color.YELLOW);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(btnX, btnY, 200, 60, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("COMBATTRE", btnX + 25, btnY + 38);
    }
    
    public void drawCombatMessage(Graphics2D g2d, int screenWidth) {
        if (combatMessage.isEmpty()) return;
        
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(screenWidth / 2 - 250, 50, 500, 60, 15, 15);
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(combatMessage, screenWidth / 2 - fm.stringWidth(combatMessage) / 2, 85);
    }
    
    public void drawDice(Graphics2D g2d, Hero hero, int screenWidth, int screenHeight, Component observer) {
        int totalW = 200 * 3;
        int startX = (screenWidth - totalW) / 2;
        int diceY = screenHeight - 150;
        
        for (int i = 0; i < 3; i++) {
            int dx = startX + (i * 200) + 60;
            
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRoundRect(dx - 60, diceY - 20, 200, 140, 15, 15);
            g2d.setColor(diceColors[i]);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(dx - 60, diceY - 20, 200, 140, 15, 15);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(diceLabels[i], dx + 40 - fm.stringWidth(diceLabels[i]) / 2, diceY - 5);
            
            if (diceImages[diceValues[i]] != null) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2d.setColor(diceColors[i]);
                g2d.fillRect(dx, diceY + 20, diceSize, diceSize);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2d.drawImage(diceImages[diceValues[i]], dx, diceY + 20, diceSize, diceSize, observer);
            } else {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(dx, diceY + 20, diceSize, diceSize);
                g2d.setColor(diceColors[i]);
                g2d.drawRect(dx, diceY + 20, diceSize, diceSize);
                g2d.setFont(new Font("Arial", Font.BOLD, 40));
                g2d.drawString(String.valueOf(diceValues[i] + 1), dx + 25, diceY + 75);
            }
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            int stat = (i == 0) ? hero.getAttaque() + hero.getStatBonus()[0] :
                      (i == 1) ? hero.getDefense() + hero.getStatBonus()[1] :
                      hero.getChance() + hero.getStatBonus()[2];
            g2d.drawString("Stat: " + stat, dx + 10, diceY + 115);
        }
    }
    
    public Rectangle getCombatButtonBounds(int screenWidth, int screenHeight) {
        int btnX = screenWidth / 2 - 100;
        int btnY = screenHeight - 250;
        return new Rectangle(btnX, btnY, 200, 60);
    }
    
    public Rectangle getDiceBounds(int diceIndex, int screenWidth, int screenHeight) {
        int totalW = 200 * 3;
        int startX = (screenWidth - totalW) / 2;
        int diceY = screenHeight - 150;
        int dx = startX + (diceIndex * 200) + 60;
        return new Rectangle(dx, diceY + 20, diceSize, diceSize);
    }
    
    public void setDiceValues(int attackValue, int defenseValue, int chanceValue) {
        diceValues[0] = attackValue - 1;
        diceValues[1] = defenseValue - 1;
        diceValues[2] = chanceValue - 1;
    }
    
    // Getters
    public boolean isInCombat() { return inCombat; }
    public int getCombatPhase() { return combatPhase; }
    public String getCombatMessage() { return combatMessage; }
}