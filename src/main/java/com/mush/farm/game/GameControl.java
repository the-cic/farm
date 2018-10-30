/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game;

/**
 *
 * @author cic
 */
public class GameControl {

    private int xJoystick = 0;
    private int yJoystick = 0;
    private boolean modified = false;
    
    public int getXJoystick() {
        return xJoystick;
    }
    
    public int getYJoystick() {
        return yJoystick;
    }
    
    public boolean isModified() {
        boolean value = modified;
        modified = false;
        return value;
    }

    public void pushLeft() {
        xJoystick = -1;
        modified = true;
    }

    public void pushRight() {
        xJoystick = 1;
        modified = true;
    }

    public void pushUp() {
        yJoystick = -1;
        modified = true;
    }

    public void pushDown() {
        yJoystick = 1;
        modified = true;
    }

    public void releaseLeft() {
        xJoystick = xJoystick == -1 ? 0 : xJoystick;
        modified = true;
    }

    public void releaseRight() {
        xJoystick = xJoystick == 1 ? 0 : xJoystick;
        modified = true;
    }

    public void releaseUp() {
        yJoystick = yJoystick == -1 ? 0 : yJoystick;
        modified = true;
    }

    public void releaseDown() {
        yJoystick = yJoystick == 1 ? 0 : yJoystick;
        modified = true;
    }

}
