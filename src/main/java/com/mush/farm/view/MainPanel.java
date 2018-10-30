/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.view;

import com.mush.farm.game.GameRenderer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author cic
 */
public class MainPanel extends JPanel {

    private GameRenderer renderer;

    MainPanel(GameRenderer renderer) {
        super();
        this.renderer = renderer;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.green);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        renderer.render((Graphics2D) g);
    }

}
