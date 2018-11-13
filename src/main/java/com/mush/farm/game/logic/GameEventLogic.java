/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.logic;

import com.mush.farm.game.Game;
import com.mush.farm.game.GameEventQueue;
import com.mush.farm.game.events.CreatureEvent;
import com.mush.farm.game.events.InteractionEvent;
import com.mush.farm.game.model.Body;
import com.mush.farm.game.model.Creature;
import com.mush.farm.game.model.MapObject;
import com.mush.farm.game.render.GameRenderer;

/**
 *
 * @author mush
 */
public class GameEventLogic {
    
    private final Game game;
    
    public GameEventLogic(Game game) {
        this.game = game;
    }
 
    public void onEvent(CreatureEvent.Interact event) {
        Creature creature = game.creatures.getCreature(event.creatureId);
        if (creature == null) {
            return;
        }

        Body nearest = game.getClosestBodyTo(creature.body, 1);
        Body tool = creature.getEquipped();

        if (tool != null) {
            if (nearest != null) {
                GameEventQueue.send(new InteractionEvent.BodyOnBody(creature, tool, nearest));
            }
            int u = (int) ((game.getPlayer().body.position.x) / GameRenderer.TILE_SIZE);
            int v = (int) ((game.getPlayer().body.position.y + GameRenderer.TILE_SIZE) / GameRenderer.TILE_SIZE);

            MapObject mapObject = game.gameMap.getMapObject(u, v);
            GameEventQueue.send(new InteractionEvent.BodyOnMapObject(creature, tool, mapObject));
        }
    }

    public void onEvent(CreatureEvent.Give event) {
        Creature creature = game.creatures.getCreature(event.creatureId);
        if (creature == null) {
            return;
        }

        if (creature.hasEquipped()) {
            Body nearest = game.getClosestCreatureBodyTo(creature.body, 2);

            if (nearest != null && nearest.creature != null) {
                Creature peer = nearest.creature;

                Body item = creature.unequipDirectly();

                if (peer.hasEquipped()) {
                    peer.addToInventory(item);
                } else {
                    peer.equipDirectly(item);
                }
            }
        }
    }
    
    public void onEvent(CreatureEvent.PickUp event) {
        Creature creature = game.creatures.getCreature(event.creatureId);
        if (creature == null) {
            return;
        }

        Body nearest = game.getClosestBodyTo(creature.body, 1);

        if (nearest != null) {
            game.bodies.getBodies().remove(nearest);
            if (creature.hasEquipped()) {
                creature.addToInventory(nearest);
            } else {
                creature.equipDirectly(nearest);
            }
        }
    }

    public void onEvent(CreatureEvent.Drop event) {
        Creature creature = game.creatures.getCreature(event.creatureId);
        if (creature == null) {
            return;
        }
        Body item = creature.removeLastFromInventory();
        if (item == null) {
            item = creature.unequipDirectly();
        }
        if (item != null) {
            item.position.setLocation(creature.body.position);
            // just a tiny bit in front of the creature
            item.position.y += 1;
            game.bodies.getBodies().add(item);
        }
    }

    public void onEvent(CreatureEvent.Equip event) {
        Creature creature = game.creatures.getCreature(event.creatureId);
        if (creature == null) {
            return;
        }
        creature.equipFromInventory(event.inventoryIndex);
    }

    public void onEvent(CreatureEvent.Unequip event) {
        Creature creature = game.creatures.getCreature(event.creatureId);
        if (creature == null) {
            return;
        }
        creature.unequipIntoInventory();
    }

    public void onEvent(CreatureEvent.CycleInventory event) {
        Creature creature = game.creatures.getCreature(event.creatureId);
        if (creature == null) {
            return;
        }
        creature.cycleInventory();
    }
    
    
}
