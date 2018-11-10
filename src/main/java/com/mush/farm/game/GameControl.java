/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game;

import com.mush.farm.game.events.ControlEvent;
import com.mush.farm.game.events.GenericGameEvent;
import com.mush.farm.game.model.MovableCharacter;

/**
 *
 * @author mush
 */
public class GameControl {

    private final Game game;
    public final GameControlJoystick joystick;

    GameControl(Game game) {
        this.game = game;
        joystick = new GameControlJoystick();
    }

    public void debugEvent(String name, Object payload) {
        game.eventQueue.add(new GenericGameEvent(name, payload));
    }

    void toggleShowStats() {
        game.eventQueue.add(new ControlEvent(ControlEvent.Action.TOGGLE_STATS));
    }

    void togglePause() {
        game.eventQueue.add(new ControlEvent(ControlEvent.Action.PAUSE));
    }

    void changeCharacter() {
        game.eventQueue.add(new ControlEvent(ControlEvent.Action.CHANGE_CHARACTER));
    }

    public void applyJoystick() {
        if (joystick.isModified()) {
            game.eventQueue.add(new ControlEvent(ControlEvent.Action.APPLY_JOYSTICK));
        }
    }

    public void actionPlayerInteract() {
        MovableCharacter player = game.getPlayer();
        if (player != null) {
            player.interact();
        }
    }

    public void actionPlayerDrop() {
        MovableCharacter player = game.getPlayer();
        if (player != null) {
            player.drop();
        }
    }

    public void actionPlayerEquip() {
        MovableCharacter player = game.getPlayer();
        if (player != null) {
            player.equip();
        }
    }

}
