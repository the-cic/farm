/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.events;

/**
 * Temporary, will remove
 *
 * @author mush
 */
public class GenericGameEvent extends GameEvent {

    public final String eventName;
    public final Object eventPayload;

    public GenericGameEvent(String name) {
        this(name, null);
    }

    public GenericGameEvent(String name, Object payload) {
        this.eventName = name;
        this.eventPayload = payload;
    }

}
