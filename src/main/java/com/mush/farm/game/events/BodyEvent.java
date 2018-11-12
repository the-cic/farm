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

    public BodyEvent(Body body) {
        this.bodyId = body.bodyId;
    }
    
    public BodyEvent(int bodyId) {
        this.bodyId = bodyId;
    }

    public static class ChangeType extends BodyEvent {

        public final BodyType type;

        public ChangeType(Body body, BodyType type) {
            super(body);
            this.type = type;
        }
        
        public ChangeType(int bodyId, BodyType type) {
            super(bodyId);
            this.type = type;
        }
    }
}
