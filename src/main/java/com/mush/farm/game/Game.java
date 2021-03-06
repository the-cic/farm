/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game;

import com.mush.farm.game.logic.GameInteractionsLogic;
import com.mush.farm.game.events.ControlEvent;
import com.mush.farm.game.events.GenericGameEvent;
import com.mush.farm.game.logic.GameEventLogic;
import com.mush.farm.game.logic.GameMapEventLogic;
import com.mush.farm.game.logic.GameSizes;
import com.mush.farm.game.logic.MovementLogic;
import com.mush.farm.game.render.GameRenderer;
import com.mush.farm.game.model.Body;
import com.mush.farm.game.model.BodyType;
import com.mush.farm.game.model.GameBodies;
import com.mush.farm.game.model.GameCreatures;
import com.mush.farm.game.model.Creature;
import com.mush.farm.game.model.GameMap;
import com.mush.farm.game.model.GameMapGenerator;
import com.mush.farm.game.model.MapObjectType;
import java.awt.Point;
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
    public GameBodies bodies;
    public GameCreatures creatures;
    
    private GameEventLogic gameEventLogic;
    private GameMapEventLogic gameMapLogic;
    private GameInteractionsLogic interactionsLogic;
    private MovementLogic movementLogic;

    private Creature playerCreature;
    private boolean showStats;
    private boolean paused = false;
    private boolean mapCollisionsEnabled = true;

    public Game() {
        control = new GameControl(this);
        bodies = new GameBodies();
        creatures = new GameCreatures(bodies);
        gameMap = new GameMap();
        renderer = new GameRenderer(this);
        keyboardListener = new GameKeyboardListener(control);
        
        gameEventLogic = new GameEventLogic(this);
        gameMapLogic = new GameMapEventLogic(gameMap, bodies);
        interactionsLogic = new GameInteractionsLogic(this);
        movementLogic = new MovementLogic(this);

        GameEventQueue.addListener(this);
        GameEventQueue.addListener(gameEventLogic);
        GameEventQueue.addListener(gameMapLogic);
        GameEventQueue.addListener(interactionsLogic);
        GameEventQueue.addListener(movementLogic);

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
        GameEventQueue.processQueue();

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
    
    public boolean getMapCollisionsEnabled() {
        return mapCollisionsEnabled;
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
            case TOGGLE_COLLISIONS:
                mapCollisionsEnabled = !mapCollisionsEnabled;
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
        if (nearest != null && shortest < GameSizes.TILE_SIZE * distance) {
            return nearest;
        }
        return null;
    }

    private void onJoystick() {
        playerCreature.setMovingDirection(control.joystick.getXJoystick(), control.joystick.getYJoystick());
    }

    private void setTileUnderPlayer(MapObjectType type) {
        Creature creature = playerCreature;
        
        Point mapPoint = GameSizes.getTileCoordinates(creature.body.position);

        gameMap.setTile(mapPoint.x, mapPoint.y, type);
    }

}
