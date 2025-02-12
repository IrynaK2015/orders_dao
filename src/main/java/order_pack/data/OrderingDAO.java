package order_pack.data;

import order_pack.model.Ordering;

import java.sql.Connection;

public class OrderingDAO extends AbstractDAO<Ordering> {


    public OrderingDAO(Connection conn, String tabName) {
        super(conn, tabName);
    }
}
