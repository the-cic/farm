/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author mush
 */
public abstract class GameEventHandleHelper {

    public static void handle(GameEvent event, GameEventListener listener) {
        Class<?> eventClass = event.getClass();
        Class<?> listenerClass = listener.getClass();

        try {
            Method method = listenerClass.getMethod("onEvent", eventClass);

            method.invoke(listener, event);

        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            //
        }
    }

}
