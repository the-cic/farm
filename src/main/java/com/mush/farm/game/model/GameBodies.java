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
public class GameBodies {

    private final List<Body> bodies;
    private final Map<Integer, Body> bodyMap;
    private int nextBodyId = 0;

    public GameBodies() {
        bodies = new LinkedList<>();
        bodyMap = new HashMap<>();
    }

    public synchronized Body spawnBody(BodyType type, double x, double y) {
        int id = nextBodyId++;
        Body body = new Body(id, type);
        body.position.setLocation(x, y);
        bodies.add(body);
        bodyMap.put(id, body);
        return body;
    }

    public synchronized boolean unspawn(int id) {
        Body body = bodyMap.remove(id);
        if (body != null) {
            bodies.remove(body);
            return true;
        }
        return false;
    }

    // todo: maybe something more useful, like a bound box of bodies?
    public List<Body> getBodies() {
        return bodies;
    }
    
    public Body getBody(int id) {
        return bodyMap.get(id);
    }

}
