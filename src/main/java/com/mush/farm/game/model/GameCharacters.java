/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.model;

import com.mush.farm.game.GameEventQueue;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author mush
 */
public class GameCharacters {
    
    private GameMap gameMap;
    private GameEventQueue eventQueue;
    private List<MovableCharacter> characters;
    
    public GameCharacters(GameMap gameMap, GameEventQueue queue) {
        this.gameMap = gameMap;
        this.eventQueue = queue;
        this.characters = new LinkedList<>();
    }
    
    // do something fancier, maybe body location dependent
    public List<MovableCharacter> getCharacters() {
        return characters;
    }
    
    public void update(double elapsedSeconds) {
        for (MovableCharacter character : characters) {
            character.update(elapsedSeconds);
        }
    }
    
    public MovableCharacter spawn(int x, int y, BodyType bodyType) {
        MovableCharacter character = new MovableCharacter(gameMap.spawnBody(bodyType, x, y), eventQueue);
        characters.add(character);
        return character;
    }
    
}
