/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.events;

/**
 *
 * @author mush
 */
public class ControlEvent extends GameEvent {
    
    public static enum Action {
        PAUSE,
        TOGGLE_STATS,
        APPLY_JOYSTICK,
        CHANGE_CREATURE
    }
    
    public final Action action;
    
    public ControlEvent(Action action) {
        this.action = action;
    }
    
}
