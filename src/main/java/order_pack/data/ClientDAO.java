package order_pack.data;

import order_pack.model.Client;

import java.sql.Connection;

public class ClientDAO extends AbstractDAO<Client> {

    //public static String tabName = "client";

    public ClientDAO(Connection conn, String tabName) {
        super(conn, tabName);
    }
}
