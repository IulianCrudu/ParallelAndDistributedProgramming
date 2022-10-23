package model;

public class Product {
    private final int id;
    private final int price;
    private int quantity;
    private final int initialQuantity;


    public Product(int id, int price, int quantity) {
        this.price = price;
        this.quantity = quantity;
        this.initialQuantity = quantity;
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public int getInitialQuantity() {
        return initialQuantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", price=" + price +
                ", quantity=" + quantity +
                ", initialQuantity=" + initialQuantity +
                '}';
    }
}
