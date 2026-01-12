package Util;

import java.sql.*;

public class ShowAllUsers {
    public static void main(String[] args) {
        String url = "jdbc:mysql://3.69.96.96:80/db1";
        String user = "db1";
        String password = "!db1.wip25?SS1";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT MNR, Email, Passwort, Vorname, Nachname, Rolle FROM studentendb ORDER BY MNR";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                System.out.println("\n=== ALLE BENUTZER ===\n");
                System.out.printf("%-5s %-30s %-20s %-15s %-15s %-12s%n", 
                    "MNR", "Email", "Passwort", "Vorname", "Nachname", "Rolle");
                System.out.println("-".repeat(100));
                
                while (rs.next()) {
                    System.out.printf("%-5d %-30s %-20s %-15s %-15s %-12s%n",
                        rs.getInt("MNR"),
                        rs.getString("Email"),
                        rs.getString("Passwort"),
                        rs.getString("Vorname"),
                        rs.getString("Nachname"),
                        rs.getString("Rolle"));
                }
                
                System.out.println("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
