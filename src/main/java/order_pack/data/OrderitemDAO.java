package order_pack.data;

import order_pack.model.Orderitem;

import java.sql.Connection;

public class OrderitemDAO extends AbstractDAO<Orderitem> {

    public OrderitemDAO(Connection conn, String tabName) {
        super(conn, tabName);
    }
}
