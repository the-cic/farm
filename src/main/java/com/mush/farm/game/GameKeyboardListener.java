/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game;

import com.mush.farm.game.model.MapObjectType;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author mush
 */
public class GameKeyboardListener implements KeyListener {

    private final GameControl control;

    public GameKeyboardListener(GameControl control) {
        this.control = control;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                control.joystick.pushLeft();
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                control.joystick.pushRight();
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                control.joystick.pushUp();
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                control.joystick.pushDown();
                break;
            case KeyEvent.VK_V:
                control.toggleShowStats();
                break;
            case KeyEvent.VK_C:
                control.toggleCollisions();
                break;
            case KeyEvent.VK_1:
                control.debugEvent("setTile", MapObjectType.DIRT);
                break;
            case KeyEvent.VK_2:
                control.debugEvent("setTile", MapObjectType.WATER);
                break;
            case KeyEvent.VK_3:
                control.debugEvent("setTile", MapObjectType.POTATO_PLANTED);
                break;
            case KeyEvent.VK_4:
                control.debugEvent("setTile", MapObjectType.STONE_WALL);
                break;
            case KeyEvent.VK_5:
                control.debugEvent("setTile", MapObjectType.DIRT_HOLE);
                break;
            case KeyEvent.VK_E:
                control.actionPlayerPickUp();
                break;
            case KeyEvent.VK_Q:
                control.actionPlayerDrop();
                break;
            case KeyEvent.VK_R:
                control.actionPlayerEquip();
                break;
            case KeyEvent.VK_F:
                control.actionPlayerInteract();
                break;
            case KeyEvent.VK_G:
                control.actionPlayerGive();
                break;
            case KeyEvent.VK_I:
                control.actionCycleInventory();
                break;
            case KeyEvent.VK_P:
                control.togglePause();
                break;
            case KeyEvent.VK_T:
                control.changeCreature();
                break;
        }

        control.applyJoystick();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                control.joystick.releaseLeft();
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                control.joystick.releaseRight();
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                control.joystick.releaseUp();
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                control.joystick.releaseDown();
                break;
        }

        control.applyJoystick();
    }

}
