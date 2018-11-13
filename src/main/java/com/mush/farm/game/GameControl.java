/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game;

import com.mush.farm.game.events.ControlEvent;
import com.mush.farm.game.events.GenericGameEvent;
import com.mush.farm.game.model.Creature;

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
        GameEventQueue.send(new GenericGameEvent(name, payload));
    }

    void toggleShowStats() {
        GameEventQueue.send(new ControlEvent(ControlEvent.Action.TOGGLE_STATS));
    }

    void togglePause() {
        GameEventQueue.send(new ControlEvent(ControlEvent.Action.PAUSE));
    }

    void changeCreature() {
        GameEventQueue.send(new ControlEvent(ControlEvent.Action.CHANGE_CREATURE));
    }

    public void applyJoystick() {
        if (joystick.isModified()) {
            GameEventQueue.send(new ControlEvent(ControlEvent.Action.APPLY_JOYSTICK));
        }
    }

    public void actionPlayerInteract() {
        Creature player = game.getPlayer();
        if (player != null) {
            player.sendInteract();
        }
    }

    public void actionPlayerGive() {
        Creature player = game.getPlayer();
        if (player != null) {
            player.sendGive();
        }
    }

    public void actionPlayerPickUp() {
        Creature player = game.getPlayer();
        if (player != null) {
            player.sendPickUp();
        }
    }

    public void actionPlayerDrop() {
        Creature player = game.getPlayer();
        if (player != null) {
            player.sendDrop();
        }
    }

    public void actionPlayerEquip() {
        Creature player = game.getPlayer();
        if (player != null) {
            if (player.getEquipped() == null) {
                player.sendEquipLast();
            } else {
                player.sendUnequip();
            }
        }
    }
    
    public void actionCycleInventory() {
        Creature player = game.getPlayer();
        if (player != null) {
            player.sendCycleInventory();
        }
    }
    
}
