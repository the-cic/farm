/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.view;

import com.mush.farm.game.Game;
import java.awt.Component;

/**
 *
 * @author cic
 */
public class RefreshThread implements Runnable {

    private Game game;
    private Component component;

    private boolean running = true;
    private int targetFps = 30;
    private long targetMillis = 1000 / targetFps;
    private double targetDuration = 1.0 / targetFps;

    public RefreshThread(Game game, Component component) {
        this.game = game;
        this.component = component;
    }

    @Override
    public void run() {
        long tick = System.currentTimeMillis();
        while (running) {
            long tock = System.currentTimeMillis();
            double elapsedSeconds = (tock - tick) / 1000.0;
            tick = tock;

            game.update(elapsedSeconds);
            component.repaint();

            tock = System.currentTimeMillis();
            long remainMillis = targetMillis - (tock - tick);

            if (remainMillis > 0) {
                try {
                    Thread.sleep(remainMillis);
                } catch (InterruptedException ex) {
                }
            }
        }
    }

}
