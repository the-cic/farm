/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.view;

import com.mush.farm.game.Game;
import com.mush.farm.game.GameKeyboardListener;
import com.mush.farm.game.GameRenderer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author cic
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
//        setLayout(null);
        setSize(25 * 16 * 2 + 20, 25 * 16 * 2 + 50);
//        setResizable(false);

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
