/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.model;

import com.mush.farm.game.GameEventQueue;
import com.mush.farm.game.events.MapEvent;

/**
 *
 * @author mush
 */
public class MapObject {

    public MapObjectType type;

    public double age; // 0 .. inf
    private Double maxAge;
    public double integrity; // 0 .. 1
    private double decayRateMultiplier; // 0.9 - 1.1

    private final int u;
    private final int v;

    public MapObject(MapObjectType type, int u, int v) {
        this.u = u;
        this.v = v;
        reset(type);
    }

    public void reset(MapObjectType type) {
        this.type = type;
        age = 0;
        maxAge = MapObjectType.getEvolveAge(type);
        if (!maxAge.isInfinite()) {
            maxAge *= 0.9 + Math.random() * 0.2;
        }
        integrity = 1;
        decayRateMultiplier = 0.9 + Math.random() * 0.2;
    }

    public void update(double time, MapWater mapWater) {
        age += time;

        if (type == MapObjectType.WATER) {
            mapWater.setStepsFromSourceAtSource();
        }
        
        double decayRate = MapObjectType.getDecayRate(type);
        double waterMultiplier = MapObjectType.getWaterDecayMultiplier(type, mapWater.getValue());

        integrity -= decayRate * waterMultiplier * time * decayRateMultiplier;

        if (integrity < 0) {
            decay();
        } else if (age > maxAge) {
            spread();
            spawn();
            evolve();
        }
    }

    public void evolve() {
        MapObjectType newType = MapObjectType.evolve(type);
        reset(newType);
    }

    public void spread() {
        MapObjectType spreadType = MapObjectType.spread(type);
        if (spreadType != null) {
            GameEventQueue.send(new MapEvent.Spread(u, v, spreadType));
        }
    }

    public void spawn() {
        BodyType bodyType = MapObjectType.spawn(type);
        if (bodyType != null) {
            GameEventQueue.send(new MapEvent.SpawnOnTile(u, v, bodyType));
        }
    }

    public void decay() {
        MapObjectType newType = MapObjectType.decay(type);
        reset(newType);
    }

    public double getAgePercent() {
        return age / maxAge;
    }
    
    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

}
