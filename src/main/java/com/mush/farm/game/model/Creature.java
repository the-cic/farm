/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.model;

import com.mush.farm.game.GameEventQueue;
import com.mush.farm.game.events.CreatureEvent;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * MOB
 *
 * @author mush
 */
public class Creature {

    public final int creatureId;
    public final Body body;
    public final Point2D.Double velocity = new Point2D.Double(0, 0);
    private final double movementSpeed;

    private Body equippedBody;

    public Creature(int id, Body body) {
        this.creatureId = id;
        this.body = body;
        this.movementSpeed = 50;
        this.body.creature = this;
    }

    public void setMovingDirection(int dx, int dy) {
        velocity.x = dx;
        velocity.y = dy;
        if (dx != 0 && dy != 0) {
            double a = Math.sqrt(dx * dx + dy * dy);
            velocity.x /= a;
            velocity.y /= a;
        }
    }

    public void update(double elapsedSeconds) {
        double xOffset = velocity.x * elapsedSeconds * movementSpeed;
        double yOffset = velocity.y * elapsedSeconds * movementSpeed;
        
        if (xOffset != 0 || yOffset != 0) {
            GameEventQueue.send(new CreatureEvent.Move(this, xOffset, yOffset));
        }
    }

    public void sendInteract() {
        GameEventQueue.send(new CreatureEvent.Interact(this));
    }

    public void sendGive() {
        GameEventQueue.send(new CreatureEvent.Give(this));
    }

    public void sendPickUp() {
        GameEventQueue.send(new CreatureEvent.PickUp(this));
    }

    public void sendDrop() {
        GameEventQueue.send(new CreatureEvent.Drop(this));
    }

    public void sendEquip(int index) {
        GameEventQueue.send(new CreatureEvent.Equip(this, index));
    }
    
    public void sendEquipLast() {
        int lastIndex = getInventory().size() - 1;
        if (lastIndex >= 0) {
            GameEventQueue.send(new CreatureEvent.Equip(this, lastIndex));
        }
    }
    
    public void sendUnequip() {
        GameEventQueue.send(new CreatureEvent.Unequip(this));
    }
    
    public void sendCycleInventory() {
        GameEventQueue.send(new CreatureEvent.CycleInventory(this));
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
    
    public Body unequipDirectly() {
        return unEquip();
    }
    
    public void equipDirectly(Body item) {
        equip(item);
    }

    private void equip(Body item) {
        equippedBody = item;
    }

    private Body unEquip() {
        Body item = equippedBody;
        equippedBody = null;
        return item;
    }

    public boolean hasEquipped() {
        return equippedBody != null;
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
