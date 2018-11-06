/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.model;

/**
 *
 * @author mush
 */
public class MapWater {

    private double waterDistance;
    private double nextWaterDistance;
    private double age;
    private static final int maxDistance = 6;
    private static final double decreaseAge = 2.0;

    public MapWater() {
        waterDistance = maxDistance + 1;
        nextWaterDistance = waterDistance;
    }
    
    public void update(double elapsedSeconds) {
        if (waterDistance > maxDistance) {
            return;
        }
        age += elapsedSeconds;
        //waterDistance += (age / decreaseAge);
        if (age > decreaseAge) {
            nextWaterDistance = Math.min(waterDistance + 1, maxDistance + 1);
//            nextWaterDistance = waterDistance;
            age = 0;
        }
    }

    public double getValue() {
        if (waterDistance > maxDistance) {
            return 0;
        }
        return Math.min(3.0 / (waterDistance * 2 + 1), 1);
    }

    public void setNextDistance(double distance) {
        if (distance < nextWaterDistance) {
            nextWaterDistance = distance;
        }
    }
    
    public void applyNextDistance() {
        waterDistance = nextWaterDistance;
    }

    public double getDistance() {
        return waterDistance;
    }

}
