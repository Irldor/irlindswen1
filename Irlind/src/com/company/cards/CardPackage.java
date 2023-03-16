package com.company.cards;

import com.company.database.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public class CardPackage {
    public final static int PACKAGE_COST=5;

    public boolean addCards(List<Card> newCards){
        int maxPackageId=getMaxPackageID();
        if(maxPackageId>=0){
            Connection conn= ConnectionManager.getConnection();

            if(conn!=null){
                try{
                    String sql="INSERT INTO Cards(id_cards,name,damage,package_id,\"usedinDeck\") VALUES(?,?,?,?,FALSE);";
                    PreparedStatement ps=conn.prepareStatement(sql.repeat(newCards.size()));

                    for(int i=0;i<newCards.size();i++){
                        Card card=newCards.get(i);
                        ps.setString(1+4*i,card.getID());
                        ps.setString(2+4*i,card.getName());
                        ps.setDouble(3+4*i,card.getDamage());
                        ps.setInt(4+4*i,maxPackageId+1);
                    }
                    ps.execute();
                    return true;
                }
                catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }


    private int getMaxPackageID(){
        Connection conn= ConnectionManager.getConnection();

        if(conn!=null){
            try{
                PreparedStatement ps=conn.prepareStatement("""
                    SELECT COALESCE(MAX("package_id"),0) as "maxPackageId" FROM cards;
                """ );
                ResultSet resultSet=ps.executeQuery();
                resultSet.next();
                return resultSet.getInt(1);
            }
            catch (Exception e){
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }

    public boolean acquirePackage(String username){
        Connection conn= ConnectionManager.getConnection();

        if(conn!=null){
            try{
                PreparedStatement ps=conn.prepareStatement("""
                   select acquirepackage(?);
                """ );

                ps.setString(1,username);
                ResultSet res=  ps.executeQuery();
                res.next();

                if(res.getInt(1)==1)
                    return true;
                else
                    return false;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static List<Card> getALlCardsFromUser(String username){
        List<Card> cards=new LinkedList<>();
        Connection conn= ConnectionManager.getConnection();

        if(conn!=null){
            try{
                PreparedStatement ps=conn.prepareStatement("""
                    select id_cards,name,damage from cards where "user"=?;
                """ );

                ps.setString(1,username);
                ResultSet res=ps.executeQuery();

                while(res.next()){
                    Card c=new Card();
                    c.setID(res.getString(1));
                    c.setName(res.getString(2));
                    c.setDamage(res.getDouble(3));
                    cards.add(c);
                }

                return cards;
            }
            catch (Exception e){
                e.printStackTrace();
                cards=new LinkedList<>();
            }
        }
        return cards;
    }
}



