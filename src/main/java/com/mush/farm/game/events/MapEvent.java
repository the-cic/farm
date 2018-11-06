/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.events;

import com.mush.farm.game.GameEvent;
import com.mush.farm.game.model.BodyType;
import com.mush.farm.game.model.MapObjectType;

/**
 *
 * @author mush
 */
public abstract class MapEvent extends GameEvent {

    public final int u;
    public final int v;

    private MapEvent(int u, int v) {
        this.u = u;
        this.v = v;
    }

    public static class Spread extends MapEvent {

        public final MapObjectType type;

        public Spread(int u, int v, MapObjectType type) {
            super(u, v);
            this.type = type;
        }

    }

    public static class SpawnOnTile extends MapEvent {

        public final BodyType type;

        public SpawnOnTile(int u, int v, BodyType type) {
            super(u, v);
            this.type = type;
        }

    }

}
