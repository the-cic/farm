/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.events;

import com.mush.farm.game.GameEvent;
import com.mush.farm.game.model.MovableCharacter;

/**
 *
 * @author mush
 */
public abstract class CharacterEvent extends GameEvent {
    
    public final int characterId;
    
    public CharacterEvent(MovableCharacter character) {
        this.characterId = character.characterId;
    }
    
    public static class Interact extends CharacterEvent {

        public Interact(MovableCharacter character) {
            super(character);
        }
    }
    
    public static class PickUp extends CharacterEvent {

        public PickUp(MovableCharacter character) {
            super(character);
        }
    }
    
    public static class Drop extends CharacterEvent {

        public Drop(MovableCharacter character) {
            super(character);
        }
    }
    
    public static class Equip extends CharacterEvent {
        
        public final int inventoryIndex;

        public Equip(MovableCharacter character, int inventoryIndex) {
            super(character);
            this.inventoryIndex = inventoryIndex;
        }
    }
    
    public static class Unequip extends CharacterEvent {

        public Unequip(MovableCharacter character) {
            super(character);
        }
    }
    
    public static class CycleInventory extends CharacterEvent {

        public CycleInventory(MovableCharacter character) {
            super(character);
        }
    }
    
}
