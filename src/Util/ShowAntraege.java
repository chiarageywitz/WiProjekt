package Util;

import java.sql.*;

public class ShowAntraege {
    public static void main(String[] args) {
        String url = "jdbc:mysql://3.69.96.96:80/db1";
        String user = "db1";
        String password = "!db1.wip25?SS1";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT * FROM antraege ORDER BY antrag_id";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                System.out.println("\n=== ALLE ANTRÄGE ===\n");
                
                while (rs.next()) {
                    System.out.println("Antrag ID: " + rs.getInt("antrag_id"));
                    System.out.println("  Student MNR: " + rs.getInt("student_mnr"));
                    System.out.println("  Thema: " + rs.getString("thema"));
                    System.out.println("  Betreuer MNR: " + rs.getInt("betreuer_mnr"));
                    System.out.println("  Status: " + rs.getString("status"));
                    System.out.println("  Betreuer genehmigt: " + rs.getBoolean("betreuer_genehmigt"));
                    System.out.println("  Dekan genehmigt: " + rs.getBoolean("dekan_genehmigt"));
                    System.out.println("  Datum: " + rs.getDate("datum"));
                    System.out.println();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
