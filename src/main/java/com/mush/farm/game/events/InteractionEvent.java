/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.events;

import com.mush.farm.game.GameEvent;
import com.mush.farm.game.model.Body;
import com.mush.farm.game.model.BodyType;
import com.mush.farm.game.model.MapObject;
import com.mush.farm.game.model.MapObjectType;
import com.mush.farm.game.model.MovableCharacter;

/**
 *
 * @author mush
 */
public class InteractionEvent extends GameEvent {
    
    public final int characterId;
    
    private InteractionEvent(int characterId) {
        this.characterId = characterId;
    }
    
    public static class BodyOnBody extends InteractionEvent {
        
        public final int toolId;
        public final int targetId;
        public final BodyType toolType;
        public final BodyType targetType;
        
        public BodyOnBody(MovableCharacter character, Body toolBody, Body targetBody) {
            super(character.characterId);
            this.toolId = toolBody.bodyId;
            this.toolType = toolBody.type;
            this.targetId = targetBody.bodyId;
            this.targetType = targetBody.type;
        }
    }
    
    public static class BodyOnMapObject extends InteractionEvent {
        
        public final int toolId;
        public final BodyType toolType;
        public final MapObjectType targetType;
        public final int targetU;
        public final int targetV;
        
        public BodyOnMapObject(MovableCharacter character, Body toolBody, MapObject targetMapObject) {
            super(character.characterId);
            this.toolId = toolBody.bodyId;
            this.toolType = toolBody.type;
            this.targetType = targetMapObject.type;
            this.targetU = targetMapObject.getU();
            this.targetV = targetMapObject.getV();
        }
    }
    
}
