package main.java.main;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class CollisionEditor implements MouseListener, MouseMotionListener {
    private boolean editorMode;
    private ArrayList<Rectangle> collisionZonesFond1;
    private ArrayList<Rectangle> collisionZonesFond2;
    private Point dragStart;
    private Rectangle currentRect;
    private Rectangle selectedZone;
    private String currentRoom;
    
    private static final String COLLISION_FILE_FOND1 = "collisions_fond1.dat";
    private static final String COLLISION_FILE_FOND2 = "collisions_fond2.dat";
    
    public CollisionEditor(String currentRoom) {
        this.editorMode = false;
        this.collisionZonesFond1 = new ArrayList<>();
        this.collisionZonesFond2 = new ArrayList<>();
        this.currentRoom = currentRoom;
        loadCollisions();
    }
    
    public void toggleEditor() {
        editorMode = !editorMode;
        if (!editorMode) {
            saveCollisions();
        }
    }
    
    public ArrayList<Rectangle> getCurrentCollisionZones() {
        return currentRoom.equals("fond1") ? collisionZonesFond1 : collisionZonesFond2;
    }
    
    public void setCurrentRoom(String room) {
        this.currentRoom = room;
    }
    
    public boolean checkCollision(Rectangle heroFeet) {
        for (Rectangle zone : getCurrentCollisionZones()) {
            if (heroFeet.intersects(zone)) {
                return true;
            }
        }
        return false;
    }
    
    private void loadCollisions() {
        try {
            File f1 = new File(COLLISION_FILE_FOND1);
            if (f1.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f1));
                collisionZonesFond1 = (ArrayList<Rectangle>) ois.readObject();
                ois.close();
                System.out.println("Collisions fond1: " + collisionZonesFond1.size());
            }
        } catch (Exception e) {
            System.out.println("Error loading fond1: " + e.getMessage());
        }
        
        try {
            File f2 = new File(COLLISION_FILE_FOND2);
            if (f2.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f2));
                collisionZonesFond2 = (ArrayList<Rectangle>) ois.readObject();
                ois.close();
                System.out.println("Collisions fond2: " + collisionZonesFond2.size());
            }
        } catch (Exception e) {
            System.out.println("Error loading fond2: " + e.getMessage());
        }
    }
    
    private void saveCollisions() {
        String file = currentRoom.equals("fond1") ? COLLISION_FILE_FOND1 : COLLISION_FILE_FOND2;
        ArrayList<Rectangle> zones = getCurrentCollisionZones();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(zones);
            oos.close();
            System.out.println("Saved " + currentRoom + ": " + zones.size() + " zones");
        } catch (Exception e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }
    
    public void draw(Graphics2D g2d) {
        if (!editorMode) return;
        
        g2d.setStroke(new BasicStroke(2));
        
        // Draw existing zones
        for (Rectangle zone : getCurrentCollisionZones()) {
            if (zone == selectedZone) {
                g2d.setColor(new Color(255, 0, 0, 100));
                g2d.fillRect(zone.x, zone.y, zone.width, zone.height);
                g2d.setColor(Color.RED);
            } else {
                g2d.setColor(new Color(255, 255, 0, 50));
                g2d.fillRect(zone.x, zone.y, zone.width, zone.height);
                g2d.setColor(Color.YELLOW);
            }
            g2d.drawRect(zone.x, zone.y, zone.width, zone.height);
        }
        
        // Draw zone being created
        if (currentRect != null) {
            g2d.setColor(new Color(0, 255, 0, 50));
            g2d.fillRect(currentRect.x, currentRect.y, currentRect.width, currentRect.height);
            g2d.setColor(Color.GREEN);
            g2d.drawRect(currentRect.x, currentRect.y, currentRect.width, currentRect.height);
        }
        
        // Display information
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(5, 5, 350, 90);
        g2d.setColor(Color.CYAN);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("EDITEUR - " + currentRoom.toUpperCase(), 15, 25);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Clic gauche: creer | Clic droit: supprimer", 15, 45);
        g2d.drawString("'E' pour sauvegarder et quitter", 15, 60);
        g2d.drawString("Zones: " + getCurrentCollisionZones().size(), 15, 80);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (!editorMode) return;
        
        if (e.getButton() == MouseEvent.BUTTON1) {
            dragStart = e.getPoint();
            currentRect = new Rectangle(dragStart.x, dragStart.y, 0, 0);
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            selectedZone = null;
            ArrayList<Rectangle> zones = getCurrentCollisionZones();
            for (int i = zones.size() - 1; i >= 0; i--) {
                if (zones.get(i).contains(e.getPoint())) {
                    selectedZone = zones.get(i);
                    zones.remove(i);
                    saveCollisions();
                    break;
                }
            }
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (!editorMode || dragStart == null) return;
        
        Point current = e.getPoint();
        int x = Math.min(dragStart.x, current.x);
        int y = Math.min(dragStart.y, current.y);
        int w = Math.abs(current.x - dragStart.x);
        int h = Math.abs(current.y - dragStart.y);
        currentRect = new Rectangle(x, y, w, h);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (!editorMode || dragStart == null || e.getButton() != MouseEvent.BUTTON1) return;
        
        if (currentRect != null && currentRect.width > 5 && currentRect.height > 5) {
            getCurrentCollisionZones().add(new Rectangle(currentRect));
            saveCollisions();
        }
        dragStart = null;
        currentRect = null;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
    
    // Getters
    public boolean isEditorMode() { return editorMode; }
}