package main.java.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Main extends JPanel implements KeyListener {
    // Game components
    private Hero hero;
    private Boss boss;
    private CombatSystem combatSystem;
    private GameStats gameStats;
    private UpgradeSystem upgradeSystem;
    private CollisionEditor collisionEditor;
    private GameWorld gameWorld;
    
    // Images
    private BufferedImage[] diceImages = new BufferedImage[6];
    
    // Controls
    private Set<Integer> pressedKeys = new HashSet<>();
    private Timer moveTimer;
    
    public Main() {
        Random rand = new Random();
        
        // Load images
        BufferedImage heroImage = loadImage("herosvuedudessus.png");
        BufferedImage deesseImage = loadImage("deesse.png");
        BufferedImage boss1Image = loadImage("boss1.png");
        BufferedImage boss2Image = loadImage("boss2.png");
        BufferedImage fond1Image = loadImage("fond1.png");
        BufferedImage fond2Image = loadImage("fond2.png");
        
        for (int i = 0; i < 6; i++) {
            diceImages[i] = loadImage("Des_" + (i + 1) + ".png");
        }
        
        // Initialize random stats
        int attaque = rand.nextInt(6) + 1;
        int defense = rand.nextInt(6) + 1;
        int chance = rand.nextInt(6) + 1;
        
        System.out.println("=== Stats ===\nATK:" + attaque + " DEF:" + defense + " CHC:" + chance);
        
        // Create components
        hero = new Hero(heroImage, 80, attaque, defense, chance);
        boss = new Boss(300);
        combatSystem = new CombatSystem(diceImages);
        combatSystem.setDiceValues(attaque, defense, chance);
        gameStats = new GameStats();
        upgradeSystem = new UpgradeSystem(deesseImage, 80);
        collisionEditor = new CollisionEditor(gameStats.getCurrentRoom());
        gameWorld = new GameWorld(fond1Image, fond2Image);
        
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(new GameMouseListener());
        addMouseListener(collisionEditor);
        addMouseMotionListener(collisionEditor);
    }
    
    private BufferedImage loadImage(String filename) {
        try {
            return ImageIO.read(new File(filename));
        } catch (Exception e) {
            System.out.println("Error loading " + filename + ": " + e.getMessage());
            return null;
        }
    }
    
    private void checkRoomTransition() {
        int width = getWidth();
        String currentRoom = gameStats.getCurrentRoom();
        
        if (currentRoom.equals("fond1") && hero.getX() + hero.getSize() >= width - 5) {
            gameStats.setCurrentRoom("fond2");
            hero.setX(10);
            collisionEditor.setCurrentRoom("fond2");
            
            if (!boss.exists() || boss.isDefeated()) {
                spawnNewBoss();
            } else {
                boss.reset();
            }
        } else if (currentRoom.equals("fond2") && hero.getX() <= 5) {
            gameStats.setCurrentRoom("fond1");
            hero.setX(width - hero.getSize() - 10);
            collisionEditor.setCurrentRoom("fond1");
            boss.reset();
        }
        repaint();
    }
    
    private void spawnNewBoss() {
        BufferedImage boss1Image = loadImage("boss1.png");
        BufferedImage boss2Image = loadImage("boss2.png");
        boss.spawn(boss1Image, boss2Image);
    }
    
    private void checkHealing() {
        if (gameStats.getCurrentRoom().equals("fond1") && gameWorld.isInHealingZone(hero)) {
            hero.heal();
        }
    }
    
    private void updateHeroPosition() {
        int oldX = hero.getX();
        int oldY = hero.getY();
        int dx = 0, dy = 0;
        
        if (pressedKeys.contains(KeyEvent.VK_UP)) dy -= hero.getMoveSpeed();
        if (pressedKeys.contains(KeyEvent.VK_DOWN)) dy += hero.getMoveSpeed();
        if (pressedKeys.contains(KeyEvent.VK_LEFT)) dx -= hero.getMoveSpeed();
        if (pressedKeys.contains(KeyEvent.VK_RIGHT)) dx += hero.getMoveSpeed();
        
        if (dx != 0 || dy != 0) {
            hero.move(dx, dy, getWidth(), getHeight());
            checkRoomTransition();
            checkHealing();
            
            if (collisionEditor.checkCollision(hero.getFeetCollision())) {
                hero.undoLastMove(oldX, oldY);
            }
        }
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Update dimensions
        gameStats.setScreenWidth(getWidth());
        gameStats.setScreenHeight(getHeight());
        gameWorld.updateHealingZonePosition(getWidth(), getHeight());
        
        // Initialize hero position if needed
        if (hero.getX() == -1) {
            hero.setX((getWidth() - hero.getSize()) / 2);
            hero.setY((getHeight() - hero.getSize()) / 2);
        }
        
        // Draw world
        gameWorld.drawBackground(g2d, gameStats.getCurrentRoom(), getWidth(), getHeight(), this);
        
        // Editor mode
        if (collisionEditor.isEditorMode()) {
            collisionEditor.draw(g2d);
        }
        
        // Draw hero
        hero.draw(g2d, this);
        
        // fond1 area
        if (gameStats.getCurrentRoom().equals("fond1")) {
            gameWorld.drawHealingZone(g2d);
            upgradeSystem.checkProximity(hero);
            upgradeSystem.draw(g2d, hero, gameStats, getWidth(), getHeight(), this);
        }
        
        // fond2 area (combat)
        if (gameStats.getCurrentRoom().equals("fond2")) {
            if (boss.exists() && !boss.isDefeated()) {
                boss.draw(g2d, this, getWidth(), getHeight());
                combatSystem.drawCombatButton(g2d, getWidth(), getHeight());
                combatSystem.drawCombatMessage(g2d, getWidth());
            }
            combatSystem.drawDice(g2d, hero, getWidth(), getHeight(), this);
        }
        
        // HUD
        gameStats.drawHUD(g2d, hero);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        // Upgrade menu
        if (upgradeSystem.isShowUpgradeMenu()) {
            if (key == KeyEvent.VK_UP) {
                upgradeSystem.selectPrevious();
                repaint();
                return;
            } else if (key == KeyEvent.VK_DOWN) {
                upgradeSystem.selectNext();
                repaint();
                return;
            } else if (key == KeyEvent.VK_ENTER) {
                upgradeSystem.purchaseUpgrade(hero, gameStats);
                repaint();
                return;
            } else if (key == KeyEvent.VK_ESCAPE) {
                upgradeSystem.closeMenu();
                repaint();
                return;
            }
        }
        
        // Open upgrade menu
        if (key == KeyEvent.VK_ENTER && upgradeSystem.isNearDeesse() && 
            !upgradeSystem.isShowUpgradeMenu() && !collisionEditor.isEditorMode()) {
            upgradeSystem.openMenu();
            repaint();
            return;
        }
        
        // Editor mode
        if (key == KeyEvent.VK_E) {
            collisionEditor.toggleEditor();
            repaint();
            return;
        }
        
        // Movement
        if (!collisionEditor.isEditorMode() && !upgradeSystem.isShowUpgradeMenu() &&
            (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN || 
             key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT)) {
            pressedKeys.add(key);
            
            if (pressedKeys.size() == 1 && (moveTimer == null || !moveTimer.isRunning())) {
                moveTimer = new Timer(16, evt -> {
                    if (pressedKeys.isEmpty()) {
                        moveTimer.stop();
                    } else {
                        updateHeroPosition();
                    }
                });
                moveTimer.start();
            }
            updateHeroPosition();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    // Mouse listener
    private class GameMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            // Combat button
            if (gameStats.getCurrentRoom().equals("fond2") && !combatSystem.isInCombat() && 
                boss.exists() && !boss.isDefeated()) {
                Rectangle combatBtn = combatSystem.getCombatButtonBounds(getWidth(), getHeight());
                if (combatBtn.contains(e.getPoint())) {
                    combatSystem.startCombat();
                    repaint();
                    return;
                }
            }
            
            // Click on dice
            if (gameStats.getCurrentRoom().equals("fond2") && combatSystem.isInCombat()) {
                for (int i = 0; i < 3; i++) {
                    Rectangle diceBounds = combatSystem.getDiceBounds(i, getWidth(), getHeight());
                    if (diceBounds.contains(e.getPoint())) {
                        combatSystem.processCombatRoll(i, hero, boss, gameStats);
                        repaint();
                        return;
                    }
                }
            }
        }
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Jeu du Heros");
        Main game = new Main();
        frame.add(game);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}