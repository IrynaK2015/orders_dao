package order_pack.model;

import order_pack.utilities.MathHelper;

public class Orderitem {
    @Id
    private int id;
    private int orderid;
    private int productid;
    private int quantity;
    private float price;
    private Product product;

    public Orderitem() {}

    public Orderitem(int quantity, Product product) {
        setProduct(product);
        setQuantity(quantity);
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.price = product.getPrice() * this.quantity;
        MathHelper.round(this.price, 2);
    }

    public float getPrice() {
        return price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setOrderId(int orderId) {
        this.orderid = orderId;
    }

    public int getOrderId() {
        return orderid;
    }

    public int getProductid() {
        return productid;
    }

    public void setProduct(Product product) {
        this.productid = product.getId();
        this.product = product;
    }

    @Override
    public String toString() {
        return String.format(
            "%d items of \"%s\", total value = %.2f",
            quantity, product.getName(), price
        );
    }
}
