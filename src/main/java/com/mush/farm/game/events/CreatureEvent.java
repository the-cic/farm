/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.events;

import com.mush.farm.game.model.Body;
import com.mush.farm.game.model.Creature;
import com.mush.farm.game.model.MapObjectType;

/**
 *
 * @author mush
 */
public abstract class CreatureEvent extends GameEvent {

    public final int creatureId;

    public CreatureEvent(Creature creature) {
        this.creatureId = creature.creatureId;
    }

    public static class Interact extends CreatureEvent {

        public Interact(Creature creature) {
            super(creature);
        }
    }

    public static class Give extends CreatureEvent {

        public Give(Creature creature) {
            super(creature);
        }
    }

    public static class PickUp extends CreatureEvent {

        public PickUp(Creature creature) {
            super(creature);
        }
    }

    public static class Drop extends CreatureEvent {

        public Drop(Creature creature) {
            super(creature);
        }
    }

    public static class Equip extends CreatureEvent {

        public final int inventoryIndex;

        public Equip(Creature creature, int inventoryIndex) {
            super(creature);
            this.inventoryIndex = inventoryIndex;
        }
    }

    public static class Unequip extends CreatureEvent {

        public Unequip(Creature creature) {
            super(creature);
        }
    }

    public static class CycleInventory extends CreatureEvent {

        public CycleInventory(Creature creature) {
            super(creature);
        }
    }

    public static class Move extends CreatureEvent {

        public final double xOffset;
        public final double yOffset;

        public Move(Creature creature, double xOffset, double yOffset) {
            super(creature);
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }

    public static class BodyCollision extends CreatureEvent {

        public final int bodyId;

        public BodyCollision(Creature creature, Body body) {
            super(creature);
            this.bodyId = body.bodyId;
        }
    }

    public static class MapCollision extends CreatureEvent {

        public final MapObjectType type;

        public MapCollision(Creature creature, MapObjectType type) {
            super(creature);
            this.type = type;
        }
    }

}
