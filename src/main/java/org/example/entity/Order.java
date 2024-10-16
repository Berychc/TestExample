package org.example.entity;

public class Order {

    private String userId;  // идентификатор пользователя, поставившего заявку
    private String clorderId; // уникальный пользовательский идентификатор заявки
    private char action; // действие с заявкой 0 — постановка заявки 1 — снятие заявки 2 — сведения заявки в сделку
    private Integer instrumentId; // уникальный номер инструмента
    private char side; // сторона заявки B — заявка в покупку S - заявка в продажу
    private long price; // цена заявки
    private Integer amount; // объем в операции
    private Integer amountRest; // объем оставшейся части заявки

    public Order(String userId, String clorderId, char action, Integer instrumentId, char side, long price, Integer amount, Integer amountRest) {
        this.userId = userId;
        this.clorderId = clorderId;
        this.action = action;
        this.instrumentId = instrumentId;
        this.side = side;
        this.price = price;
        this.amount = amount;
        this.amountRest = amountRest;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClorderId() {
        return clorderId;
    }

    public void setClorderId(String clorderId) {
        this.clorderId = clorderId;
    }

    public char getAction() {
        return action;
    }

    public void setAction(char action) {
        this.action = action;
    }

    public Integer getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Integer instrumentId) {
        this.instrumentId = instrumentId;
    }

    public char getSide() {
        return side;
    }

    public void setSide(char side) {
        this.side = side;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmountRest() {
        return amountRest;
    }

    public void setAmountRest(Integer amountRest) {
        this.amountRest = amountRest;
    }
}
