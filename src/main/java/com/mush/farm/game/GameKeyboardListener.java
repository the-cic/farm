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
 * @author cic
 */
public class GameKeyboardListener implements KeyListener {

    private Game game;

    public GameKeyboardListener(Game game) {
        this.game = game;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                game.control.pushLeft();
                break;
            case KeyEvent.VK_RIGHT:
                game.control.pushRight();
                break;
            case KeyEvent.VK_UP:
                game.control.pushUp();
                break;
            case KeyEvent.VK_DOWN:
                game.control.pushDown();
                break;
            case KeyEvent.VK_V:
                game.showStats = !game.showStats;
                break;
            case KeyEvent.VK_1:
                setTile(MapObjectType.DIRT);
                break;
            case KeyEvent.VK_2:
                setTile(MapObjectType.WATER);
                break;
            case KeyEvent.VK_3:
                setTile(MapObjectType.POTATO_PLANTED);
                break;
            case KeyEvent.VK_E:
                interact();
                break;
        }

        applyJoystick();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                game.control.releaseLeft();
                break;
            case KeyEvent.VK_RIGHT:
                game.control.releaseRight();
                break;
            case KeyEvent.VK_UP:
                game.control.releaseUp();
                break;
            case KeyEvent.VK_DOWN:
                game.control.releaseDown();
                break;
        }

        applyJoystick();
    }

    private void applyJoystick() {
        if (game.control.isModified()) {
            game.eventQueue.add(new GameEvent("applyJoystick", null));
        }
    }

    private void setTile(MapObjectType type) {
        game.eventQueue.add(new GameEvent("setTile", type));
    }
    
    private void interact() {
        game.eventQueue.add(new GameEvent("interact", null));
    }

}
