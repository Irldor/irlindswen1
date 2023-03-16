package com.company.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.company.database.ConnectionManager;
import lombok.Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Data

public class User {
   @JsonProperty(value = "Username")
   private String username;
   @JsonProperty(value = "Password", access = JsonProperty.Access.WRITE_ONLY)
   private String password;
   @JsonProperty(value = "Bio")
   private String bio;
   @JsonProperty(value = "Image")
   private String image;
   @JsonProperty(value = "Name")
   private String name;
   @JsonIgnore
   private CardDeck deck;
   @JsonIgnore
   private CardStack stack;
   @JsonIgnore
   private int coins;
   @JsonIgnore
   private int elo;


   public boolean register() {
      Connection conn = ConnectionManager.getConnection();
      if (conn != null) {
         try {
            PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO USERS(username,password,coins) VALUES (?,?,20);
                    INSERT INTO stats(wins,losses,draws,"user") values (0,0,0,?)
                """ );

            ps.setString(1,this.getUsername());
            ps.setString(2, this.getPassword());
            ps.setString(3,this.getUsername());
            ps.execute();

            return true;
         }
         catch (Exception e){
            System.out.println("Could not save user");
            e.printStackTrace();
            return false;
         }
      }
      else{
         return false;
      }
   }
   public String login() {
      Connection conn = ConnectionManager.getConnection();
      if (conn != null) {
         try {
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT username,password FROM USERS
                    WHERE username=? and password=?;
                """ );

            ps.setString(1,getUsername());
            ps.setString(2, getPassword());
            ResultSet resultSet=ps.executeQuery();

            if(resultSet.next()){
               System.out.println("credentials correct");
               return this.saveTokenForUser();
            }
            else{
               System.out.println("credentials wrong");
               return "";
            }
         }
         catch (Exception e){
            System.out.println("Could not save user");
            e.printStackTrace();
            return "";
         }
      }
      else{
         return"";}

   }

   private String saveTokenForUser(){
      String token="Basic "+this.getUsername()+"-mtcgToken";
      Connection conn= ConnectionManager.getConnection();

      if(conn!=null){
         try {
            PreparedStatement ps = conn.prepareStatement("""
                        UPDATE users SET token=? WHERE username=?;
                    """);

            ps.setString(1, token);
            ps.setString(2, this.getUsername());
            ps.execute();

            return token;
         }
         catch (Exception e){
            System.out.println("Could not save token");
            e.printStackTrace();
            return "-1";
         }
      }
      else{
         return "-1";
      }
   }

   public static String getUsernameFromTokenAndCheckCoins(String token){
      Connection conn= ConnectionManager.getConnection();

      if(conn!=null){
         try {
            PreparedStatement ps = conn.prepareStatement("""
                        SELECT username,coins from USERS
                        WHERE token=?
                    """);

            ps.setString(1, token);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
               int coins = resultSet.getInt(2);
               if (coins < CardPackage.PACKAGE_COST) return "-1";
               return resultSet.getString(1);
            } else {
               System.out.println("Token missing or expired");
               return "-1";
            }
         }
         catch (Exception e){
            System.out.println("Could not save user");
            e.printStackTrace();
            return "-1";
         }
      }
      else{
         return "-1";
      }
   }
   public boolean retrieveInfoFromDB(){
      Connection conn= ConnectionManager.getConnection();
      if(conn!=null){
         try{
            PreparedStatement ps=conn.prepareStatement("""
                    SELECT name,bio,image from USERS
                    WHERE username=?
                """ );
            ps.setString(1,this.username);
            ResultSet resultSet=ps.executeQuery();
            if(resultSet.next()){
               this.name=resultSet.getString(1);
               this.bio=resultSet.getString(2);
               this.image=resultSet.getString(3);

               return true;
            }
            else{
               System.out.println("Token missing or expired");
               return false;
            }

         }catch (Exception e){
            System.out.println("Could not save user");
            e.printStackTrace();
            return false;
         }
      }
      else{
         return false;
      }
   }
   public boolean updateUserData(){
      Connection conn= ConnectionManager.getConnection();
      if(conn!=null){
         try{
            PreparedStatement ps=conn.prepareStatement("""
                    UPDATE users SET name=?, bio=?, image=?
                    WHERE username=?
                """ );
            ps.setString(1,this.name);
            ps.setString(2,this.bio);
            ps.setString(3,this.image);
            ps.setString(4,this.username);
            ps.execute();
            return true;

         }catch (Exception e){
            System.out.println("Could not edit user info");
            e.printStackTrace();
            return false;
         }
      }
      else{
         return false;
      }
   }
   @Override
   public String toString() {
      return "User{" +
              "username='" + username + '\'' +
              ", bio='" + bio + '\'' +
              ", image='" + image + '\'' +
              '}';
   }
}


