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
    private final GameEventQueue eventQueue;

    private Body equippedBody;

    public Creature(int id, Body body, GameEventQueue queue) {
        this.creatureId = id;
        this.body = body;
        this.movementSpeed = 50;
        this.eventQueue = queue;
        this.body.creature = this;
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
        eventQueue.add(new CreatureEvent.Interact(this));
    }

    public void sendGive() {
        eventQueue.add(new CreatureEvent.Give(this));
    }

    public void sendPickUp() {
        eventQueue.add(new CreatureEvent.PickUp(this));
    }

    public void sendDrop() {
        eventQueue.add(new CreatureEvent.Drop(this));
    }

    public void sendEquip(int index) {
        eventQueue.add(new CreatureEvent.Equip(this, index));
    }
    
    public void sendEquipLast() {
        int lastIndex = getInventory().size() - 1;
        if (lastIndex >= 0) {
            eventQueue.add(new CreatureEvent.Equip(this, lastIndex));
        }
    }
    
    public void sendUnequip() {
        eventQueue.add(new CreatureEvent.Unequip(this));
    }
    
    public void sendCycleInventory() {
        eventQueue.add(new CreatureEvent.CycleInventory(this));
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