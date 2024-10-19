package org.example.entity;

public class Order {
    public String userId;
    public String clorderId;
    public long price;
    public int amount;
    public int amountRest;
    public char side;
    public int instrumentId;

    public Order(String userId, String clorderId, long price, int amount, int amountRest, char side, int instrumentId) {
        this.userId = userId;
        this.clorderId = clorderId;
        this.price = price;
        this.amount = amount;
        this.amountRest = amountRest;
        this.side = side;
        this.instrumentId = instrumentId;
    }
}