package org.example.service;

import org.example.entity.Order;
import org.example.entity.Price;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Book {

    private final Map<Integer, Map<Character, TreeMap<Long, Integer>>> orders = new HashMap<>();
    private final Map<String, Order> activeOrders = new HashMap<>();

    /**
     * Обрабатывает входящую строку заявки и выполняет соответствующее действие.
     *
     * @param line Строка в формате semicolon-separated value, содержащая информацию о заявке.
     */
    public void processInput(String line) {
        String[] parts = line.split(";");
        String userId = parts[0];
        String clorderId = parts[1];
        char action = parts[2].charAt(0);
        int instrumentId = Integer.parseInt(parts[3]);
        char side = parts[4].charAt(0);
        long price = Long.parseLong(parts[5]);
        int amount = Integer.parseInt(parts[6]);
        int amountRest = Integer.parseInt(parts[7]);

        switch (action) {
            case '0':
                placeOrder(userId, clorderId, instrumentId, side, price, amount, amountRest);
                break;
            case '1':
                cancelOrder(userId, clorderId);
                break;
            case '2':
                executeOrder(userId, clorderId, instrumentId, side, price, amount);
                break;
        }
    }

    /**
     * Ставит новую заявку в книгу.
     *
     * @param userId      Идентификатор пользователя, который ставит заявку.
     * @param clorderId   Уникальный идентификатор заявки для пользователя.
     * @param instrumentId Идентификатор биржевого инструмента.
     * @param side        Сторона заявки (B - покупка, S - продажа).
     * @param price       Цена заявки.
     * @param amount      Начальный объем заявки.
     * @param amountRest  Остаток объема заявки.
     */
    private void placeOrder(String userId, String clorderId, int instrumentId, char side, long price, int amount, int amountRest) {
        Order order = new Order(userId, clorderId, price, amount, amountRest, side, instrumentId);
        activeOrders.put(userId + clorderId, order);

        orders.putIfAbsent(instrumentId, new HashMap<>());
        orders.get(instrumentId).putIfAbsent(side, new TreeMap<>());

        TreeMap<Long, Integer> priceMap = orders.get(instrumentId).get(side);
        priceMap.put(price, priceMap.getOrDefault(price, 0) + amount);

        Price bestPrice = getBestPrice(instrumentId, side);

        if (bestPrice.bestPrice == price) {
            System.out.printf("%d;%c;%d;%d%n", instrumentId, side, bestPrice.bestPrice, bestPrice.totalAmount);
        } else if (side == 'B' && price > bestPrice.bestPrice) {
            System.out.printf("%d;%c;%d;%d%n", instrumentId, side, price, amount);
        }
    }


    /**
     * Снимает заявку из книги.
     *
     * @param userId    Идентификатор пользователя, ставившего заявку.
     * @param clorderId Уникальный идентификатор заявки для пользователя.
     */
    private void cancelOrder(String userId, String clorderId) {
        Order order = activeOrders.remove(userId + clorderId);
        if (order != null) {
            int instrumentId = order.instrumentId;
            char side = order.side;
            TreeMap<Long, Integer> priceMap = orders.get(instrumentId).get(side);

            if (priceMap != null) {
                int amountToRemove = order.amountRest == 0 ? order.amount : order.amountRest;
                int remainingAmount = priceMap.get(order.price) - amountToRemove;

                if (remainingAmount <= 0) {
                    priceMap.remove(order.price);
                } else {
                    priceMap.put(order.price, remainingAmount);
                }

                if (priceMap.isEmpty()) {
                    orders.get(instrumentId).remove(side);
                    if (side == 'B') {
                        System.out.printf("%d;%c;%d;%d%n", instrumentId, side, 0, 0);
                    } else {
                        System.out.printf("%d;%c;%d;%d%n", instrumentId, side, 999999999999999999L, 0);
                    }
                } else {
                    Price bestPrice = getBestPrice(instrumentId, side);
                    if (bestPrice != null) {
                        System.out.printf("%d;%c;%d;%d%n", instrumentId, side, bestPrice.bestPrice, bestPrice.totalAmount);
                    }
                }
            }
        }
    }

    /**
     * Выполняет ордер, обновляя статусы активных заявок и лучшую цену.
     *
     * @param userId      Идентификатор пользователя, который ставит ордер.
     * @param clorderId   Уникальный идентификатор заявки для пользователя.
     * @param instrumentId Идентификатор биржевого инструмента.
     * @param side        Сторона заявки (B - покупка, S - продажа).
     * @param price       Цена заявки.
     * @param amount      Объем сделки.
     */
    private void executeOrder(String userId, String clorderId, int instrumentId, char side, long price, int amount) {
        String orderKey = userId + clorderId;
        Order order = activeOrders.get(orderKey);

        if (order != null) {
            int amountRest = order.amountRest - amount;

            if (amountRest <= 0) {
                activeOrders.remove(orderKey);
            } else {
                order.amountRest = amountRest;

                TreeMap<Long, Integer> priceMap = orders.get(instrumentId).get(side);
                if (priceMap != null && priceMap.containsKey(order.price)) {
                    int currentAmount = priceMap.get(order.price);
                    if (currentAmount > 0) {
                        priceMap.put(order.price, currentAmount - amount);

                        if (priceMap.get(order.price) <= 0) {
                            priceMap.remove(order.price);
                            if (priceMap.isEmpty()) {
                                orders.get(instrumentId).remove(side);
                                if (side == 'B') {
                                    System.out.printf("%d;%c;%d;%d%n", instrumentId, side, 0, 0);
                                } else {
                                    System.out.printf("%d;%c;%d;%d%n", instrumentId, side, 999999999999999999L, 0);
                                }
                                return;
                            }
                        }
                    }
                }
            }

            Price bestPrice = getBestPrice(instrumentId, side);
            if (bestPrice != null) {
                System.out.printf("%d;%c;%d;%d%n", instrumentId, side, bestPrice.bestPrice, bestPrice.totalAmount);
            }
        }
    }

    /**
     * Получает лучшую цену для указанного инструмента и стороны.
     *
     * @param instrumentId Идентификатор биржевого инструмента.
     * @param side        Сторона запроса (B - покупка, S - продажа).
     * @return Объект Price, содержащий лучшую цену и общий объем.
     */
    private Price getBestPrice(int instrumentId, char side) {
        TreeMap<Long, Integer> priceMap = orders.get(instrumentId).get(side);
        if (priceMap == null || priceMap.isEmpty()) {
            return null;
        }
        long bestPrice = side == 'B' ? priceMap.lastKey() : priceMap.firstKey();
        int totalAmount = priceMap.get(bestPrice);
        return new Price(bestPrice, totalAmount);
    }
}
