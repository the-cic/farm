/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.model;

import com.mush.farm.game.GameEventQueue;
import com.mush.farm.game.events.MapEvent;
import com.mush.farm.game.render.GameRenderer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mush
 */
public class GameMap {

    public static final int MAP_WIDTH = 25;
    public static final int MAP_HEIGHT = 25;
    private final double maxElapsedSeconds = 0.5;

    private MapObject[] mapObjects;
    private MapWater[] waterMap;
    private List<Body> mapBodies;
    private double totalElapsedSeconds = 0;
    private final GameEventQueue eventQueue;

    public GameMap(GameEventQueue eventQueue) {
        this.eventQueue = eventQueue;
        createMap();
    }

    public void update(double elapsedSeconds) {
        totalElapsedSeconds += elapsedSeconds;

        if (totalElapsedSeconds < maxElapsedSeconds) {
            return;
        }

        update();
        totalElapsedSeconds = 0;
    }

    private void update() {
        for (int i = 0; i < mapObjects.length; i++) {
            mapObjects[i].update(totalElapsedSeconds, waterMap[i], eventQueue);
        }
        for (int i = 0; i < MAP_WIDTH; i++) {
            for (int j = 0; j < MAP_HEIGHT; j++) {
                MapWater water = getWaterObject(i, j);

                if (water.isWithinMaxStepsFromSource()) {
                    MapObjectType type = getMapObjectType(i, j);

                    if (!MapObjectType.blocksWater(type)) {
                        propagate(water, i, j);
                    }
                }
            }
        }
        for (MapWater water : waterMap) {
            water.applyStepsFromSource();
            water.update(totalElapsedSeconds);
        }
    }

    public void onEvent(MapEvent.Spread event) {
        spread(event.u, event.v, event.type);
    }

    public void onEvent(MapEvent.SpawnOnTile event) {
        spawnOnTile(event.u, event.v, event.type);
    }

    private void createMap() {
        int size = MAP_WIDTH * MAP_HEIGHT;

        mapObjects = new MapObject[size];
        waterMap = new MapWater[size];

        for (int i = 0; i < size; i++) {
            mapObjects[i] = null;
            waterMap[i] = new MapWater();
        }

        mapBodies = new ArrayList<>();
    }

    public Body spawnBody(BodyType type, double x, double y) {
        Body body = new Body(type);
        body.position.setLocation(x, y);
        mapBodies.add(body);
        return body;
    }

    // todo: maybe something more useful, like a bound box of bodies?
    public List<Body> getBodies() {
        return mapBodies;
    }

    public MapObject getMapObject(int u, int v) {
        if (u >= 0 && u < MAP_WIDTH && v >= 0 && v < MAP_HEIGHT) {
            return mapObjects[u + v * MAP_HEIGHT];
        }
        return null;
    }

    public void setMapObject(int u, int v, MapObject object) {
        mapObjects[u + v * MAP_HEIGHT] = object;
    }

    public MapObjectType getMapObjectType(int u, int v) {
        MapObject object = getMapObject(u, v);
        return object != null ? object.type : null;
    }

    public void setMapObject(int u, int v, MapObjectType type) {
        setMapObject(u, v, new MapObject(type, u, v));
    }

    public MapWater getWaterObject(int u, int v) {
        if (u >= 0 && u < MAP_WIDTH && v >= 0 && v < MAP_HEIGHT) {
            return waterMap[u + v * MAP_HEIGHT];
        }
        return null;
    }

    public void setTile(int u, int v, MapObjectType type) {
        MapObject mapObject = getMapObject(u, v);

        if (mapObject != null) {
            mapObject.reset(type);
        }
    }

    private void spread(int u, int v, MapObjectType type) {
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
        MapObject mapObject = getMapObject(u, v);
        if (mapObject != null) {
            if (MapObjectType.canSpreadInto(type, mapObject.type)) {
                mapObject.reset(type);
                return true;
            }
        }
        return false;
    }

    private void spawnOnTile(int u, int v, BodyType type) {
        int x = u * GameRenderer.TILE_SIZE + GameRenderer.TILE_SIZE / 2;
        int y = v * GameRenderer.TILE_SIZE;
        spawnBody(type, x, y);
    }

    private void propagate(MapWater water, int i, int j) {
        double stepsFromSource = water.getStepsFromSource();

        stepsFromSource += 1;

        propagateStepsTo(i + 1, j + 0, stepsFromSource);
        propagateStepsTo(i - 1, j + 0, stepsFromSource);
        propagateStepsTo(i + 0, j + 1, stepsFromSource);
        propagateStepsTo(i + 0, j - 1, stepsFromSource);

        stepsFromSource += 0.42;

        propagateStepsTo(i + 1, j - 1, stepsFromSource);
        propagateStepsTo(i - 1, j - 1, stepsFromSource);
        propagateStepsTo(i + 1, j + 1, stepsFromSource);
        propagateStepsTo(i - 1, j + 1, stepsFromSource);
    }

    private void propagateStepsTo(int i, int j, double steps) {
        MapWater water = getWaterObject(i, j);
        if (water != null) {
            water.setStepsFromSource(steps);
        }
    }

}
