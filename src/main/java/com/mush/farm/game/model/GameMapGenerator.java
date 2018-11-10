/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.model;

import static com.mush.farm.game.model.GameMap.MAP_HEIGHT;
import static com.mush.farm.game.model.GameMap.MAP_WIDTH;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mush
 */
public class GameMapGenerator {

    private GameMap gameMap;

    public GameMapGenerator(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public void generate() {

        placeSeeds(gameMap);

        while (fillInMap() > 0) {
            // twiddle thumbs
        }
    }

    private void placeSeeds(GameMap gameMap) {
        int seeds = 100;

        List<MapObjectType> seedValues = new ArrayList<>();

        seedValues.add(MapObjectType.DIRT);
        seedValues.add(MapObjectType.DIRT);
        seedValues.add(MapObjectType.GRASS);
        seedValues.add(MapObjectType.GRASS);
        seedValues.add(MapObjectType.STONE_WALL);
        seedValues.add(MapObjectType.POTATO_PLANTED);
        seedValues.add(MapObjectType.WATER);

        for (int i = 0; i < seeds; i++) {
            int u = (int) (MAP_WIDTH * Math.random());
            int v = (int) (MAP_HEIGHT * Math.random());
            gameMap.setMapObject(u, v, seedValues.get((int) (Math.random() * seedValues.size())));
        }
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
        for (int v = 0; v < MAP_HEIGHT; v++) {
            for (int u = 0; u < MAP_WIDTH; u++) {
                MapObjectType type = gameMap.getMapObjectType(u, v);
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
            gameMap.setMapObject(operation.u, operation.v, operation.type);
        }
        return count;
    }

    private MapObjectType fillInTile(int u, int v) {
        Map<MapObjectType, Integer> histogram = new HashMap<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                MapObjectType tile = gameMap.getMapObjectType(u + i, v + j);
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

}
