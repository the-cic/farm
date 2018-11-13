/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mush
 */
public class GameCreatures {

    private GameBodies gameBodies;
    private List<Creature> creatures;
    private Map<Integer, Creature> creatureMap;
    private int nextCreatureId = 0;

    public GameCreatures(GameBodies gameBodies) {
        this.gameBodies = gameBodies;
        this.creatures = new LinkedList<>();
        this.creatureMap = new HashMap<>();
    }

    // do something fancier, maybe body location dependent
    public List<Creature> getCreatures() {
        return creatures;
    }

    public Creature getCreature(int id) {
        return creatureMap.get(id);
    }

    public void update(double elapsedSeconds) {
        for (Creature creature : creatures) {
            creature.update(elapsedSeconds);
        }
    }

    public synchronized Creature spawn(int x, int y, BodyType bodyType) {
        int creatureId = nextCreatureId++;

        Creature creature = new Creature(
                creatureId,
                gameBodies.spawnBody(bodyType, x, y));

        creatures.add(creature);
        creatureMap.put(creatureId, creature);

        return creature;
    }

    public synchronized boolean unspawn(int id) {
        Creature creature = creatureMap.remove(id);
        if (creature != null) {
            creatures.remove(creature);
            return true;
        }
        return false;
    }

}
