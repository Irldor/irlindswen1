package com.company.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.company.database.ConnectionManager;
import enums.CardType;
import enums.ElementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor


public  class Card {
    @JsonProperty(value="Name")
    protected String name;
    @JsonProperty(value="Damage")
    protected  double damage;
    @JsonIgnore
    protected  CardType cardType;
    @JsonIgnore
    protected ElementType elementType;
    @JsonProperty(value="Id")
    protected   String ID;

    public  int fight(Card opponent) {
        boolean playerWon= checkSpecialties(opponent);
        boolean OppWon = opponent.checkSpecialties(this);
        if(playerWon) return 1;
        if(OppWon) return -1;

        double effDamage = this.getEffectiveness(opponent);
        double oppsEffDamage = opponent.getEffectiveness(this);

        if (Double.compare(effDamage,oppsEffDamage)==0) {
            return 0;
        }
        else if (effDamage > oppsEffDamage) {
            return 1;
        }
        else{
            return -1;
        }
    }

    public double getEffectiveness(Card opponent){
        return this.damage;
 }

    public boolean checkSpecialties(Card opponent) {
        return false;
    }

    @Override
    public  String toString(){
        String returnString="{"+this.name+": "+this.damage+" damage"+"}";
        return returnString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;

        try {
            Card card = (Card) o;
            return ID.equals(card.ID);
        }
        catch(Exception e){
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    public static boolean checkOwnership(String user, String cardID){
        Connection conn= ConnectionManager.getConnection();

        if(conn!=null){
            try{
                PreparedStatement ps=conn.prepareStatement("""
                    select id_cards from cards where "user"=? and id_cards=? and "usedinDeck"=false;
                """);

                ps.setString(1,user);
                ps.setString(2,cardID);

                ResultSet res=ps.executeQuery();

                if(res.next())
                    return true;
                else
                    return false;
            }
            catch (Exception e){
                System.out.println("Could not check User Ownership");
                e.printStackTrace();
                return false;
            }
        }
        else{
            return false;
        }
    }
    public static Card initializeFromDB(String cardID){
        Card card=null;
        Connection conn= ConnectionManager.getConnection();

        if(conn!=null){
            try{
                PreparedStatement ps=conn.prepareStatement("""
                    select * from cards where id_cards=?;
                """);
                ps.setString(1,cardID);
                ResultSet res=ps.executeQuery();

                if(res.next()){
                    card=new Card();
                    card.setName(res.getString("name"));
                    card.setDamage(res.getDouble("damage"));
                    card= card.createCardFromName();
                    return card;
                }
            }
            catch (Exception e){
                System.out.println("Could not check User Ownership");
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public Card createCardFromName(){
        if(name.contains("Water")){
            elementType=ElementType.WATER;
        }
        else if (name.contains("Fire")){
            elementType=ElementType.FIRE;
        }
        else{
            elementType=ElementType.NORMAL;
        }

        if(name.contains("Spell")){
            cardType=CardType.SPELL;
            return new SpellCard(name,damage,elementType,ID);
        }
        else{
            cardType=CardType.MONSTER;
            return new MonsterCard(ID,name,damage,elementType);
        }
    }
    public boolean changeCardOwnership(String newUser){
        Connection conn= ConnectionManager.getConnection();

        if(conn!=null){
            try{
                PreparedStatement ps=conn.prepareStatement("""
                    UPDATE CARDS SET "user"=?, "usedinDeck"=false
                    WHERE id_cards=?;
                """ );
                ps.setString(1,newUser);
                ps.setString(2,ID);
                int res=ps.executeUpdate();
                return res==1;
            }
            catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private class MonsterCard extends Card {
        public MonsterCard(String id, String name, double damage, enums.ElementType elementType) {
        }
    }
}
