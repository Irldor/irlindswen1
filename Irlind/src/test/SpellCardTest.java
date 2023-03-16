package test;

import com.company.cards.Card;
import com.company.cards.MonsterCard;
import com.company.cards.SpellCard;
import enums.ElementType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SpellCardTest {
    @Test
    void specialties(){
        Card waterSpell = new SpellCard("WaterSpell",5, ElementType.WATER,"dummy");
        Card knight = new MonsterCard("dummy","knight",100,ElementType.NORMAL);

        assertThat(waterSpell.checkSpecialties(knight)).isTrue();
    }

    @Test
    void fireVSwater(){
        Card waterSpell = new SpellCard("WaterSpell",5, ElementType.WATER,"dummy");
        Card fireSpell = new SpellCard("FireSpell",5, ElementType.FIRE,"dummy");

        assertThat(waterSpell.getEffectiveness(fireSpell)).isEqualTo(10);
        assertThat(fireSpell.getEffectiveness(waterSpell)).isEqualTo(2.5);
    }

    @Test
    void fireVSnormal(){
        Card goblin = new MonsterCard("dummy","goblin",100,ElementType.NORMAL);
        Card fireSpell = new SpellCard("FireSpell",5, ElementType.FIRE,"dummy");

        assertThat(fireSpell.getEffectiveness(goblin)).isEqualTo(10);
        assertThat(goblin.getEffectiveness(fireSpell)).isEqualTo(100);
    }
}
