package test;

import com.company.battle.BattleManager;
import com.company.cards.Card;
import com.company.cards.CardDeck;
import enums.CardType;
import enums.ElementType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BattleTest {
    CardDeck firstDeck = new CardDeck(), secondDeck = new CardDeck();

    @Test
    void testWin(){
        fillDecks();
        firstDeck.initializeCards();
        secondDeck.initializeCards();
        
        BattleManager.BattleResult result = BattleManager.battle(firstDeck, secondDeck);

        System.out.println(result.getBattleLog());
        assertThat(result.getResult()).isEqualTo(1);
    }

    @Test
    void testDraw(){
        fillDecks();
        firstDeck.initializeCards();
        secondDeck.initializeCards();

        BattleManager.BattleResult result = BattleManager.battle(firstDeck, firstDeck);

        System.out.println(result.getBattleLog());
        assertThat(result.getResult()).isEqualTo(0);
    }

    @Test
    void testLose(){
        fillDecks();
        firstDeck.initializeCards();
        secondDeck.initializeCards();

        BattleManager.BattleResult result = BattleManager.battle(secondDeck, firstDeck);

        System.out.println(result.getBattleLog());
        assertThat(result.getResult()).isEqualTo(-1);
    }

    private void fillDecks(){
        Card[] myCards={
                new Card("WaterSpell",20, CardType.SPELL, ElementType.WATER,"2")
                ,new Card("FireSpell",32, CardType.SPELL, ElementType.FIRE,"3")
                ,new Card("Dragon",50, CardType.SPELL, ElementType.NORMAL,"4")
                ,new Card("WaterOrk",25, CardType.SPELL, ElementType.WATER,"5")
        };


        Card[] oppsCards={
                new Card("WaterSpell",1, CardType.SPELL, ElementType.WATER,"8")
                ,new Card("WaterSpell",1, CardType.SPELL, ElementType.WATER,"7")
                ,new Card("Dragon",1, CardType.SPELL, ElementType.NORMAL,"6")
                ,new Card("Kraken",1, CardType.SPELL, ElementType.WATER,"9")
        };


        for(Card c:myCards){
            firstDeck.addCardToDeck(c);
        }

        for(Card c:oppsCards){
            secondDeck.addCardToDeck(c);
        }
    }
}
