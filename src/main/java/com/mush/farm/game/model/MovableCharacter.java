/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.model;

import com.mush.farm.game.GameEventQueue;
import com.mush.farm.game.events.CharacterEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * MOB
 * 
 * @author mush
 */
public class MovableCharacter {
    
    public final int characterId;
    public final Body body;
    public final Point2D.Double velocity = new Point2D.Double(0, 0);
    private final double movementSpeed;
    private final GameEventQueue eventQueue;

    private List<Body> inventory;
    private List<BodyType> inventoryTypes;

    public MovableCharacter(int id, Body body, GameEventQueue queue) {
        this.characterId = id;
        this.body = body;
        this.movementSpeed = 50;
        this.inventory = new ArrayList<>();
        this.inventoryTypes = new ArrayList<>();
        this.eventQueue = queue;
    }

    public void move(int dx, int dy) {
        velocity.x = dx;
        velocity.y = dy;
        if (dx != 0 && dy != 0) {
            double a = Math.sqrt(dx * dx + dy * dy);
            velocity.x /= a;
            velocity.y /= a;
        }
    }

    public void update(double elapsedSeconds) {
        body.position.x += velocity.x * elapsedSeconds * movementSpeed;
        body.position.y += velocity.y * elapsedSeconds * movementSpeed;
    }
    
    public void interact() {
        eventQueue.add(new CharacterEvent.Interact(this));
    }
    
    public void drop() {
        eventQueue.add(new CharacterEvent.Drop(this));
    }

    public void addToInventory(Body item) {
        inventory.add(item);
        inventoryTypes.add(item.type);
        body.containedBodies.add(item);
    }

    // I don't know I'm just winging it
    // Excellent comment, I have no idea what it's about
    // Every time I come here I have less idea what this is about
    public Body removeFromInventory(int index) {
        Body item = inventory.remove(index);
        inventoryTypes.remove(index);
        body.containedBodies.remove(index);
        return item;
    }

    public Body removeLastFromInventory() {
        if (inventory.isEmpty()) {
            return null;
        }
        return removeFromInventory(inventory.size() - 1);
    }

    public List<Body> getInventory() {
        return inventory;
    }

    public List<BodyType> getInventoryTypes() {
        return inventoryTypes;
    }

}
