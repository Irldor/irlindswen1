package com.company.cards;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data


public class CardStack {
   private final List<Card> card = new ArrayList<>();

   public void addCardToStack(){}
}
