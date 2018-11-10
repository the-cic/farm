/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.model;

import static com.mush.farm.game.model.BodyType.POTATO;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mush
 */
public enum MapObjectType {
    DIRT,
    DIRT_HOLE,
    ORGANIC_RUBBLE,
    STONE_RUBBLE,
    GRASS,
    WATER,
    POTATO_PLANTED,
    POTATO_SAPLING,
    POTATO_PLANT,
    STONE_WALL,
//    WOOD_WALL
    ;

    public static final Map<MapObjectType, MapObjectType> decayMap;
    public static final Map<MapObjectType, MapObjectType> evolveMap;
    public static final Map<MapObjectType, MapObjectType> spreadOnEvolveMap;
    public static final Map<MapObjectType, Set<MapObjectType>> canSpreadIntoMap;
    public static final Map<MapObjectType, BodyType> spawnOnEvolveMap;
    public static final Map<MapObjectType, Double> decayRateMap;
    public static final Map<MapObjectType, Double> evolveAgeMap;
    public static final Map<MapObjectType, double[]> waterDecayParamsMap;
    public static final Map<MapObjectType, Boolean> waterBlockingMap;

    static {
        decayMap = new HashMap<>();
        decayRateMap = new HashMap<>();

        evolveMap = new HashMap<>();
        evolveAgeMap = new HashMap<>();
        spreadOnEvolveMap = new HashMap<>();
        canSpreadIntoMap = new HashMap<>();
        spawnOnEvolveMap = new HashMap<>();
        waterDecayParamsMap = new HashMap<>();
        waterBlockingMap = new HashMap<>();

        setDecay(POTATO_PLANTED, ORGANIC_RUBBLE, 10.0);
        setDecay(POTATO_SAPLING, ORGANIC_RUBBLE, 20.0);
        setDecay(POTATO_PLANT, ORGANIC_RUBBLE, 20.0);
//        setDecay(WOOD_WALL, ORGANIC_RUBBLE, 60.0);
        setDecay(STONE_WALL, STONE_RUBBLE, 120.0);
        setDecay(ORGANIC_RUBBLE, DIRT, 120.0);
        setDecay(GRASS, DIRT, 5.0);

        setEvolve(POTATO_PLANTED, POTATO_SAPLING, 15.0);
        setEvolve(POTATO_SAPLING, POTATO_PLANT, 15.0);
        setEvolveAndSpawn(POTATO_PLANT, ORGANIC_RUBBLE, POTATO, 15.0);
        setEvolveAndSpread(GRASS, GRASS, GRASS, 10);
        setCanSpreadInto(GRASS, DIRT);
        setCanSpreadInto(GRASS, ORGANIC_RUBBLE);
        setEvolveAndSpread(WATER, WATER, WATER, 0.01);
        setCanSpreadInto(WATER, DIRT_HOLE);

        setWaterDecayMultiplayerParams(POTATO_SAPLING, 1, -1);
        setWaterDecayMultiplayerParams(POTATO_PLANTED, 1, -1);
        setWaterDecayMultiplayerParams(POTATO_PLANT, 1, -1);
        
        setWaterDecayMultiplayerParams(GRASS, 1, -1);
        
        setWaterDecayMultiplayerParams(STONE_WALL, 0, 1);
        
        setBlocksWater(STONE_WALL);
    }

    public static MapObjectType decay(MapObjectType from) {
        MapObjectType result = decayMap.get(from);
        return result != null ? result : from;
    }

    public static MapObjectType evolve(MapObjectType from) {
        MapObjectType result = evolveMap.get(from);
        return result != null ? result : from;
    }
    
    public static MapObjectType spread(MapObjectType from) {
        MapObjectType result = spreadOnEvolveMap.get(from);
        return result;
    }
    
    public static boolean canSpreadInto(MapObjectType type, MapObjectType intoType) {
        Set<MapObjectType> set = canSpreadIntoMap.get(type);
        if (set.isEmpty()) {
            return false;
        }
        return set.contains(intoType);
    }
    
    public static BodyType spawn(MapObjectType from) {
        return spawnOnEvolveMap.get(from);
    }

    public static double getDecayRate(MapObjectType type) {
        Double rate = decayRateMap.get(type);
        return rate != null ? rate : 0;
    }
    
    public static boolean blocksWater(MapObjectType type) {
        Boolean blocks = waterBlockingMap.get(type);
        return Boolean.TRUE.equals(blocks);
    }

    public static double getWaterDecayMultiplier(MapObjectType type, double water) {
        double[] params = waterDecayParamsMap.get(type);
        if (params == null) {
            return 1;
        }
        if (params.length == 2) {
            return calculateWaterDecayMultiplyer(water, params[0], params[1]);
        } else {
            return calculateWaterDecayMultiplyer(water, params[0], params[1], params[2]);
        }
    }

    private static double calculateWaterDecayMultiplyer(double water, double scale, double xOfs, double yOfs) {
        double x = water - xOfs;
        return (x * x / scale) + yOfs;
    }

    private static double calculateWaterDecayMultiplyer(double water, double zeroValue, double oneValue) {
        return zeroValue * (1 - water) + oneValue * water;
    }

    public static double getEvolveAge(MapObjectType type) {
        Double evolveAge = evolveAgeMap.get(type);
        return evolveAge != null ? evolveAge : Double.POSITIVE_INFINITY;
    }

    public static void setWaterDecayMultiplayerParams(MapObjectType type, double scale, double xOfs, double yOfs) {
        waterDecayParamsMap.put(type, new double[]{scale, xOfs, yOfs});
    }

    public static void setWaterDecayMultiplayerParams(MapObjectType type, double zeroValue, double oneValue) {
        waterDecayParamsMap.put(type, new double[]{zeroValue, oneValue});
    }

    public static void setDecay(MapObjectType decayFrom, MapObjectType decayTo, double decayTime) {
        decayMap.put(decayFrom, decayTo);
        decayRateMap.put(decayFrom, 1 / decayTime);
    }

    public static void setEvolve(MapObjectType evolveFrom, MapObjectType evolveTo, double evolveAge) {
        evolveMap.put(evolveFrom, evolveTo);
        evolveAgeMap.put(evolveFrom, evolveAge);
    }
    
    public static void setEvolveAndSpread(MapObjectType evolveFrom, MapObjectType evolveTo, MapObjectType spreadAs, double evolveAge) {
        setEvolve(evolveFrom, evolveTo, evolveAge);
        spreadOnEvolveMap.put(evolveFrom, spreadAs);
    }
    
    public static void setCanSpreadInto(MapObjectType type, MapObjectType spreadInto) {
        Set<MapObjectType> set = canSpreadIntoMap.get(type);
        if (set == null) {
            set = new HashSet<>();
            canSpreadIntoMap.put(type, set);
        }
        set.add(spreadInto);
    }

    private static void setEvolveAndSpawn(MapObjectType evolveFrom, MapObjectType evolveTo, BodyType spawnOne, double evolveAge) {
        setEvolve(evolveFrom, evolveTo, evolveAge);
        spawnOnEvolveMap.put(evolveFrom, spawnOne);
    }
    
    private static void setBlocksWater(MapObjectType type) {
        waterBlockingMap.put(type, true);
    }
    
}
