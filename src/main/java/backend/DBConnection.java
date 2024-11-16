package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection conn = null;

    public DBConnection() {
        init();
    }

    private void init() {
        String url = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12744385";
        String user = "sql12744385";
        String password = "pnJrbIX6gQ";

        try {
            conn = DriverManager.getConnection(url, user, password);

            if (conn != null) {
                System.out.println("DB CONNECTED");
            } else {
                System.out.println("DB NOT CONNECTED");
            }

        } catch (SQLException e) {
            System.out.println("DB NOT CONNECTED");
            e.printStackTrace();
        }

    }
    public static   Connection getConnection() {
        return conn;
    }

}
