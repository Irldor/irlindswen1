package com.company.cards;

import enums.CardType;
import enums.ElementType;
import enums.MonsterType;
import lombok.*;

@Value

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MonsterCard extends Card {

  //  MonsterType monsterType;

    public MonsterCard(String id, String name, double damage, ElementType elementType) {
        super(name, damage, CardType.MONSTER, elementType,id);
      //  this.monsterType=monsterType;
    }

    @Override
   public double getEffectiveness(Card opponent) {
        return this.damage;
    }

    @Override
    public boolean checkSpecialties(Card opponent) {
        MonsterType thisMonstertype=getMonsterType();
        if(opponent.getCardType()==CardType.MONSTER){
            MonsterCard monsterOpponent = (MonsterCard) opponent;
            MonsterType playerType = thisMonstertype;
            MonsterType oppsType = monsterOpponent.getMonsterType();
            if(playerType==MonsterType.DRAGON && oppsType==MonsterType.GOBLIN ){
                return true;
            }
            else if (playerType==MonsterType.WIZZARD && oppsType==MonsterType.ORK ){
                return  true;
            }
            else if (playerType==MonsterType.ELF && this.elementType==ElementType.FIRE && oppsType==MonsterType.DRAGON ){
                return  true;
            }
        }
        else if(thisMonstertype==MonsterType.KRAKEN){
            return true;
            }
         return false;
        }
    public MonsterType getMonsterType(){
            String monsterTypeString=name;
            monsterTypeString= monsterTypeString.replace("Water","");
            monsterTypeString=monsterTypeString.replace("Fire","");
            return MonsterType.valueOf(monsterTypeString.toUpperCase());

   };
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






