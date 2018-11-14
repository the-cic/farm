/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.logic;

import com.mush.farm.game.events.BodyEvent;
import com.mush.farm.game.events.MapEvent;
import com.mush.farm.game.model.Body;
import com.mush.farm.game.model.BodyType;
import com.mush.farm.game.model.GameBodies;
import com.mush.farm.game.model.GameMap;
import java.awt.Point;

/**
 *
 * @author mush
 */
public class GameMapEventLogic {

    private final GameMap gameMap;
    private final GameBodies bodies;

    public GameMapEventLogic(GameMap gameMap, GameBodies bodies) {
        this.gameMap = gameMap;
        this.bodies = bodies;
    }

    public void onEvent(MapEvent.Spread event) {
        gameMap.spread(event.u, event.v, event.type);
    }

    public void onEvent(MapEvent.SpawnOnTile event) {
        spawnOnTile(event.u, event.v, event.type);
    }

    public void onEvent(BodyEvent.ChangeType event) {
        Body body = bodies.getBody(event.bodyId);
        if (body != null) {
            body.type = event.type;
        }
    }

    private void spawnOnTile(int u, int v, BodyType type) {
        Point point = GameSizes.getTileCenterPosition(u, v);
        bodies.spawnBody(type, point.x, point.y);
    }

}
