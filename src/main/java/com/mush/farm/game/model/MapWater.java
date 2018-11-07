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

    private double age;
    private static final double drainAge = 2.0;

    private double value = 0;
    public static final int MAX_STEPS_FROM_SOURCE = 5;
    private double stepsFromSource = MAX_STEPS_FROM_SOURCE;
    private double nextStepsFromSource = MAX_STEPS_FROM_SOURCE;
    
    public MapWater() {
    }

    public void update(double elapsedSeconds) {
        if (stepsFromSource < MAX_STEPS_FROM_SOURCE) {
            double factor = (MAX_STEPS_FROM_SOURCE - stepsFromSource) / MAX_STEPS_FROM_SOURCE;
            value = 0.5 + 0.5 * factor;
            age = 0;
            return;
        }
        if (value <= 0) {
            value = 0;
            return;
        }
        age += elapsedSeconds;
        if (age > drainAge) {
            value = 0;
            age = 0;
        }
    }

    public double getValue() {
        return value;
    }

    public void setStepsFromSource(double steps) {
        if (steps < nextStepsFromSource) {
            nextStepsFromSource = steps;
        }
    }

    public void applyStepsFromSource() {
        stepsFromSource = nextStepsFromSource;
        nextStepsFromSource = MAX_STEPS_FROM_SOURCE;
    }

    public double getStepsFromSource() {
        return stepsFromSource < MAX_STEPS_FROM_SOURCE
                ? stepsFromSource
                : MAX_STEPS_FROM_SOURCE;
    }

}
