package order_pack.data;

import order_pack.model.Id;
import order_pack.model.Orderitem;
import order_pack.model.Product;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.Date;

public abstract class AbstractDAO<T> {
    private final Connection conn;
    private final String table;

    public AbstractDAO(Connection conn, String table) {
        this.conn = conn;
        this.table = table;
    }

    public void createTable(Class<T> cls) {
        Field[] fields = cls.getDeclaredFields();
        Field id = getPrimaryKeyField(null, fields);

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ")
                .append(table)
                .append("(");

        sql.append(id.getName())
                .append(" ")
                .append(" SERIAL PRIMARY KEY,");

        for (Field f : fields) {
            if (f != id && f.getType() != Product.class && f.getType() != ArrayList.class) {
                f.setAccessible(true);

                sql.append(f.getName()).append(" ");

                if (f.getType() == int.class) {
                    sql.append("INT,");
                } else if (f.getType() == String.class) {
                    sql.append("VARCHAR(100),");
                } else if (f.getType() == float.class || f.getType() == double.class) {
                    sql.append("NUMERIC (10, 2),");
                } else if (f.getType() == Date.class) {
                    sql.append("DATE NOT NULL DEFAULT CURRENT_DATE,");
                } else {
                    System.out.println(f.getType());
                    throw new RuntimeException("Wrong type");
                }
            }
        }

        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");

        try {
            try (Statement st = conn.createStatement()) {
                st.execute(sql.toString());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void add(T t) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            Field id = getPrimaryKeyField(t, fields);

            StringBuilder names = new StringBuilder();
            StringBuilder values = new StringBuilder();

            // insert into t (name,age) values("..",..)

            for (Field f : fields) {
                if (f != id && f.getType() != ArrayList.class && f.getType() != Date.class
                        && f.getType() != Product.class
                ) {
                    f.setAccessible(true);

                    names.append(f.getName()).append(',');
                    values.append("'").append(f.get(t)).append("',");
                }
            }

            names.deleteCharAt(names.length() - 1); // last ','
            values.deleteCharAt(values.length() - 1);

            String sql = "INSERT INTO " + table + "(" + names.toString() +
                    ") VALUES(" + values.toString() + ") RETURNING id";

            try (Statement st = conn.createStatement()) {
                st.execute(sql);
                ResultSet rs = st.getResultSet();
                if (rs.next()) {
                    int objId = rs.getInt(1);
                    if (objId > 0)
                        t.getClass().getMethod("setId", int.class).invoke(t, objId);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void update(T t) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            Field id = getPrimaryKeyField(t, fields);

            StringBuilder sb = new StringBuilder();

            for (Field f : fields) {
                if (f != id && f.getType() != ArrayList.class && f.getType() != Date.class
                        && f.getType() != Product.class
                ) {
                    f.setAccessible(true);

                    sb.append(f.getName())
                            .append(" = ")
                            .append("'")
                            .append(f.get(t))
                            .append("'")
                            .append(',');
                }
            }

            sb.deleteCharAt(sb.length() - 1);

            String sql = "UPDATE " + table + " SET " + sb.toString() + " WHERE " +
                    id.getName() + " = '" + id.get(t) + "'";
            try (Statement st = conn.createStatement()) {
                st.execute(sql);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void delete(T t) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            Field id = getPrimaryKeyField(t, fields);

            String sql = "DELETE FROM " + table + " WHERE " + id.getName() +
                    " = '" + id.get(t) + "'";

            try (Statement st = conn.createStatement()) {
                st.execute(sql);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<T> getAll(Class<T> cls) {
        String sql = "SELECT * FROM " + table;

        return doSelect(cls, sql);
    }

    public List<T> getAllFiltered(Class<T> cls, HashMap<String, Integer> filters) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : filters.entrySet()) {
            sb.append(" AND " + entry.getKey() + "='" + entry.getValue() + "'");
        }
        sb.delete(0, 4);
        String sql = "SELECT * FROM " + table + " WHERE " + sb;

        return doSelect(cls, sql);
    }

    private List<T> doSelect(Class<T> cls, String sql) {
        List<T> res = new ArrayList<>();
        try {
            try (Statement st = conn.createStatement()) {

                try (ResultSet rs = st.executeQuery(sql)) {
                    ResultSetMetaData md = rs.getMetaData();

                    while (rs.next()) {
                        T t = cls.getDeclaredConstructor().newInstance();

                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            String columnName = md.getColumnName(i);
                            Field field = cls.getDeclaredField(columnName);
                            field.setAccessible(true);
                            if (md.getColumnTypeName(i).equals("numeric") && md.getScale(i) > 0)
                                field.set(t, rs.getFloat(columnName));
                            else
                                field.set(t, rs.getObject(columnName));
                        }

                        res.add(t);
                    }
                }
            }

            return res;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public T getSingle (Class<T> cls, int id) {
        T item = null;
        Field idName = getPrimaryKeyField(item, cls.getDeclaredFields());
        try {
            item = cls.getDeclaredConstructor().newInstance();
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery(
                        "SELECT * FROM " + table + " WHERE " + idName.getName() + " = '" + id + "'"
                )) {
                    ResultSetMetaData md = rs.getMetaData();

                   if(rs.next()) {
                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            String columnName = md.getColumnName(i);
                            Field field = cls.getDeclaredField(columnName);
                            field.setAccessible(true);
                            if (md.getColumnTypeName(i).equals("numeric") && md.getScale(i) > 0)
                                field.set(item, rs.getFloat(columnName));
                            else
                                field.set(item, rs.getObject(columnName));
                        }
                    }
                }
            }

            return item;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Field getPrimaryKeyField(T t, Field[] fields) {
        Field result = null;

        for (Field f : fields) {
            if (f.isAnnotationPresent(Id.class)) {
                result = f;
                result.setAccessible(true);
                break;
            }
        }

        if (result == null)
            throw new RuntimeException("No Id field found");

        return result;
    }
}
