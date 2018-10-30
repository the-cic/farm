/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.render;

import com.mush.farm.game.model.Body;
import java.util.Comparator;

/**
 *
 * @author mush
 */
public class BodyComparator implements Comparator<Body> {

    @Override
    public int compare(Body body1, Body body2) {
        return Double.compare(body1.position.y, body2.position.y);
    }

}
