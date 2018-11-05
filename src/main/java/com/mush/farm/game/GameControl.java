/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game;

import com.mush.farm.game.model.MovableCharacter;

/**
 *
 * @author mush
 */
public class GameControl {

    public static final String E_APPLY_JOYSTICK = "applyJoystick";

    private final Game game;
    public final GameControlJoystick joystick;

    GameControl(Game game) {
        this.game = game;
        joystick = new GameControlJoystick();
    }

    public void debugEvent(String name, Object payload) {
        game.eventQueue.add(new GameEvent(name, payload));
    }

    void toggleShowStats() {
        game.showStats = !game.showStats;
    }

    void togglePause() {
        game.togglePause();
    }
    
    void changeCharacter() {
        game.changeCharacter();
    }

    public void applyJoystick() {
        if (joystick.isModified()) {
            game.eventQueue.add(new GameEvent(GameControl.E_APPLY_JOYSTICK, null));
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

}
