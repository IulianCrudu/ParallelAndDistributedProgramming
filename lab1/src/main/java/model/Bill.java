package model;

import java.util.HashMap;

public class Bill {
    private final double totalPrice;
    private final HashMap<Integer, Integer> productList;

    public Bill(double totalPrice, HashMap<Integer, Integer> productList) {
        this.totalPrice = totalPrice;
        this.productList = productList;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "totalPrice=" + totalPrice +
                ", productList=" + productList +
                '}';
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public HashMap<Integer, Integer> getProductList() {
        return productList;
    }
}
