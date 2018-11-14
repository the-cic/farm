/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mush
 */
public enum BodyType {
    PERSON,
    POTATO,
    BUCKET_EMPTY,
    BUCKET_FULL,
    SHOVEL;

    public static final Map<BodyType, Body.BoundingBox> boundingBoxMap;

    static {
        boundingBoxMap = new HashMap<>();
        
        setBoundingBox(PERSON, 7, 8, 7, 0);
    }

    private static void setBoundingBox(BodyType type, int left, int top, int right, int bottom) {
        Body.BoundingBox box = new Body.BoundingBox(left, top, right, bottom);
        boundingBoxMap.put(type, box);
    }

    public static void setBoundingBox(Body body) {
        Body.BoundingBox box = boundingBoxMap.get(body.type);
        body.setBoundingBox(box);
    }
}
