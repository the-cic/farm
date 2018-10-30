/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.model;

/**
 *
 * @author cic
 */
public class MapWater {

    private double waterDistance;
    private double age;
    private static final int maxDistance = 5;
    private static final double decreaseAge = 2.0;

    public MapWater() {
        waterDistance = maxDistance + 1;
    }
    
    public void update(double elapsedSeconds) {
        if (waterDistance > maxDistance) {
            return;
        }
        age += elapsedSeconds;
        //waterDistance += (age / decreaseAge);
        if (age > decreaseAge) {
            waterDistance = Math.min(waterDistance + 1, maxDistance + 1);
            age = 0;
        }
    }

    public double getValue() {
        if (waterDistance > maxDistance) {
            return 0;
        }
        return Math.min(3.0 / (waterDistance * 2 + 1), 1);
    }

    public void setDistance(double distance) {
        if (distance < waterDistance) {
            waterDistance = distance;
        }
    }

    public double getDistance() {
        return waterDistance;
    }

}
