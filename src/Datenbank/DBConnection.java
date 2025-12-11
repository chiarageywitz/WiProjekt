package Datenbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://3.69.96.96:80/db1";
    private static final String USER = "db1";
    private static final String PASSWORD = "!db1.wip25?SS1";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    }
