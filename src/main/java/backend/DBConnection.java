package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection
{
    private static Connection conn;

    public DBConnection()
    {
        if (conn == null)
        {
            init();
        }
    }

    private void init()
    {
        String url = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12745785";
        String user = "sql12745785";
        String password = "BwBxhPpNBP";

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

    public static Connection getConnection() {

        if (conn == null ) {
            new DBConnection(); // Reinitialize the connection
        }

        return conn;
    }


    public static boolean isConnectionOpen() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}