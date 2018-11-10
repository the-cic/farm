/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.events;

import com.mush.farm.game.GameEvent;
import com.mush.farm.game.model.Body;
import com.mush.farm.game.model.MapObject;
import com.mush.farm.game.model.MovableCharacter;

/**
 *
 * @author mush
 */
public class InteractionEvent extends GameEvent {
    
    public final MovableCharacter character;
    
    private InteractionEvent(MovableCharacter character) {
        this.character = character;
    }
    
    public static class BodyOnBody extends InteractionEvent {
        
        public final Body tool;
        public final Body target;
        
        public BodyOnBody(MovableCharacter character, Body toolBody, Body targetBody) {
            super(character);
            this.tool = toolBody;
            this.target = targetBody;
        }
    }
    
    public static class BodyOnMapObject extends InteractionEvent {
        
        public final Body tool;
        public final MapObject target;
        
        public BodyOnMapObject(MovableCharacter character, Body toolBody, MapObject targetMapObject) {
            super(character);
            this.tool = toolBody;
            this.target = targetMapObject;
        }
    }
    
}
