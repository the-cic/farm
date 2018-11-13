/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game;

import com.mush.farm.game.events.BodyEvent;
import com.mush.farm.game.events.InteractionEvent;
import com.mush.farm.game.model.BodyType;
import com.mush.farm.game.model.MapObjectType;

/**
 *
 * @author mush
 */
public class GameInteractionsLogic {

    private Game game;
    private GameEventQueue eventQueue;

    public GameInteractionsLogic(Game game, GameEventQueue eventQueue) {
        this.game = game;
        this.eventQueue = eventQueue;
    }

    public void onEvent(InteractionEvent.BodyOnBody event) {
        System.out.println("interaction by c.id:" + event.creatureId
                + " with tool.type:" + event.toolType
                + " on target.type:" + event.targetType);
    }

    public void onEvent(InteractionEvent.BodyOnMapObject event) {
        System.out.println("interaction by c.id:" + event.creatureId
                + " with tool.type:" + event.toolType
                + " on target.type:" + event.targetType);

        switch (event.toolType) {
            case BUCKET_EMPTY:
                bucketEmptyOnTile(event);
                break;
            case BUCKET_FULL:
                bucketFullOnTile(event);
                break;
            case SHOVEL:
                shovelOnTile(event);
                break;
        }
    }

    private void bucketFullOnTile(InteractionEvent.BodyOnMapObject event) {
        if (event.targetType == MapObjectType.DIRT_HOLE) {
            game.gameMap.setTile(event.targetU, event.targetV, MapObjectType.WATER);
            eventQueue.add(new BodyEvent.ChangeType(event.toolId, BodyType.BUCKET_EMPTY));
        }
    }

    private void bucketEmptyOnTile(InteractionEvent.BodyOnMapObject event) {
        if (event.targetType == MapObjectType.WATER) {
            eventQueue.add(new BodyEvent.ChangeType(event.toolId, BodyType.BUCKET_FULL));
        }
    }
    
    private void shovelOnTile(InteractionEvent.BodyOnMapObject event) {
        if (event.targetType == MapObjectType.DIRT || event.targetType == MapObjectType.GRASS || event.targetType == MapObjectType.ORGANIC_RUBBLE) {
            game.gameMap.setTile(event.targetU, event.targetV, MapObjectType.DIRT_HOLE);
        }
    }

}
