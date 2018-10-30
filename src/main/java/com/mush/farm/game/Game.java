/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game;

import com.mush.farm.game.render.GameRenderer;
import com.mush.farm.game.model.Body;
import com.mush.farm.game.model.BodyType;
import com.mush.farm.game.model.Character;
import com.mush.farm.game.model.GameMap;
import com.mush.farm.game.model.MapObject;
import com.mush.farm.game.model.MapObjectType;

/**
 *
 * @author mush
 */
public class Game implements GameEventListener {

    public GameControl control;
    public GameRenderer renderer;
    public GameMap gameMap;
    public GameKeyboardListener keyboardListener;
    public GameEventQueue eventQueue;

    public Character character;
    public boolean showStats;
    private boolean paused = false;

    public Game() {
        control = new GameControl();
        gameMap = new GameMap();
        renderer = new GameRenderer(this);
        keyboardListener = new GameKeyboardListener(this);
        eventQueue = new GameEventQueue();

        eventQueue.addListener(this);

        showStats = true;

        setupBodies();
    }

    // todo: ugly, put all of this somewhere else
    private void setupBodies() {
        for (int i = 0; i < 3; i++) {
//            gameMap.spawnBody(Math.random() < 0.75 ? BodyType.POTATO : BodyType.BUCKET, Math.random() * 25 * 16, Math.random() * 25 * 16);
            gameMap.spawnBody(BodyType.BUCKET, Math.random() * 25 * 16, Math.random() * 25 * 16);
        }
        character = new Character(gameMap.spawnBody(BodyType.PERSON, 0, 0));
//        Body aBody = gameMap.getBodies().remove(0);
//        character.addToInventory(aBody);
    }

    public void update(double elapsedSeconds) {
        eventQueue.process();

        character.update(paused ? 0 : elapsedSeconds);
        gameMap.update(paused ? 0 : elapsedSeconds, eventQueue);
    }

    public void applyJoystick() {
        character.move(control.getXJoystick(), control.getYJoystick());
    }

    @Override
    public void onEvent(GameEvent event) {
        // todo: move this to game logic or something
//        System.out.println(event.eventName);
        switch (event.eventName) {
            case "pause":
                paused = !paused;
                break;
            case "setTile":
                setTile((MapObjectType) event.eventPayload);
                break;
            case "applyJoystick":
                applyJoystick();
                break;
            case "interact":
                interact();
                break;
            case "drop":
                drop();
                break;
            case "spread":
                spread((Object[]) event.eventPayload);
                break;
            case "spawnOnTile":
                spawnOnTile((Object[]) event.eventPayload);
                break;
        }
    }

    private void setTile(MapObjectType type) {
        int u = (int) ((character.body.position.x) / GameRenderer.TILE_SIZE);
        int v = (int) ((character.body.position.y + GameRenderer.TILE_SIZE) / GameRenderer.TILE_SIZE);

        MapObject mapObject = gameMap.getMapObject(u, v);

        if (mapObject != null) {
            mapObject.reset(type);
        }
    }

    private void spread(Object[] params) {
        // new Object[]{u, v, spreadType}
        int u = (int) params[0];
        int v = (int) params[1];
        MapObjectType type = (MapObjectType) params[2];
        int du = 0;
        int dv = 0;

        if (Math.random() < 0.5) {
            du = Math.random() < 0.5 ? - 1 : +1;
        } else {
            dv = Math.random() < 0.5 ? - 1 : +1;
        }

        if (spreadTo(u + du, v + dv, type)) {
            return;
        }

        du = -du;
        dv = -dv;

        if (spreadTo(u + du, v + dv, type)) {
            return;
        }

        int du0 = du;
        du = dv;
        dv = du0;

        if (spreadTo(u + du, v + dv, type)) {
            return;
        }

        du = -du;
        dv = -dv;

        spreadTo(u + du, v + dv, type);
    }

    private boolean spreadTo(int u, int v, MapObjectType type) {
        MapObject mapObject = gameMap.getMapObject(u, v);
        if (mapObject != null) {
            if (mapObject.type == MapObjectType.DIRT || mapObject.type == MapObjectType.ORGANIC_RUBBLE) {
                mapObject.reset(type);
                return true;
            }
        }
        return false;
    }

    private void interact() {
        double shortest = Double.POSITIVE_INFINITY;
        Body nearest = null;
        for (Body body : gameMap.getBodies()) {
            if (body != character.body) {
                double dx = body.position.x - character.body.position.x;
                double dy = body.position.y - character.body.position.y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < shortest) {
                    shortest = dist;
                    nearest = body;
                }
            }
        }
        if (nearest != null && shortest < GameRenderer.TILE_SIZE) {
            gameMap.getBodies().remove(nearest);
            character.addToInventory(nearest);
        }
    }

    private void drop() {
        Body item = character.removeLastFromInventory();
        if (item != null) {
            item.position.setLocation(character.body.position);
            gameMap.getBodies().add(item);
        }
    }

    private void spawnOnTile(Object[] params) {
        //eventQueue.add(new GameEvent("spawnOnTile", new Object[]{u, v, bodyType}));
        int u = (int) params[0];
        int v = (int) params[1];
        BodyType type = (BodyType) params[2];
        int x = u * GameRenderer.TILE_SIZE + GameRenderer.TILE_SIZE / 2;
        int y = v * GameRenderer.TILE_SIZE;
        gameMap.spawnBody(type, x, y);
    }

}
