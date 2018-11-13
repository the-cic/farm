/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game;

import com.mush.farm.game.events.CreatureEvent;
import com.mush.farm.game.events.ControlEvent;
import com.mush.farm.game.events.GenericGameEvent;
import com.mush.farm.game.events.InteractionEvent;
import com.mush.farm.game.render.GameRenderer;
import com.mush.farm.game.model.Body;
import com.mush.farm.game.model.BodyType;
import com.mush.farm.game.model.GameBodies;
import com.mush.farm.game.model.GameCreatures;
import com.mush.farm.game.model.Creature;
import com.mush.farm.game.model.GameMap;
import com.mush.farm.game.model.GameMapGenerator;
import com.mush.farm.game.model.MapObject;
import com.mush.farm.game.model.MapObjectType;
import java.util.List;

/**
 *
 * @author mush
 */
public class Game {

    public GameControl control;
    public GameRenderer renderer;
    public GameMap gameMap;
    public GameKeyboardListener keyboardListener;
    public GameEventQueue eventQueue;
    public GameBodies bodies;
    public GameCreatures creatures;
    private GameInteractionsLogic interactionsLogic;

    private Creature playerCreature;
    private boolean showStats;
    private boolean paused = false;

    public Game() {
        eventQueue = new GameEventQueue();
        control = new GameControl(this);
        bodies = new GameBodies();
        creatures = new GameCreatures(bodies, eventQueue);
        gameMap = new GameMap(bodies, eventQueue);
        renderer = new GameRenderer(this);
        keyboardListener = new GameKeyboardListener(control);
        interactionsLogic = new GameInteractionsLogic(this, eventQueue);

        eventQueue.addListener(this);
        eventQueue.addListener(gameMap);
        eventQueue.addListener(interactionsLogic);

        showStats = false;

        new GameMapGenerator(gameMap).generate();
        setupBodies();
    }

    // todo: ugly, put all of this somewhere else
    private void setupBodies() {
        for (int i = 0; i < 3; i++) {
            bodies.spawnBody(BodyType.BUCKET_EMPTY, Math.random() * 25 * 16, Math.random() * 25 * 16);
        }

        bodies.spawnBody(BodyType.SHOVEL, Math.random() * 25 * 16, Math.random() * 25 * 16);

        for (int i = 0; i < 3; i++) {
            creatures.spawn((int) (100 + Math.random() * 200), (int) (20 + Math.random() * 200), BodyType.PERSON);
        }

        playerCreature = creatures.getCreatures().get(0);
    }

    public void update(double elapsedSeconds) {
        eventQueue.process();

        double seconds = paused ? 0 : elapsedSeconds;

        creatures.update(seconds);
        gameMap.update(seconds);
    }

    public void togglePause() {
        paused = !paused;
    }

    public void changeCreature() {
        List<Creature> all = creatures.getCreatures();
        int ind = all.indexOf(playerCreature);
        ind++;
        playerCreature.velocity.setLocation(0, 0);
        playerCreature = ind >= all.size() ? all.get(0) : all.get(ind);
    }

    public Creature getPlayer() {
        return playerCreature;
    }

    public boolean getShowStats() {
        return showStats;
    }

    public void onEvent(ControlEvent event) {
        switch (event.action) {
            case APPLY_JOYSTICK:
                onJoystick();
                break;
            case PAUSE:
                paused = !paused;
                break;
            case TOGGLE_STATS:
                showStats = !showStats;
                break;
            case CHANGE_CREATURE:
                changeCreature();
                break;
        }
    }

    public void onEvent(GenericGameEvent event) {
        switch (event.eventName) {
            case "setTile":
                setTileUnderPlayer((MapObjectType) event.eventPayload);
                break;
        }
    }

    public void onEvent(CreatureEvent.Interact event) {
        Creature creature = creatures.getCreature(event.creatureId);
        if (creature == null) {
            return;
        }

        Body nearest = getClosestBodyTo(creature.body, 1);
        Body tool = creature.getEquipped();

        if (tool != null) {
            if (nearest != null) {
                eventQueue.add(new InteractionEvent.BodyOnBody(creature, tool, nearest));
            }
            int u = (int) ((playerCreature.body.position.x) / GameRenderer.TILE_SIZE);
            int v = (int) ((playerCreature.body.position.y + GameRenderer.TILE_SIZE) / GameRenderer.TILE_SIZE);

            MapObject mapObject = gameMap.getMapObject(u, v);
            eventQueue.add(new InteractionEvent.BodyOnMapObject(creature, tool, mapObject));
        }
    }

    public void onEvent(CreatureEvent.Give event) {
        Creature creature = creatures.getCreature(event.creatureId);
        if (creature == null) {
            return;
        }

        if (creature.hasEquipped()) {
            Body nearest = getClosestCreatureBodyTo(creature.body, 2);

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

    public Body getClosestCreatureBodyTo(Body body0, double distance) {
        return getClosestBodyTo(body0, true, distance);
    }

    public Body getClosestBodyTo(Body body0, double distance) {
        return getClosestBodyTo(body0, false, distance);
    }

    public Body getClosestBodyTo(Body body0, boolean isCreature, double distance) {
        double shortest = Double.POSITIVE_INFINITY;
        Body nearest = null;
        for (Body aBody : bodies.getBodies()) {
            if (aBody != body0 && (!isCreature || aBody.creature != null)) {
                double dx = aBody.position.x - body0.position.x;
                double dy = aBody.position.y - body0.position.y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < shortest) {
                    shortest = dist;
                    nearest = aBody;
                }

            }
        }
        if (nearest != null && shortest < GameRenderer.TILE_SIZE * distance) {
            return nearest;
        }
        return null;
    }

    public void onEvent(CreatureEvent.PickUp event) {
        Creature creature = creatures.getCreature(event.creatureId);
        if (creature == null) {
            return;
        }

        Body nearest = getClosestBodyTo(creature.body, 1);

        if (nearest != null) {
            bodies.getBodies().remove(nearest);
            creature.addToInventory(nearest);
        }
    }

    public void onEvent(CreatureEvent.Drop event) {
        Creature creature = creatures.getCreature(event.creatureId);
        if (creature == null) {
            return;
        }
        Body item = creature.removeLastFromInventory();
        if (item != null) {
            item.position.setLocation(creature.body.position);
            // just a tiny bit in front of the creature
            item.position.y += 1;
            bodies.getBodies().add(item);
        }
    }

    public void onEvent(CreatureEvent.Equip event) {
        Creature creature = creatures.getCreature(event.creatureId);
        if (creature == null) {
            return;
        }
        creature.equipFromInventory(event.inventoryIndex);
    }

    public void onEvent(CreatureEvent.Unequip event) {
        Creature creature = creatures.getCreature(event.creatureId);
        if (creature == null) {
            return;
        }
        creature.unequipIntoInventory();
    }

    public void onEvent(CreatureEvent.CycleInventory event) {
        Creature creature = creatures.getCreature(event.creatureId);
        if (creature == null) {
            return;
        }
        creature.cycleInventory();
    }

    private void onJoystick() {
        playerCreature.move(control.joystick.getXJoystick(), control.joystick.getYJoystick());
    }

    private void setTileUnderPlayer(MapObjectType type) {
        Creature creature = playerCreature;
        int u = (int) ((creature.body.position.x) / GameRenderer.TILE_SIZE);
        int v = (int) ((creature.body.position.y + GameRenderer.TILE_SIZE) / GameRenderer.TILE_SIZE);

        gameMap.setTile(u, v, type);
    }

}
