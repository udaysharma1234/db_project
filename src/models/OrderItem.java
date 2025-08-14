package models;

public class OrderItem {
    private int id;
    private String name;
    private int quantity;
    private String comment;
    private String status;

    public OrderItem(int id, String name, int quantity, String comment) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.comment = comment;
        this.status = "Received";
    }

    public OrderItem(int id, String name, int quantity, String comment, String status) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.comment = comment;
        if (status.equals("sent")) {
            this.status = "Received";
        } else {
            this.status = status;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getComment() {
        return comment;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return name + " (Quantity: " + quantity + ") - Comment: " + comment;
    }
}
