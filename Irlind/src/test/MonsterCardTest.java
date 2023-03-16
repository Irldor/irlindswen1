package test;

import com.company.cards.Card;
import com.company.cards.MonsterCard;
import enums.ElementType;
import enums.MonsterType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MonsterCardTest {
    @Test
    void goblinVSdragon(){
        Card goblin = new MonsterCard("dummy-id","WaterGoblin",50, ElementType.WATER);
        Card dragon = new MonsterCard("dummy-id","FireDragon",50, ElementType.FIRE);
        assertThat(dragon.checkSpecialties(goblin)).isTrue();
    }

    @Test
    void wizardVSork(){
        Card wizz = new MonsterCard("dummy-id","Wizzard",50, ElementType.NORMAL);
        Card ork = new MonsterCard("dummy-id","Ork",50, ElementType.NORMAL);
        assertThat(wizz.checkSpecialties(ork)).isTrue();

    }

    @Test
    void fireElfVSdragon(){
        Card elf = new MonsterCard("dummy-id","FireElf",50, ElementType.FIRE);
        Card dragon = new MonsterCard("dummy-id","Dragon",50, ElementType.NORMAL);
        assertThat(elf.checkSpecialties(dragon)).isTrue();

    }

    @Test
    void getMonsterType(){
        MonsterCard waterGoblin=new MonsterCard("1","WaterGoblin",54,ElementType.WATER);
        MonsterCard fireGoblin=new MonsterCard("1","FireGoblin",54,ElementType.FIRE);
        MonsterCard ork=new MonsterCard("1","Ork",54,ElementType.NORMAL);

        assertThat(waterGoblin.getMonsterType()).isEqualTo(MonsterType.GOBLIN);
        assertThat(fireGoblin.getMonsterType()).isEqualTo(MonsterType.GOBLIN);
        assertThat(ork.getMonsterType()).isEqualTo(MonsterType.ORK);
    }
}
