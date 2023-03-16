package com.company.cards;

import com.company.database.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CardDeck {
    private Set<Card> cards =  new HashSet<>();

    public Set<Card> getCardDeckForUser(String username){
        Connection conn =  ConnectionManager.getConnection();

        if(conn != null){
            try{
                PreparedStatement ps = conn.prepareStatement("""
                    select id_cards,name,damage from cards where "user" = ? and "usedinDeck" = TRUE;
                """ );

                ps.setString(1,username);
                ResultSet res = ps.executeQuery();

                while(res.next()){
                    Card c = new Card();
                    c.setID(res.getString(1));
                    c.setName(res.getString(2));
                    c.setDamage(res.getDouble(3));
                    cards.add(c);
                }
                return cards;

            }
            catch (Exception e){
                e.printStackTrace();
                cards = new HashSet<>();
            }
        }
        return cards;
    }

    @Override
    public String toString(){
        String s = "Deck Begin ";
        for (Card c : cards){
            s += c.toString()+" ";
        }
        s += " Deck End";
        return s;
    }

    public boolean addCardToDeck(Card c){
         return  cards.add(c);
    }

    public  int getSize(){
        return cards.size();
    }

    public boolean removeCardFromDeck(Card c){
       return cards.remove(c);
    }

    public boolean configureDeck(List<String> cardIDs,String username){
        Connection conn =  ConnectionManager.getConnection();

        if(conn != null){
            try{
                PreparedStatement ps = conn.prepareStatement("""
                    select * from configuredeck(?,?,?,?,?);
                """ );

                int index = 0;

                for(String cardId:cardIDs){
                    index++;
                    ps.setString(index,cardId);
                }

                ps.setString(5,username);
                ResultSet res = ps.executeQuery();

                res.next();
                return res.getInt(1) == 1;
            }
            catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    public Card getRandomCardFromDeck(){
        Card c = cards.stream().skip((int) (cards.size() * Math.random())).findAny().get();
        return c;
    }

    public void initializeCards(){
        Set cardsInitialized = new HashSet();

        for (Card c: cards) {
            Card newCard = c.createCardFromName();
            cardsInitialized.add(newCard);
        }

        this.cards = cardsInitialized;
        System.out.println(cards);
    }

    public void transferCardsAndRemoveFromDeck(String newUser){
        for(Card c: cards){
            c.changeCardOwnership(newUser);
        }
    }
}