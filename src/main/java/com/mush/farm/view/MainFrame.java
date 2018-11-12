/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.view;

import com.mush.farm.game.Game;
import com.mush.farm.game.model.GameMap;
import com.mush.farm.game.render.GameRenderer;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author mush
 */
public class MainFrame extends JFrame {

    private Game game;
    private RefreshThread refreshThread;
    public MainPanel panel;

    public MainFrame() {
        super("Mush's desolate wasteland");
        init();
    }

    private void init() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        ImageIcon img = new ImageIcon("img/icon.png");
        setIconImage(img.getImage());

        game = new Game();
        addKeyListener(game.keyboardListener);

        panel = new MainPanel(game.renderer);
        panel.setVisible(true);

        int tileSize = GameRenderer.TILE_SIZE * GameRenderer.TILE_ZOOM;

        panel.setPreferredSize(new Dimension(GameMap.MAP_WIDTH * tileSize, GameMap.MAP_HEIGHT * tileSize));

        add(panel);

        pack();

        refreshThread = new RefreshThread(game, panel);
        new Thread(refreshThread).start();
    }

}
