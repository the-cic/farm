/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.logic;

import com.mush.farm.game.Game;
import com.mush.farm.game.GameEventQueue;
import com.mush.farm.game.events.CreatureEvent;
import com.mush.farm.game.model.Body;
import com.mush.farm.game.model.Creature;
import com.mush.farm.game.model.MapObjectType;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 *
 * @author mush
 */
public class MovementLogic {

    private final Game game;

    public MovementLogic(Game game) {
        this.game = game;
    }

    public void onEvent(CreatureEvent.Move event) {
        Creature creature = game.creatures.getCreature(event.creatureId);
        if (creature != null) {
            move(creature, event.xOffset, event.yOffset);
        }
    }

    private void move(Creature creature, double xOffset, double yOffset) {
        Point2D.Double offset = new Point2D.Double(xOffset, yOffset);

        checkMove(creature, offset);

        creature.body.position.x += offset.x;
        creature.body.position.y += offset.y;
    }

    private void checkMove(Creature creature, Point2D.Double offset) {
        for (Body body : game.bodies.getBodies()) {
            if (body != creature.body) {
                if (checkMoveCollision(creature.body, body, offset)) {
                    GameEventQueue.send(new CreatureEvent.BodyCollision(creature, body));
                }
            }
        }

        if (!game.getMapCollisionsEnabled()) {
            return;
        }
        
        Body dummy = new Body(-1, null);
        dummy.setBoundingBox(GameSizes.TILE_SIZE / 2, GameSizes.TILE_SIZE / 2, GameSizes.TILE_SIZE / 2, GameSizes.TILE_SIZE / 2);

        Point centerTilePoint = GameSizes.getTileCoordinates(
                creature.body.position.x,
                creature.body.position.y
        );

        for (int j = -1; j <= 1; j++) {
            for (int i = -1; i <= 1; i++) {
                    MapObjectType type = game.gameMap.getMapObjectType(centerTilePoint.x + i, centerTilePoint.y + j);
                    if (MapObjectType.isCollidable(type)) {
                        Point p = GameSizes.getTileCenterPosition(centerTilePoint.x + i, centerTilePoint.y + j);
                        dummy.position.x = p.x;
                        dummy.position.y = p.y;
                        if (checkMoveCollision(creature.body, dummy, offset)) {
                            GameEventQueue.send(new CreatureEvent.MapCollision(creature, type));
                        }
                    }
            }
        }
    }

    private boolean checkMoveCollision(Body bodyA, Body bodyB, Point2D.Double offset) {
        if (bodyA.boundingBox == null || bodyB.boundingBox == null) {
            return false;
        }

        Body.BoundingBox boxA = actualBodyBox(bodyA);
        Body.BoundingBox boxB = actualBodyBox(bodyB);

        double bRightLead = boxB.right - boxA.left;
        double aRightLead = boxA.right - boxB.left;
        double bBottomLead = boxB.bottom - boxA.top;
        double aBottomLead = boxA.bottom - boxB.top;

        if (bRightLead > 0 && aRightLead > 0 && bBottomLead > 0 && aBottomLead > 0) {
            double correctionX = aRightLead < bRightLead ? -aRightLead : bRightLead;
            double correctionY = aBottomLead < bBottomLead ? -aBottomLead : bBottomLead;
            if (Math.abs(correctionX) > Math.abs(correctionY)) {
                offset.y += correctionY;
            } else {
                offset.x += correctionX;
            }
            return true;
        }
        return false;
    }

    private Body.BoundingBox actualBodyBox(Body body) {
        Body.BoundingBox box = new Body.BoundingBox(
                body.position.x - body.boundingBox.left,
                body.position.y - body.boundingBox.top,
                body.position.x + body.boundingBox.right,
                body.position.y + body.boundingBox.bottom
        );

        return box;
    }

}
