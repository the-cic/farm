/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game;

import com.mush.farm.game.render.GameRenderer;
import com.mush.farm.game.model.Body;
import com.mush.farm.game.model.BodyType;
import com.mush.farm.game.model.GameCharacters;
import com.mush.farm.game.model.MovableCharacter;
import com.mush.farm.game.model.GameMap;
import com.mush.farm.game.model.MapObjectType;
import java.util.List;

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
    public GameCharacters characters;

    private MovableCharacter playerCharacter;
    public boolean showStats;
    private boolean paused = false;

    public Game() {
        eventQueue = new GameEventQueue();
        control = new GameControl(this);
        gameMap = new GameMap();
        characters = new GameCharacters(gameMap, eventQueue);
        renderer = new GameRenderer(this);
        keyboardListener = new GameKeyboardListener(control);

        eventQueue.addListener(this);

        showStats = false;

        setupBodies();
    }

    // todo: ugly, put all of this somewhere else
    private void setupBodies() {
        for (int i = 0; i < 3; i++) {
            gameMap.spawnBody(BodyType.BUCKET, Math.random() * 25 * 16, Math.random() * 25 * 16);
        }
        
        for (int i = 0; i < 3; i++) {
            characters.spawn((int) (100 + Math.random() * 200), (int) (20 + Math.random() * 200), BodyType.PERSON);
        }
        
        playerCharacter = characters.getCharacters().get(0);
    }

    public void update(double elapsedSeconds) {
        eventQueue.process();
        
        double seconds = paused ? 0 : elapsedSeconds;

        characters.update(seconds);
        gameMap.update(seconds, eventQueue);
    }

    public void togglePause() {
        paused = !paused;
    }
    
    public void changeCharacter() {
        List<MovableCharacter> all = characters.getCharacters();
        int ind = all.indexOf(playerCharacter);
        ind++;
        playerCharacter.velocity.setLocation(0, 0);
        playerCharacter = ind >= all.size() ? all.get(0) : all.get(ind);
    }

    public MovableCharacter getPlayer() {
        return playerCharacter;
    }

    @Override
    public void onEvent(GameEvent event) {
        // todo: move this to game logic or something
        switch (event.eventName) {
            case "setTile":
                setTile((MapObjectType) event.eventPayload);
                break;
            case GameControl.E_APPLY_JOYSTICK:
                onJoystick();
                break;
            case MovableCharacter.E_INTERACT:
                onCharacterInteract(event);
                break;
            case MovableCharacter.E_DROP:
                onCharacterDrop(event);
                break;
            case GameMap.E_SPREAD:
                gameMap.onSpread(event);
                break;
            case GameMap.E_SPAWN_ON_TILE:
                gameMap.onSpawnOnTile(event);
                break;
        }
    }

    private void onJoystick() {
        playerCharacter.move(control.joystick.getXJoystick(), control.joystick.getYJoystick());
    }

    private void onCharacterInteract(GameEvent event) {
        MovableCharacter character = (MovableCharacter) event.eventPayload;
        if (character == null) {
            return;
        }
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

    private void onCharacterDrop(GameEvent event) {
        MovableCharacter character = (MovableCharacter) event.eventPayload;
        if (character == null) {
            return;
        }
        Body item = character.removeLastFromInventory();
        if (item != null) {
            item.position.setLocation(character.body.position);
            gameMap.getBodies().add(item);
        }
    }

    private void setTile(MapObjectType type) {
        int u = (int) ((playerCharacter.body.position.x) / GameRenderer.TILE_SIZE);
        int v = (int) ((playerCharacter.body.position.y + GameRenderer.TILE_SIZE) / GameRenderer.TILE_SIZE);

        gameMap.setTile(u, v, type);
    }

}
