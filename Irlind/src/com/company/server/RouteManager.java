package com.company.server;

import com.company.battle.BattleManager;
import com.company.cards.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.database.ConnectionManager;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class RouteManager {
    private ObjectMapper objectMapper  =  new ObjectMapper();

    public HTTPResponse handleRoute(String httpMethodWithPath, String data, String token){
        String[] parts = httpMethodWithPath.split(" ");
        String httpMethod = parts[0];
        String path = parts[1];

        if("POST".equals(httpMethod) && "/users".equals(path)){
            try{
                final User userDto = objectMapper.readValue(data, User.class);

                if(userDto.register())
                    return new HTTPResponse("User registered successfully","200 OK");
                else
                    return new HTTPResponse("Error","500 Internal Server Error");
            }
            catch (Exception e){
                System.out.println("User could not be registered");
                return new HTTPResponse("Error","400 Bad Request");
            }
        }
        else if("POST".equals(httpMethod) && "/sessions".equals(path)){
            try{
                final User userDto = objectMapper.readValue(data, User.class);

                String newToken = userDto.login();

                if(!newToken.isEmpty()){
                    if("-1".equals(newToken))
                        return new HTTPResponse("Error","500 Internal Server Error");
                    else
                        return new HTTPResponse(newToken,"200 OK");
                }
                else {
                    return new HTTPResponse("Error", "400 Bad Request");
                }
            }
            catch (Exception e){
                System.out.println("User login failed");
                return new HTTPResponse("Error","400 Bad Request");
            }
        }
        else if("POST".equals(httpMethod) && "/packages".equals(path)){
            if(!checkAuth(token)){
                return new HTTPResponse("Error Auth","401 Unauthorized");
            }

            try{
                final CardPackage cardPackageDto = new CardPackage();
                
                List<Card> newCards = objectMapper.readValue(data, new TypeReference<List<Card>>(){});
                
                if(cardPackageDto.addCards(newCards))
                    return new HTTPResponse("Package added","200 OK");
                else
                    return new HTTPResponse("Error","500 Internal Server Error");
            }
            catch (Exception e){
                System.out.println("User login failed");
                e.printStackTrace();
                
                return new HTTPResponse("Error","400 Bad Request");
            }
        }
        else if("POST".equals(httpMethod) && "/transactions/packages".equals(path)){
            String username  =  User.getUsernameFromTokenAndCheckCoins(token);
            
            if("-1".equals(username)){
                return new HTTPResponse("Package could not be acquired","401 Unauthorized");
            }
            
            try{
                final CardPackage cardPackageDto = new CardPackage();
                
                if(cardPackageDto.acquirePackage(username))
                    return new HTTPResponse("Package acquired for user: "+username,"200 OK");
                else
                    return new HTTPResponse("Error","500 Internal Server Error");
            }
            catch (Exception e){
                System.out.println("Package could not be acquired");
                e.printStackTrace();
                
                return new HTTPResponse("Error","400 Bad Request");
            }
        }
        else if("GET".equals(httpMethod) && "/cards".equals(path)){
            
            String username = getUserAuthentication(token);
            
            if("-1".equals(username)){
                return new HTTPResponse("Authentication failed","401 Unauthorized");
            }
            
            try{
                List<Card> cards = CardPackage.getALlCardsFromUser(username);
                return new HTTPResponse(cards,"200 OK");
            }
            catch (Exception e){
                System.out.println("Cards could not be acquired");
                e.printStackTrace();
                return new HTTPResponse("Error","400 Bad Request");
            }
        }
        else if("PUT".equals(httpMethod) && "/deck".equals(path)){
            String username = getUserAuthentication(token);
            
            if("-1".equals(username)){
                return new HTTPResponse("Authentication failed","401 Unauthorized");
            }
            
            try{
                final CardDeck cardDeckdto = new CardDeck();
                List<String> cardIDs =  Arrays.asList(objectMapper.readValue(data, String[].class));
                
                if(cardIDs.size() != 4){
                    return new HTTPResponse("Error","400 Bad Request");
                }
                
                if(cardDeckdto.configureDeck(cardIDs,username))
                    return new HTTPResponse("OK","200 OK");
                else
                    return new HTTPResponse("Could not configure Deck","400 Bad Request");
            }
            catch (Exception e){
                System.out.println("Cards could not be acquired");
                e.printStackTrace();
                
                return new HTTPResponse("Error","400 Bad Request");
            }
        }
        else if("GET".equals(httpMethod) && path.contains("/deck")){
            String username = getUserAuthentication(token);
            
            if("-1".equals(username)){
                return new HTTPResponse("Authentication failed","401 Unauthorized");
            }
            
            try{
                final CardDeck cardDeckdto  =  new CardDeck();
                Set<Card> cards =  cardDeckdto.getCardDeckForUser(username);
                if(path.contains("format = plain")){
                    return new HTTPResponse(cardDeckdto.toString(),"200 OK");
                }
                else return new HTTPResponse(cards,"200 OK");
            }
            catch (Exception e){
                System.out.println("Cards could not be acquired");
                e.printStackTrace();
                return new HTTPResponse("Error","400 Bad Request");
            }
        }
        else if("GET".equals(httpMethod) && path.contains("/users")){
            String usernameFromPath = "-1";
            
            try{
                usernameFromPath = path.split("/")[2];
            }
            catch (ArrayIndexOutOfBoundsException e){
                return new HTTPResponse("Error","400 Bad Request");
            }
            String usernameFromToken = getUserAuthentication(token);
            if("-1".equals(usernameFromToken) || !usernameFromPath.equals(usernameFromToken)){
                return new HTTPResponse("Authentication failed","401 Unauthorized");
            }

            try{
              User UserDto = new User();
              UserDto.setUsername(usernameFromPath);
              if(UserDto.retrieveInfoFromDB()){
                  return new HTTPResponse(UserDto,"200 OK");
              }

            }catch (Exception e){
                System.out.println("Could not get User Data");
                e.printStackTrace();
                return new HTTPResponse("Error","400 Bad Request");
            }
        }
        else if("PUT".equals(httpMethod) && path.contains("/users")){
            String usernameFromPath = path.split("/")[2];
            String usernameFromToken = getUserAuthentication(token);
            if("-1".equals(usernameFromToken) || !usernameFromPath.equals(usernameFromToken)){
                return new HTTPResponse("Authentication failed","401 Unauthorized");
            }
            try{
                User userDto = objectMapper.readValue(data, User.class);
                userDto.setUsername(usernameFromPath);
                if(userDto.updateUserData()){
                    return new HTTPResponse("User Data edited successfully","200 OK");
                }
                else{
                    return new HTTPResponse("Error","500 Internal Server Error");
                }
            }
            catch (Exception e){
                System.out.println("Could not edit user stats");
                e.printStackTrace();
                return new HTTPResponse("Error","400 Bad Request");
            }
        }
        else if("GET".equals(httpMethod) && path.contains("/stats")){
            String username = getUserAuthentication(token);
            if("-1".equals(username)){
                return new HTTPResponse("Authentication failed","401 Unauthorized");
            }
            try{
                Stats statsDto = new Stats(username);
                if(statsDto.StatsForUser()){
                    return new HTTPResponse(statsDto,"200 OK");
                }
                else{
                    return new HTTPResponse("Error","500 Internal Server Error");
                }
            }
            catch (Exception e){
                System.out.println("Could not get Stats");
                e.printStackTrace();
                return new HTTPResponse("Error","400 Bad Request");
            }
        }
        else if("GET".equals(httpMethod) && path.contains("/score")){
            String username = getUserAuthentication(token);
            if("-1".equals(username)){
                return new HTTPResponse("Authentication failed","401 Unauthorized");
            }

            List<Stats> scoreBoard  =  Stats.getScoreBoard();
            if(!scoreBoard.isEmpty()){
                return new HTTPResponse(scoreBoard,"200 OK");
            }
            else{
                return new HTTPResponse("Error","500 Internal Server Error");
            }
        }
        else if("GET".equals(httpMethod) && path.contains("/tradings")){
            String username = getUserAuthentication(token);
            if("-1".equals(username)){
                return new HTTPResponse("Authentication failed","401 Unauthorized");
            }

            List<Trades> trades  =  Trades.checkTrades(username);
            if(trades != null){
                return new HTTPResponse(trades,"200 OK");
            }
            else{
                return new HTTPResponse("Error","500 Internal Server Error");
            }
        }
        else if("POST".equals(httpMethod) && "/tradings".equals(path)){
            String username = getUserAuthentication(token);
            if("-1".equals(username)){
                return new HTTPResponse("Authentication failed","401 Unauthorized");
            }
            try{
                Trades newTrade = objectMapper.readValue(data, Trades.class);
                if(!Card.checkOwnership(username,newTrade.getOfferedCardId())){
                    return new HTTPResponse("User doesn't own the card or is used in deck","400 Bad Request");
                }
                if(newTrade.addTradeDeal()){
                    return new HTTPResponse("Trade Deal created","200 OK");
                }
                else{
                    return new HTTPResponse("Error","500 Internal Server Error");
                }
            }
            catch (Exception e){
                System.out.println("Could not create Trading Deal");
                e.printStackTrace();
                return new HTTPResponse("Error","400 Bad Request");
            }
        }
        else if("DELETE".equals(httpMethod) && path.contains("/tradings")){
            String username = getUserAuthentication(token);
            if("-1".equals(username)){
                return new HTTPResponse("Authentication failed","401 Unauthorized");
            }

            String tradeId = path.split("/")[2];
            if (Trades.deleteTrade(tradeId)){
                return new HTTPResponse("Trade offer deleted","200 OK");
            }
            else {
                return new HTTPResponse("Error", "500 Internal Server Error");
            }
        }
        else if("POST".equals(httpMethod) && path.contains("/tradings")){
            String username = getUserAuthentication(token);
            if("-1".equals(username)){
                return new HTTPResponse("Authentication failed","401 Unauthorized");
            }

            try {
                String tradeId = path.split("/")[2];
                String cardToAccept =  objectMapper.readValue(data,String.class);
                if (Trades.trade(tradeId,username,cardToAccept)){
                    return new HTTPResponse("Trade Deal done","200 OK");
                }
                else {
                    return new HTTPResponse("Error", "400 Bad Request");
                }
            }
            catch (Exception e) {
                return new HTTPResponse("Error", "400 Bad Request");
            }
        }
        else if("POST".equals(httpMethod) && "/battles".equals(path)) {
            String username  =  getUserAuthentication(token);
            if ("-1".equals(username)) {
                return new HTTPResponse("Authentication failed", "401 Unauthorized");
            }

            String opponent  =  BattleManager.startMatchmaking(username);
            if("-1".equals(opponent)){
                return new HTTPResponse("Waiting","200 OK");
            }
            else{
                BattleManager.BattleResult result = BattleManager.startBattle(opponent,username);
                System.out.println(result.getBattleLog());
                return new HTTPResponse(result.getResult(),"200 OK");
            }
        }
        return new HTTPResponse("Error","400 Bad Request");
    }


    @AllArgsConstructor
    private class HTTPRoute{
        String method;
        String route;
        Class controller;
    }


    private boolean checkAuth(String token){
        Connection conn = ConnectionManager.getConnection();

        if (conn !=  null) {
            try {
                PreparedStatement ps = conn.prepareStatement("""
                    select token from users where username = 'admin';
                """ );

               ResultSet res =  ps.executeQuery();
               res.next();
               String adminToken = res.getString(1);

               return token.equals(adminToken);
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

    public String getUserAuthentication(String token){
        Connection conn =  ConnectionManager.getConnection();

        if(conn != null){
            try{
                PreparedStatement ps = conn.prepareStatement("""
                    SELECT username FROM users WHERE token = ?; 
                """ );

                ps.setString(1,token);
                ResultSet res = ps.executeQuery();

                if(res.next())
                    return res.getString(1);
                else
                    return "-1";
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
}
