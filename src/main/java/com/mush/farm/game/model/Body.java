/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cic
 */
public class Body {

    public final Point2D.Double position = new Point2D.Double(0, 0);
    public BodyType type;
    public List<Body> containedBodies;

    public Body(BodyType type) {
        this.type = type;
        this.containedBodies = new ArrayList<>();
    }
    
}
