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
    
    // TODO: keep references out of events
    // to do that, handle ids of bodies
    public static class BodyOnBody extends InteractionEvent {
        
        public final Body tool;
        public final Body target;
        public final BodyType toolType;
        public final BodyType targetType;
        
        public BodyOnBody(MovableCharacter character, Body toolBody, Body targetBody) {
            super(character.characterId);
            this.tool = toolBody;
            this.toolType = toolBody.type;
            this.target = targetBody;
            this.targetType = targetBody.type;
        }
    }
    
    public static class BodyOnMapObject extends InteractionEvent {
        
        public final Body tool;
        public final BodyType toolType;
        public final MapObjectType targetType;
        public final int targetU;
        public final int targetV;
        
        public BodyOnMapObject(MovableCharacter character, Body toolBody, MapObject targetMapObject) {
            super(character.characterId);
            this.tool = toolBody;
            this.toolType = toolBody.type;
            this.targetType = targetMapObject.type;
            this.targetU = targetMapObject.getU();
            this.targetV = targetMapObject.getV();
        }
    }
    
}
