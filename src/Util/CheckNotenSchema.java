package Util;

import java.sql.*;

public class CheckNotenSchema {
    public static void main(String[] args) {
        String url = "jdbc:mysql://3.69.96.96:80/db1";
        String user = "db1";
        String password = "!db1.wip25?SS1";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("=== NOTEN TABELLE SCHEMA ===");
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "noten", null);
            
            while (columns.next()) {
                System.out.printf("Spalte: %-20s Typ: %s%n", 
                    columns.getString("COLUMN_NAME"),
                    columns.getString("TYPE_NAME"));
            }
            
            System.out.println("\n=== NOTEN DATEN ===");
            String sql = "SELECT * FROM noten";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                
                // Header
                for (int i = 1; i <= columnCount; i++) {
                    System.out.printf("%-20s ", rsmd.getColumnName(i));
                }
                System.out.println();
                System.out.println("-".repeat(columnCount * 21));
                
                // Daten
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.printf("%-20s ", rs.getString(i));
                    }
                    System.out.println();
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
