package Util;

import java.sql.*;

public class ShowAllgemeineInfo {
    public static void main(String[] args) {
        String url = "jdbc:mysql://3.69.96.96:80/db1";
        String user = "db1";
        String password = "!db1.wip25?SS1";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT * FROM allgemeine_informationen ORDER BY mnr";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                System.out.println("\n=== ALLGEMEINE INFORMATIONEN (ALT) ===\n");
                
                while (rs.next()) {
                    System.out.println("MNR: " + rs.getInt("mnr"));
                    System.out.println("  Thema: " + rs.getString("thema"));
                    System.out.println("  Unternehmen: " + rs.getString("unternehmen"));
                    System.out.println("  Betreuer HFT: " + rs.getString("betreuer_hft"));
                    System.out.println("  Betreuer Unternehmen: " + rs.getString("betreuer_unternehmen"));
                    System.out.println();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
