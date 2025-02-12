package order_pack.model;

public class Product {
    @Id
    private int id;
    private String code;
    private String name;
    private float price;
    private int total;

    public Product() {}

    public Product(String code, String name, float price, int total) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(int age) {
        this.code = code;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int number) {
        this.total = number;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format(
            "Product \"%s\" with ID=%d, code=%s. Total number in stock = %d", name, id, code, total
        );
    }
}
