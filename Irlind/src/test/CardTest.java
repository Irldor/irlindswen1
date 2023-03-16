package test;

import com.company.cards.Card;
import com.company.cards.MonsterCard;
import com.company.cards.SpellCard;
import enums.CardType;
import enums.ElementType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CardTest {
    private Card card = new Card();

    @Test
    void createWaterSpell(){
        card.setName("WaterSpell");

        Card c = card.createCardFromName();

        assertThat(c.getElementType()).isEqualTo(ElementType.WATER);
        assertThat(c.getCardType()).isEqualTo(CardType.SPELL);
        assertThat(c.getClass()).isEqualTo(SpellCard.class);
    }

    @Test
    void createOrk(){
        card.setName("Ork");

        Card c = card.createCardFromName();

        assertThat(c.getElementType()).isEqualTo(ElementType.NORMAL);
        assertThat(c.getCardType()).isEqualTo(CardType.MONSTER);
        assertThat(c.getClass()).isEqualTo(MonsterCard.class);
    }

    @Test
    void createFireSpell(){
        card.setName("FireSpell");

        Card c = card.createCardFromName();

        assertThat(c.getElementType()).isEqualTo(ElementType.FIRE);
        assertThat(c.getCardType()).isEqualTo(CardType.SPELL);
        assertThat(c.getClass()).isEqualTo(SpellCard.class);
    }

    @Test
    void fight1(){

        Card goblin = new Card("WaterGoblin",25,null,null,"dummy-id");
        Card ork = new Card("FireOrk",35,null,null,"dummy-id");

        assertThat(goblin.fight(ork)).isEqualTo(-1);
        assertThat(ork.fight(goblin)).isEqualTo(1);
        assertThat(ork.fight(ork)).isEqualTo(0);
    }

    @Test
    void fight2(){

        Card wGoblin = new Card("WaterGoblin",25.0,null,null,"dummy-id");
        Card fGoblin = new Card("FireGoblin",25.0,null,null,"dummy-id");

        assertThat(wGoblin.fight(fGoblin)).isEqualTo(0);
        assertThat(fGoblin.fight(wGoblin)).isEqualTo(0);
    }

    @Test
    void specialties(){
        Card elf = new MonsterCard("dummy-id","FireElf",50, ElementType.FIRE);
        Card dragon = new MonsterCard("dummy-id","FireDragon",50, ElementType.FIRE);

        assertThat(elf.fight(dragon)).isEqualTo(1);
        assertThat(dragon.fight(elf)).isEqualTo(-1);
    }

    @Test
    void effectiveness(){
        Card goblin = new MonsterCard("dummy","goblin",100,ElementType.NORMAL);
        Card fireSpell = new SpellCard("FireSpell",5, ElementType.FIRE,"dummy");

        assertThat(goblin.fight(fireSpell)).isEqualTo(1);
        assertThat(fireSpell.fight(goblin)).isEqualTo(-1);
    }
}
