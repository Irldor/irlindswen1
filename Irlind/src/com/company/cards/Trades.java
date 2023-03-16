package com.company.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.company.database.ConnectionManager;
import enums.CardType;
import enums.ElementType;
import lombok.Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Data
public class Trades {
    @JsonProperty(value = "Id")
    private String tradeId;

    @JsonProperty(value = "CardToTrade")
    private String offeredCardId;

    @JsonProperty(value = "CardName")
    private String offeredCardName;

    @JsonProperty(value = "offeredBy")
    private String offeredByUser;

    @JsonProperty(value = "Type")
    private CardType typeRequired;

    @JsonProperty(value = "MinimumDamage")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer minDamageRequired;

    @JsonProperty(value = "requiredElement")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ElementType elementTypeRequired;




    public void setTypeRequired(String type){
        if("monster".equals(type)){
            this.typeRequired=CardType.MONSTER;
        }
        else{
            this.typeRequired=CardType.SPELL;
        }
    }

    @JsonIgnore
    public void setElementTypeRequired(String type){
        if("fire".equals(type)){
            this.elementTypeRequired=ElementType.FIRE;
        }
        else if("water".equals(type)){
            this.elementTypeRequired=ElementType.WATER;
        }
        else if("normal".equals(type)){
            this.elementTypeRequired=ElementType.NORMAL;
        }
    }

    public String getElementTypeRequired(){
        if(elementTypeRequired==null) return null;
        else return elementTypeRequired.toString().toLowerCase();
    }

    private void initializeFromResultSet(ResultSet res) throws SQLException {
        setTradeId(res.getString("id"));
        setOfferedCardId(res.getString("cardOffered"));
        setTypeRequired(res.getString("cardTypeRequired"));
        setElementTypeRequired(res.getString("elementTypeRequired"));
        setMinDamageRequired(res.getInt("minDamageRequired"));
    }

    public static List<Trades> checkTrades(String user){
        Connection conn= ConnectionManager.getConnection();
        if(conn!=null){
            try{
                PreparedStatement ps=conn.prepareStatement("""
                    select t."id" as "id","elementTypeRequired","minDamageRequired","cardOffered","cardTypeRequired",u.username as "user",c.name as "cardName"
                    from trades t
                    join cards c on t."cardOffered" = c.id_cards
                    join users u on c."user" = u.username
                    where t."cardAccepted" is null and u.username!=?;
                """);

                ps.setString(1,user);
                ResultSet res=ps.executeQuery();
                List<Trades> result=new LinkedList<>();

                while(res.next()){
                    Trades trade=new Trades();
                    trade.initializeFromResultSet(res);
                    trade.setOfferedCardName(res.getString("cardName"));
                    trade.setOfferedByUser(res.getString("user"));
                    result.add(trade);
                }
                return result;

            }
            catch (Exception e){
                System.out.println("Could not get Trade Deals");
                e.printStackTrace();
                return null;
            }
        }
        else{
            return null;
        }
    }


    public boolean addTradeDeal(){
        Connection conn= ConnectionManager.getConnection();
        if(conn!=null){
            try{
                PreparedStatement ps=conn.prepareStatement("""
                    INSERT INTO trades(id,"elementTypeRequired","minDamageRequired","cardOffered","cardTypeRequired")
                    VALUES (?,?,?,?,?);
                """);

                ps.setString(1,tradeId);
                ps.setString(2,getElementTypeRequired());
                ps.setInt(3,minDamageRequired);
                ps.setString(4,offeredCardId);
                ps.setString(5,typeRequired.toString().toLowerCase());

                return ps.executeUpdate()>0;

            }catch (Exception e){
                System.out.println("Could not add Trade Deal");
                e.printStackTrace();
                return false;
            }
        }
        else{
            return false;
        }
    }

    public static boolean deleteTrade(String tradeId){
        Connection conn= ConnectionManager.getConnection();
        if(conn!=null){
            try{
                PreparedStatement ps=conn.prepareStatement("""
                    DELETE FROM trades where id=?;
                """);

                ps.setString(1,tradeId);
                return ps.executeUpdate()>0;

            }catch (Exception e){
                System.out.println("Could not delete Trade");
                e.printStackTrace();
                return false;
            }
        }
        else {
            return false;
        }
    }

    public static boolean trade(String tradeId,String userTrading,String cardToAccept){
        if(!Card.checkOwnership(userTrading,cardToAccept))
            return false;
        Connection conn= ConnectionManager.getConnection();
        if(conn!=null){
            try{
                PreparedStatement ps=conn.prepareStatement("""
                    select t."id" as "id","elementTypeRequired","minDamageRequired","cardOffered","cardTypeRequired", u.username as "user"
                    from trades t
                    join cards c on t."cardOffered" = c.id_cards
                    join users u on c."user" = u.username
                    where t."cardAccepted" is null 
                    and t."id"=? and u.username!=?;
                """);

                ps.setString(1,tradeId);
                ps.setString(2,userTrading);

                ResultSet res=ps.executeQuery();
                if(res.next()){
                    Trades trade= new Trades();
                    trade.initializeFromResultSet(res);
                    trade.setOfferedByUser(res.getString("user"));
                    System.out.println(trade);
                    if(trade.checkTradingPossible(cardToAccept,trade)){
                        return updateTradingOffer(trade,cardToAccept,userTrading);
                    }
                    else return false;
                }
                else return false;

            }catch (Exception e){
                System.out.println("Could not make Trade");
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private boolean checkTradingPossible(String cardToAccept,Trades trade){

        Card card = Card.initializeFromDB(cardToAccept);
        if(card!=null){
            card=card.createCardFromName();
            if(trade.getTypeRequired()!=card.getCardType())
                return false;
            Integer minDamageRequired= trade.getMinDamageRequired();
            if(minDamageRequired!=null){
                return minDamageRequired<card.getDamage();
            }
            else{
                String elementType=trade.getElementTypeRequired();
                if(elementType!=null){
                    return elementType.equals(card.getElementType().toString().toLowerCase());
                }
            }
            return false;
        }
        return false;
    }

    private static boolean updateTradingOffer(Trades trade,String cardAccepted, String accpetedBy){
        Connection conn= ConnectionManager.getConnection();
        if(conn!=null){
            try{
                PreparedStatement ps=conn.prepareStatement("""
                    UPDATE trades set "cardAccepted"=?  where id=?;
                    UPDATE cards set "user"=? where id_cards=?;
                    UPDATE cards set "user"=? where id_cards=?;
                """);
                ps.setString(1,cardAccepted);
                ps.setString(2,trade.getTradeId());

                ps.setString(4,trade.getOfferedCardId());
                ps.setString(3,accpetedBy);

                ps.setString(6,cardAccepted);
                ps.setString(5,trade.getOfferedByUser());
                return ps.executeUpdate()>0;

            }catch (Exception e){
                System.out.println("Could not make Trade");
                e.printStackTrace();
                return false;
            }
        }
        else {
            return false;
        }
    }
}
