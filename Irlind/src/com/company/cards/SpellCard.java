package com.company.cards;

import enums.CardType;
import enums.ElementType;
import enums.MonsterType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SpellCard extends Card{
    public SpellCard(String name, double damage, ElementType elementType, String id) {
        super(name, damage, CardType.SPELL, elementType,id);
    }


    @Override
  public double getEffectiveness(Card opponent) {
        ElementType oppsElementType=opponent.getElementType();
        if(this.elementType==ElementType.WATER && oppsElementType==ElementType.FIRE){
            return this.damage*2;
        }
        else if(this.elementType==ElementType.FIRE && oppsElementType==ElementType.WATER){
            return this.damage/2;
        }
        if(this.elementType==ElementType.FIRE && oppsElementType==ElementType.NORMAL){
            return this.damage*2;
        }
        else if(this.elementType==ElementType.NORMAL && oppsElementType==ElementType.FIRE){
            return this.damage/2;
        }
        if(this.elementType==ElementType.NORMAL && oppsElementType==ElementType.WATER){
            return this.damage*2;
        }
        else if(this.elementType==ElementType.WATER && oppsElementType==ElementType.NORMAL){
            return this.damage/2;
        }
        return this.damage;
    }

    @Override
    public boolean checkSpecialties(Card opponent) {
        if(this.elementType==ElementType.WATER && opponent.getCardType()==CardType.MONSTER){
            opponent=(MonsterCard) opponent;
            if(((MonsterCard) opponent).getMonsterType()== MonsterType.KNIGHT){
                return true;
            }

        }
        return false;
    }
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString(){
        String returnString="{"+this.name+": "+this.damage+" damage"+"}";
        return returnString;
    }
}
