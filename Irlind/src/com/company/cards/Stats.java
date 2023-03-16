package com.company.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.company.database.ConnectionManager;
import lombok.Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

@Data
public class Stats {
    private static final int ELO_POINTS_FOR_WIN=3;
    private static final int ELO_POINTS_FOR_LOSS=5;
    private static final int STARTING_ELO=100;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer wins;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer losses;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer draws;

    int elo;
    String user;
    String rank;

    private void getRankFromElo(){
        if(elo<=150) rank="Bronze";
        else if(elo>150 && elo<200) rank= "Gold";
        else rank= "Diamond";
    }

    public Stats(String username){
        this.user=username;
        wins=null;
        losses=null;
        draws=null;
    }

    @JsonIgnore
    public boolean StatsForUser(){
        Connection conn= ConnectionManager.getConnection();
        if(conn!=null){
            try{
                PreparedStatement ps=conn.prepareStatement("""
                    SELECT wins,losses,draws,"user" from stats
                    WHERE "user"=?;
                """ );
                ps.setString(1,this.user);
                ResultSet resultSet=ps.executeQuery();
                if(resultSet.next()){
                    this.wins=resultSet.getInt(1);
                    this.losses=resultSet.getInt(2);
                    this.draws=resultSet.getInt(3);
                    this.elo=STARTING_ELO
                            +wins*ELO_POINTS_FOR_WIN
                            -losses*ELO_POINTS_FOR_LOSS;
                    this.getRankFromElo();
                    return true;
                }
                else{
                    System.out.println("No Stats for this user");
                    return false;
                }

            }catch (Exception e){
                System.out.println("Could not get user stats");
                e.printStackTrace();
                return false;
            }
        }
        else{
            return false;
        }
    }

    public boolean updateStatsForUser(){
        Connection conn= ConnectionManager.getConnection();
        if(conn!=null){
            try{
                PreparedStatement ps=conn.prepareStatement("""
                    UPDATE stats set wins=?,losses=?,draws=? 
                    WHERE "user"=?;
                """ );
                ps.setInt(1,this.wins);
                ps.setInt(2,this.losses);
                ps.setInt(3,this.draws);
                ps.setString(4,this.user);
                ps.execute();
                return true;

            }catch (Exception e){
                System.out.println("Could not update user stats");
                e.printStackTrace();
                return false;
            }
        }
        else{
            return false;
        }
    }

    public static List<Stats> getScoreBoard(){
        Connection conn= ConnectionManager.getConnection();
        if(conn!=null){
            try{
                PreparedStatement ps=conn.prepareStatement("""
                    SELECT (?+s.wins*?-s.losses*?) as elo, COALESCE(u.name,u.username)
                    FROM stats s
                    JOIN users u ON s."user" = u.username
                    ORDER BY elo DESC;
                """);
                ps.setInt(1,STARTING_ELO);
                ps.setInt(2,ELO_POINTS_FOR_WIN);
                ps.setInt(3,ELO_POINTS_FOR_LOSS);
                ResultSet res=ps.executeQuery();
                List<Stats> result=new LinkedList<>();
                while(res.next()){
                    Stats stat=new Stats(res.getString(2));
                    stat.setElo(res.getInt(1));
                    stat.getRankFromElo();
                    result.add(stat);
                }
                return result;

            }catch (Exception e){
                System.out.println("Could not get Scoreboard");
                e.printStackTrace();
                return new LinkedList<>();
            }
        }
        else{
            return new LinkedList<>();
        }
    }
    public void updateStatsAfterBattle(int result){
        switch(result){
            case 1:
                wins++;
                break;
            case -1:
                losses++;
                break;
            default:
                draws++;
        }
        updateStatsForUser();
    }
}
