package order_pack.model;

import order_pack.utilities.MathHelper;

import java.util.ArrayList;
import java.util.Date;

public class Ordering {
    @Id
    private int id;
    private int clientid;
    private String status;
    private Date created;

    private ArrayList<Orderitem> items = new ArrayList<>();

    public Ordering() {}

    public Ordering(int clientId) {
        this.clientid = clientId;
        this.status = Status.NEW.getDbName();
    }

    public void addItem(Orderitem item) {
        items.add(item);
    }

    public ArrayList<Orderitem> getItems() {
        return items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientid() {
        return clientid;
    }

    public void setClientid(int clientid) {
        this.clientid = clientid;
    }

    public String getStatus() {
        return status;
    }

    public void startProgress() {
        this.status = Status.IN_PROGRESS.getDbName();
    }

    public void complete() {
        this.status = Status.COMPLETED.getDbName();
    }

    public float getPrice() {
        float price = 0f;
        for (Orderitem item: items) {
            price += item.getPrice();
        }

        return MathHelper.round(price, 2);
    }

    public Date getCreated() {
        return created;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Orderitem item: items) {
            sb.append("\t" + item.toString() + "\n");
        }

        return "Order ID = " + id + ":\n" + sb.toString()
                + "\tStatus " + status + ", total value = " + getPrice();
    }

    enum Status {
        NEW("new"),
        IN_PROGRESS("in_progress"),
        COMPLETED("completed");

        private final String dbName;

        Status(String dbName) {
            this.dbName = dbName;
        }

        public String getDbName() {
            return dbName;
        }
    }
}
