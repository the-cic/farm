/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game;

import com.mush.farm.game.events.CharacterEvent;
import com.mush.farm.game.events.ControlEvent;
import com.mush.farm.game.events.GenericGameEvent;
import com.mush.farm.game.events.InteractionEvent;
import com.mush.farm.game.render.GameRenderer;
import com.mush.farm.game.model.Body;
import com.mush.farm.game.model.BodyType;
import com.mush.farm.game.model.GameBodies;
import com.mush.farm.game.model.GameCharacters;
import com.mush.farm.game.model.MovableCharacter;
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
    public GameCharacters characters;
    private GameInteractionsLogic interactionsLogic;

    private MovableCharacter playerCharacter;
    private boolean showStats;
    private boolean paused = false;

    public Game() {
        eventQueue = new GameEventQueue();
        control = new GameControl(this);
        bodies = new GameBodies();
        characters = new GameCharacters(bodies, eventQueue);
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
            characters.spawn((int) (100 + Math.random() * 200), (int) (20 + Math.random() * 200), BodyType.PERSON);
        }

        playerCharacter = characters.getCharacters().get(0);
    }

    public void update(double elapsedSeconds) {
        eventQueue.process();

        double seconds = paused ? 0 : elapsedSeconds;

        characters.update(seconds);
        gameMap.update(seconds);
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
            case CHANGE_CHARACTER:
                changeCharacter();
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

    public void onEvent(CharacterEvent.Interact event) {
        MovableCharacter character = characters.getCharacter(event.characterId);
        if (character == null) {
            return;
        }

        Body nearest = getClosestBodyTo(character.body);
        Body tool = character.getEquipped();

        if (tool != null) {
            if (nearest != null) {
                eventQueue.add(new InteractionEvent.BodyOnBody(character, tool, nearest));
            }
            int u = (int) ((playerCharacter.body.position.x) / GameRenderer.TILE_SIZE);
            int v = (int) ((playerCharacter.body.position.y + GameRenderer.TILE_SIZE) / GameRenderer.TILE_SIZE);

            MapObject mapObject = gameMap.getMapObject(u, v);
            eventQueue.add(new InteractionEvent.BodyOnMapObject(character, tool, mapObject));
        }
    }

    public Body getClosestBodyTo(Body body0) {
        double shortest = Double.POSITIVE_INFINITY;
        Body nearest = null;
        for (Body aBody : bodies.getBodies()) {
            if (aBody != body0) {
                double dx = aBody.position.x - body0.position.x;
                double dy = aBody.position.y - body0.position.y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < shortest) {
                    shortest = dist;
                    nearest = aBody;
                }
            }
        }
        if (nearest != null && shortest < GameRenderer.TILE_SIZE) {
            return nearest;
        }
        return null;
    }

    public void onEvent(CharacterEvent.PickUp event) {
        MovableCharacter character = characters.getCharacter(event.characterId);
        if (character == null) {
            return;
        }

        Body nearest = getClosestBodyTo(character.body);

        if (nearest != null) {
            bodies.getBodies().remove(nearest);
            character.addToInventory(nearest);
        }
    }

    public void onEvent(CharacterEvent.Drop event) {
        MovableCharacter character = characters.getCharacter(event.characterId);
        if (character == null) {
            return;
        }
        Body item = character.removeLastFromInventory();
        if (item != null) {
            item.position.setLocation(character.body.position);
            // just a tiny bit in front of the character
            item.position.y += 1;
            bodies.getBodies().add(item);
        }
    }

    public void onEvent(CharacterEvent.Equip event) {
        MovableCharacter character = characters.getCharacter(event.characterId);
        if (character == null) {
            return;
        }
        character.equipFromInventory(event.inventoryIndex);
    }

    public void onEvent(CharacterEvent.Unequip event) {
        MovableCharacter character = characters.getCharacter(event.characterId);
        if (character == null) {
            return;
        }
        character.unequipIntoInventory();
    }

    public void onEvent(CharacterEvent.CycleInventory event) {
        MovableCharacter character = characters.getCharacter(event.characterId);
        if (character == null) {
            return;
        }
        character.cycleInventory();
    }

    private void onJoystick() {
        playerCharacter.move(control.joystick.getXJoystick(), control.joystick.getYJoystick());
    }

    private void setTileUnderPlayer(MapObjectType type) {
        MovableCharacter character = playerCharacter;
        int u = (int) ((character.body.position.x) / GameRenderer.TILE_SIZE);
        int v = (int) ((character.body.position.y + GameRenderer.TILE_SIZE) / GameRenderer.TILE_SIZE);

        gameMap.setTile(u, v, type);
    }

}
