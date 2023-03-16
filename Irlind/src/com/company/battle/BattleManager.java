package com.company.battle;

import com.company.cards.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.Queue;


public class BattleManager {
    private static Queue<String> playersWaiting=new LinkedList<>();

    public static synchronized String startMatchmaking(String username){
        try {
            if(playersWaiting.isEmpty()){
                playersWaiting.add(username);
                return "-1";
            }
            else{
                String opponent = playersWaiting.remove();
                return opponent;
            }
        }catch (Exception e){
            System.out.println("Error with Matchmaking");
            return "-1";
        }
    }

    public static BattleResult startBattle(String player1, String player2){
        playersWaiting.poll();


        CardDeck playerDeck=new CardDeck();
        CardDeck opponentDeck=new CardDeck();

        playerDeck.getCardDeckForUser(player1);
        playerDeck.initializeCards();

        opponentDeck.getCardDeckForUser(player2);
        opponentDeck.initializeCards();


        BattleResult result= battle(playerDeck,opponentDeck);
        updateCardsAndStats(result.getResult(),playerDeck,opponentDeck,player1,player2);
        return result;
    }



    public static BattleResult battle(CardDeck playerDeck, CardDeck opponentDeck){
        StringBuilder battleLog=new StringBuilder();

        int round=1;
        while(round<=100){
            if(playerDeck.getSize()==0){
                battleLog.append("Opponent won").append("\n");
                return new BattleResult(battleLog.toString(),-1);
            }
            else if(opponentDeck.getSize()==0){
                battleLog.append("Player won").append("\n");
                return new BattleResult(battleLog.toString(),1);

            }
            Card playerCard=playerDeck.getRandomCardFromDeck();
            Card opponentCard=opponentDeck.getRandomCardFromDeck();
            battleLog.append(playerCard + " vs "+opponentCard).append("\n");
            int result=playerCard.fight(opponentCard);
            switch (result){
                case 1:
                    opponentDeck.removeCardFromDeck(opponentCard);
                    playerDeck.addCardToDeck(opponentCard);

                    battleLog.append("Player won round "+round).append("\n");
                    break;
                case -1:
                    playerDeck.removeCardFromDeck(playerCard);
                    opponentDeck.addCardToDeck(playerCard);
                    battleLog.append("Opponent won round "+round).append("\n");
                    break;
                default:
                    battleLog.append("Draw on round "+round).append("\n");
            }
            battleLog.append("playerSize:"+playerDeck.getSize()).append("\n");
            battleLog.append("opponentSize:"+opponentDeck.getSize()).append("\n");
            round++;
        }
        battleLog.append("Battle ended in a draw (100 rounds reached)").append("\n");
        return new BattleResult(battleLog.toString(),0);

    }

    @Data
    @AllArgsConstructor
    public static class BattleResult{
        String battleLog;
        int result;
    }
    private static void updateCardsAndStats(int result,CardDeck deck1, CardDeck deck2, String player1, String player2){
        deck1.transferCardsAndRemoveFromDeck(player2);
        deck2.transferCardsAndRemoveFromDeck(player1);

        Stats statsPlayer1=new Stats(player1);
        statsPlayer1.StatsForUser();


        Stats statsPlayer2=new Stats(player2);
        statsPlayer2.StatsForUser();

        statsPlayer1.updateStatsAfterBattle(result);
        statsPlayer2.updateStatsAfterBattle(-1*result);

    }
}
