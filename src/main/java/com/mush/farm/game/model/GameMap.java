/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.model;

import com.mush.farm.game.GameEventQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author cic
 */
public class GameMap {

    private MapObject[] mapObjects;
    private MapWater[] waterMap;
    private List<Body> mapBodies;

    public final int mapWidth = 25;
    public final int mapHeight = 25;

    public GameMap() {
        createMap();
    }

    private void createMap() {
        int size = mapWidth * mapHeight;
        int seeds = 100;

        mapObjects = new MapObject[size];
        waterMap = new MapWater[size];

        for (int i = 0; i < size; i++) {
            mapObjects[i] = null;
            waterMap[i] = new MapWater();
        }
        MapObjectType[] values = MapObjectType.values();
        for (int i = 0; i < seeds; i++) {
            int u = (int) (mapWidth * Math.random());
            int v = (int) (mapHeight * Math.random());
            setMapObject(u, v, values[(int) (Math.random() * values.length)]);
        }
        while (fillInMap() > 0) {
            // twiddle thumbs
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

    private class FillInOperation {

        int u;
        int v;
        MapObjectType type;

        private FillInOperation(int u, int v, MapObjectType type) {
            this.u = u;
            this.v = v;
            this.type = type;
        }
    }

    private int fillInMap() {
        int count = 0;
        List<FillInOperation> operations = new ArrayList<>();
        for (int v = 0; v < mapHeight; v++) {
            for (int u = 0; u < mapWidth; u++) {
                MapObjectType type = getMapObjectType(u, v);
                if (type == null) {
                    type = fillInTile(u, v);
                    if (type != null) {
                        operations.add(new FillInOperation(u, v, type));
                        count++;
                    }
                }
            }
        }
        for (FillInOperation operation : operations) {
            setMapObject(operation.u, operation.v, operation.type);
        }
        return count;
    }

    private MapObjectType fillInTile(int u, int v) {
        Map<MapObjectType, Integer> histogram = new HashMap<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                MapObjectType tile = getMapObjectType(u + i, v + j);
                if (tile != null) {
                    if (histogram.containsKey(tile)) {
                        histogram.put(tile, histogram.get(tile) + 1);
                    } else {
                        histogram.put(tile, 1);
                    }
                }
            }
        }
        if (histogram.isEmpty()) {
            return null;
        }

        Collection<Integer> values = histogram.values();
        int total = 0;
        for (int value : values) {
            total += value;
        }
        int target = (int) (Math.random() * total);
        int count = 0;
        MapObjectType tile = null;
        Set<MapObjectType> keys = histogram.keySet();
        for (MapObjectType type : keys) {
            count += histogram.get(type);
            if (count >= target) {
                tile = type;
                break;
            }
        }

        return tile;
    }

    public MapObject getMapObject(int u, int v) {
        if (u >= 0 && u < mapWidth && v >= 0 && v < mapHeight) {
            return mapObjects[u + v * mapHeight];
        }
        return null;
    }

    public void setMapObject(int u, int v, MapObject object) {
        mapObjects[u + v * mapHeight] = object;
    }

    public MapObjectType getMapObjectType(int u, int v) {
        MapObject object = getMapObject(u, v);
        return object != null ? object.type : null;
    }

    public void setMapObject(int u, int v, MapObjectType type) {
        setMapObject(u, v, new MapObject(type, u, v));
    }

    public MapWater getWaterObject(int u, int v) {
        if (u >= 0 && u < mapWidth && v >= 0 && v < mapHeight) {
            return waterMap[u + v * mapHeight];
        }
        return null;
    }

    public void update(double elapsedSeconds, GameEventQueue eventQueue) {
        for (int i = 0; i < mapObjects.length; i++) {
            mapObjects[i].update(elapsedSeconds, waterMap[i], eventQueue);
        }
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                MapWater water = getWaterObject(i, j);
                water.update(elapsedSeconds);
                propagate(water, i, j);
            }
        }
    }

    private void propagate(MapWater water, int i, int j) {
        double distance = water.getDistance() + 1;

        propagateTo(i + 1, j + 0, distance);
        propagateTo(i - 1, j + 0, distance);
        propagateTo(i + 0, j + 1, distance);
        propagateTo(i + 0, j - 1, distance);
        
        distance += 0.42;
        
        propagateTo(i + 1, j - 1, distance);
        propagateTo(i - 1, j - 1, distance);
        propagateTo(i + 1, j + 1, distance);
        propagateTo(i - 1, j + 1, distance);
    }

    private void propagateTo(int i, int j, double distance) {
        MapWater water = getWaterObject(i, j);
        if (water != null) {
            water.setDistance(distance);
        }
    }

}
