package test;

import com.company.cards.Card;
import com.company.cards.CardDeck;
import enums.CardType;
import enums.ElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CardDeckTest {
    private CardDeck deck = new CardDeck();

    @Test
    void testDeck(){
        Card card = deck.getRandomCardFromDeck();
        assertThat(card.getName().contains("Fire")).isTrue();
    }

    @BeforeEach
    private void fillDecksWithCards(){
        Card[] myCards={
                new Card("FireSpell",20, CardType.SPELL, ElementType.FIRE,"2")
                ,new Card("FireSpell",32, CardType.SPELL, ElementType.FIRE,"3")
                ,new Card("FireDragon",50, CardType.SPELL, ElementType.FIRE,"4")
                ,new Card("FireOrk",25, CardType.SPELL, ElementType.FIRE,"5")
        };

        for(Card c:myCards){
            deck.addCardToDeck(c);
        }
    }
}
