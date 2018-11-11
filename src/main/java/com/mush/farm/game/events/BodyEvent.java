/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.events;

import com.mush.farm.game.GameEvent;
import com.mush.farm.game.model.Body;
import com.mush.farm.game.model.BodyType;

/**
 *
 * @author mush
 */
public class BodyEvent extends GameEvent {

    public final int bodyId;
    // get rid of this when bodies have ids
    public final Body body;

    public BodyEvent(Body body) {
        this.bodyId = 0; // body.id;
        this.body = body;
    }

    public static class ChangeType extends BodyEvent {

        public final BodyType type;

        public ChangeType(Body body, BodyType type) {
            super(body);
            this.type = type;
        }
    }
}
