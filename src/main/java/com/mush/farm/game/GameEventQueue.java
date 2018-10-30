/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author cic
 */
public class GameEventQueue {

    private List<GameEventListener> listeners;
    private LinkedList<GameEvent> queue;

    public GameEventQueue() {
        listeners = new ArrayList<>();
        queue = new LinkedList<>();
    }

    public void add(GameEvent event) {
        queue.add(event);
    }
    
    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    public void process() {
        while (!queue.isEmpty()) {
            GameEvent event = queue.removeFirst();
            process(event);
        }
    }

    private void process(GameEvent event) {
        for (GameEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

}
