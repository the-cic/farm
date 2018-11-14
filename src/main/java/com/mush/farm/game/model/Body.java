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
 * @author mush
 */
public class Body {

    public static class BoundingBox {

        public double left;
        public double right;
        public double top;
        public double bottom;

        public BoundingBox(double left, double top, double right, double bottom) {
            set(left, top, right, bottom);
        }
        
        public BoundingBox(BoundingBox box) {
            set(box);
        }
        
        public void set(double left, double top, double right, double bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
        
        public void set(BoundingBox box) {
            set(box.left, box.top, box.right, box.bottom);
        }
    }

    public final int bodyId;
    public final Point2D.Double position = new Point2D.Double(0, 0);
    public BodyType type;
    public Creature creature;
    public List<Body> containedBodies;
    public BoundingBox boundingBox = null;

    public Body(int id, BodyType type) {
        this.bodyId = id;
        this.type = type;
        this.containedBodies = new ArrayList<>();
        BodyType.setBoundingBox(this);
    }

    public Body(int id, BodyType type, Creature creature) {
        this(id, type);
        this.creature = creature;
    }

    public void setBoundingBox(int left, int top, int right, int bottom) {
        if (boundingBox == null) {
            boundingBox = new BoundingBox(left, top, right, bottom);
        } else {
            boundingBox.set(left, top, right, bottom);
        }
    }
    
    public void setBoundingBox(BoundingBox box) {
        if (box == null) {
            boundingBox = null;
            return;
        }
        if (boundingBox == null) {
            boundingBox = new BoundingBox(box);
        } else {
            boundingBox.set(box);
        }
    }

}
