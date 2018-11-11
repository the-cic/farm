/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.model;

import com.mush.farm.game.GameEventQueue;
import com.mush.farm.game.events.CharacterEvent;
import java.awt.geom.Point2D;
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

    private Body equippedBody;

    public MovableCharacter(int id, Body body, GameEventQueue queue) {
        this.characterId = id;
        this.body = body;
        this.movementSpeed = 50;
        this.eventQueue = queue;
        this.body.character = this;
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

    public void sendInteract() {
        eventQueue.add(new CharacterEvent.Interact(this));
    }

    public void sendDrop() {
        eventQueue.add(new CharacterEvent.Drop(this));
    }

    public void sendEquip(int index) {
        eventQueue.add(new CharacterEvent.Equip(this, index));
    }
    
    public void sendEquipLast() {
        int lastIndex = getInventory().size() - 1;
        if (lastIndex >= 0) {
            eventQueue.add(new CharacterEvent.Equip(this, lastIndex));
        }
    }
    
    public void sendUnequip() {
        eventQueue.add(new CharacterEvent.Unequip(this));
    }
    
    public void sendCycleInventory() {
        eventQueue.add(new CharacterEvent.CycleInventory(this));
    }

    public void cycleInventory() {
        Body inventoryItem = removeFromInventory(0);
        if (inventoryItem != null) {
            addToInventory(inventoryItem);
        }
    }
    
    public void equipFromInventory(int index) {
        Body inventoryItem = removeFromInventory(index);
        Body equippedItem = unEquip();
        if (equippedItem != null) {
            addToInventory(equippedItem);
        }
        if (inventoryItem != null) {
            equip(inventoryItem);
        }
    }
    
    public void unequipIntoInventory() {
        Body equippedItem = unEquip();
        if (equippedItem != null) {
            addToInventory(equippedItem);
        }
    }

    private void equip(Body item) {
        equippedBody = item;
    }

    private Body unEquip() {
        Body item = equippedBody;
        equippedBody = null;
        return item;
    }

    public Body getEquipped() {
        return equippedBody;
    }

    public void addToInventory(Body item) {
        body.containedBodies.add(item);
    }

    public Body removeFromInventory(int index) {
        try {
            Body item = body.containedBodies.remove(index);
            return item;
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    public Body removeLastFromInventory() {
        if (body.containedBodies.isEmpty()) {
            return null;
        }
        return removeFromInventory(body.containedBodies.size() - 1);
    }

    public List<Body> getInventory() {
        return body.containedBodies;
    }

}
