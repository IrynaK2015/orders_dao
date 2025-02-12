package order_pack;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import order_pack.utilities.ConnectionFactory;
import order_pack.data.*;
import order_pack.model.*;

public class Main {

    public static void main(String[] args) throws SQLException {
        Connection conn = ConnectionFactory.getConnection();

        cleanDatabase(conn);
        createTables(conn);
        createProducts(conn);
        createClient(conn);
        createOrder(conn);
        updateOrder(conn);

        conn.close();
    }

    protected static void cleanDatabase(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute("DROP TABLE IF EXISTS Client");
            st.execute("DROP TABLE IF EXISTS Product");
            st.execute("DROP TABLE IF EXISTS Ordering");
            st.execute("DROP TABLE IF EXISTS Orderitem");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (st != null)   st.close();
        }
    }


    protected static void createTables(Connection conn) {
        (new ClientDAO(conn, "Client")).createTable(Client.class);
        (new ProductDAO(conn, "Product")).createTable(Product.class);
        (new OrderingDAO(conn, "Ordering")).createTable(Ordering.class);
        (new OrderitemDAO(conn, "Orderitem")).createTable(Orderitem.class);
    }

    protected static void createProducts(Connection conn) {
        ProductDAO pDao = new ProductDAO(conn, "Product");
        pDao.add(
            new Product("MC01-003", "my product1", 25.06f, 30)
        );
        pDao.add(
            new Product("MC01-005", "my product2", 81.99f, 28)
        );
        System.out.println("\nProducts in stock:");
        List<Product> products = pDao.getAll(Product.class);
        for (Product prod : products) System.out.println(prod);
    }

    protected static void createClient(Connection conn) {
        ClientDAO cDao = new ClientDAO(conn, "Client");
        cDao.add(
            new Client("John Dou", "JohnDou@nomail.com")
        );
        System.out.println("\nRegistered clients:");
        List<Client> clients = cDao.getAll(Client.class);
        for (Client client : clients) System.out.println(client);
    }


    protected static void createOrder(Connection conn) {
        Ordering order = new Ordering(1);

        List<Product> products = (new ProductDAO(conn, "Product")).getAll(Product.class);
        for (Product prod : products)  {
            Orderitem item = new Orderitem(2, prod);
            order.addItem(item);
        }

        OrderingDAO oDao = new OrderingDAO(conn, "Ordering");
        try {
            try {
                conn.setAutoCommit(false);
                oDao.add(order);
                for (Orderitem orderItem : order.getItems()) {
                    orderItem.setOrderId(order.getId());
                    OrderitemDAO oiDao = new OrderitemDAO(conn, "Orderitem");
                    oiDao.add(orderItem);
                }

                conn.commit();
                System.out.println("\n" + order);
            } catch (SQLException e) {
                conn.rollback();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Order creation error " + e.getMessage());
        }
    }

    protected static void updateOrder(Connection conn) {
        OrderingDAO oDao = new OrderingDAO(conn, "Ordering");
        Ordering foundOrder = oDao.getSingle(Ordering.class, 1);
        foundOrder.startProgress();
        System.out.println("\nUpdated order\n" + foundOrder);

        OrderitemDAO oiDao = new OrderitemDAO(conn, "Orderitem");
        HashMap<String, Integer> filter = new HashMap<>();
        Integer orderId = foundOrder.getId();
        filter.put("orderid", orderId);
        List<Orderitem> items = oiDao.getAllFiltered(Orderitem.class, filter);
        oiDao.delete(items.get(0));

        ProductDAO pDao = new ProductDAO(conn, "Product");
        for (Orderitem orderItem : oiDao.getAllFiltered(Orderitem.class, filter)) {
            orderItem.setProduct(
                pDao.getSingle(Product.class, orderItem.getProductid())
            );
            foundOrder.addItem(orderItem);
        }

        System.out.println("\n" + foundOrder);
    }
}
