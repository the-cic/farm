/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game;

import com.mush.farm.game.events.GameEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mush
 */
public class GameEventQueue {

    private static GameEventQueue instance;

    private List<Object> listeners;
    private LinkedList<GameEvent> queue;
    private Map<Class<?>, Map<Class<?>, Method>> listenerEventMethodMap;

    static {
        instance = new GameEventQueue();
    }

    private GameEventQueue() {
        listeners = new ArrayList<>();
        listenerEventMethodMap = new HashMap<>();
        queue = new LinkedList<>();
    }

    public static void send(GameEvent event) {
        instance.sendEvent(event);
    }

    public static void addListener(Object listener) {
        instance.addEventListener(listener);
    }

    public static void removeListener(Object listener) {
        instance.removeEventListener(listener);
    }

    public static void processQueue() {
        instance.process();
    }

    private void sendEvent(GameEvent event) {
        queue.add(event);
    }

    private void addEventListener(Object listener) {
        listeners.add(listener);
        collectOnEventMethods(listener);
    }

    private void removeEventListener(Object listener) {
        listeners.remove(listener);
    }

    private void process() {
        try {
            LinkedList<GameEvent> oldQueue = (LinkedList<GameEvent>) queue.clone();
            queue.clear();

            for (GameEvent event : oldQueue) {
                process(event);
            }
            oldQueue.clear();

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    private void process(GameEvent event) {
        for (Object listener : listeners) {
            invokeOnEvent(event, listener);
        }
    }

    private void invokeOnEvent(GameEvent event, Object listener) {
        try {
            Method onEvent = getOnEventMethod(event, listener);

            if (onEvent != null) {
                onEvent.invoke(listener, event);
            }

        } catch (SecurityException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {

            ex.printStackTrace(System.out);
        }
    }

    private Method getOnEventMethod(GameEvent event, Object listener) {
        Map<Class<?>, Method> eventMethodMap = listenerEventMethodMap.get(listener.getClass());
        if (eventMethodMap == null) {
            return null;
        }
        return eventMethodMap.get(event.getClass());
    }

    private void collectOnEventMethods(Object listener) {
        Class<?> listenerClass = listener.getClass();

        try {
            Method[] methods = listenerClass.getMethods();
            for (Method method : methods) {
                checkListenerMethod(listenerClass, method);
            }

        } catch (SecurityException | IllegalArgumentException ex) {
            //
        }
    }

    private void checkListenerMethod(Class<?> listenerClass, Method method) {
        // At one point maybe use annotations instead, but no hurry
        if ("onEvent".equals(method.getName())) {
            if (method.getParameterCount() == 1) {
                Class<?>[] params = method.getParameterTypes();
                Class<?> paramClass = params[0];
                if (GameEvent.class.isAssignableFrom(paramClass)) {
                    addListenerEventMethod(listenerClass, paramClass, method);
                }
            }
        }
    }

    private void addListenerEventMethod(Class<?> listenerClass, Class<?> eventClass, Method method) {
        Map<Class<?>, Method> eventMethodMap = listenerEventMethodMap.get(listenerClass);
        if (eventMethodMap == null) {
            eventMethodMap = new HashMap<>();
            listenerEventMethodMap.put(listenerClass, eventMethodMap);
        }
        eventMethodMap.put(eventClass, method);
    }

}
