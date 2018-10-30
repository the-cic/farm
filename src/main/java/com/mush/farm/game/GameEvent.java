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
public class GameEvent {
    public final String eventName;
    public final Object eventPayload;
    
    public GameEvent(String name, Object payload) {
        this.eventName = name;
        this.eventPayload = payload;
    }
}
