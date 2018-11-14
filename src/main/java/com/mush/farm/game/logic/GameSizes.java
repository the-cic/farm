/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.logic;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 *
 * @author mush
 */
public class GameSizes {

    public static final int TILE_SIZE = 16;

    public static Point getTileCoordinates(double x, double y) {
        int u = (int) (x / TILE_SIZE);
        int v = (int) ((y + TILE_SIZE) / TILE_SIZE);
        return new Point(u, v);
    }

    public static Point getTileCoordinates(Point2D.Double position) {
        return getTileCoordinates(position.x, position.y);
    }

    public static Point getTileCenterPosition(int u, int v) {
        int x = u * TILE_SIZE + TILE_SIZE / 2;
        int y = v * TILE_SIZE - TILE_SIZE / 2;
        return new Point(x, y);
    }

}
