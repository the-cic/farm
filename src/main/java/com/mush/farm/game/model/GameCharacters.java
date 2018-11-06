/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.model;

import com.mush.farm.game.GameEventQueue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mush
 */
public class GameCharacters {

    private GameMap gameMap;
    private GameEventQueue eventQueue;
    private List<MovableCharacter> characters;
    private Map<Integer, MovableCharacter> characterMap;
    private int nextCharacterId = 0;

    public GameCharacters(GameMap gameMap, GameEventQueue queue) {
        this.gameMap = gameMap;
        this.eventQueue = queue;
        this.characters = new LinkedList<>();
        this.characterMap = new HashMap<>();
    }

    // do something fancier, maybe body location dependent
    public List<MovableCharacter> getCharacters() {
        return characters;
    }

    public MovableCharacter getCharacter(int id) {
        return characterMap.get(id);
    }

    public void update(double elapsedSeconds) {
        for (MovableCharacter character : characters) {
            character.update(elapsedSeconds);
        }
    }

    public synchronized MovableCharacter spawn(int x, int y, BodyType bodyType) {
        int characterId = nextCharacterId++;

        MovableCharacter character = new MovableCharacter(
                characterId,
                gameMap.spawnBody(bodyType, x, y),
                eventQueue);

        characters.add(character);
        characterMap.put(characterId, character);

        return character;
    }

    public synchronized boolean unspawn(int id) {
        MovableCharacter character = characterMap.remove(id);
        if (character != null) {
            characters.remove(character);
            return true;
        }
        return false;
    }

}
