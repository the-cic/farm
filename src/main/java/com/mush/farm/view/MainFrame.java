/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.view;

import com.mush.farm.game.Game;
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
        setSize(25 * 16 * 2 + 20, 25 * 16 * 2 + 50);

        ImageIcon img = new ImageIcon("./icon.png");
        setIconImage(img.getImage());

        game = new Game();
        addKeyListener(game.keyboardListener);

        panel = new MainPanel(game.renderer);
        panel.setVisible(true);

        add(panel);

        refreshThread = new RefreshThread(game, panel);
        new Thread(refreshThread).start();
    }

}
