package order_pack.data;

import order_pack.model.Product;

import java.sql.Connection;

public class ProductDAO extends AbstractDAO<Product> {

    //public static String tabName = "product";

    public ProductDAO(Connection conn, String tabName) {
        super(conn, tabName);
    }
}