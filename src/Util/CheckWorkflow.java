package Util;

import java.sql.*;

public class CheckWorkflow {
    public static void main(String[] args) {
        String url = "jdbc:mysql://3.69.96.96:80/db1";
        String user = "db1";
        String password = "!db1.wip25?SS1";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("\n=== ANTRÄGE ===");
            String sql = "SELECT a.*, s.Email, s.Vorname FROM antraege a " +
                        "LEFT JOIN studentendb s ON a.student_mnr = s.MNR " +
                        "ORDER BY a.antrag_id";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (!rs.next()) {
                    System.out.println("KEINE ANTRÄGE GEFUNDEN!");
                } else {
                    do {
                        System.out.printf("ID: %d | Student: %s (%s) | Thema: %s | Status: %s | Betreuer-MNR: %d%n",
                            rs.getInt("antrag_id"),
                            rs.getString("Vorname"),
                            rs.getString("Email"),
                            rs.getString("thema"),
                            rs.getString("status"),
                            rs.getInt("betreuer_mnr")
                        );
                    } while (rs.next());
                }
            }
            
            System.out.println("\n=== ALLGEMEINE INFORMATIONEN (ALT) ===");
            String sql2 = "SELECT ai.*, s.Email FROM allgemeine_informationen ai " +
                         "LEFT JOIN studentendb s ON ai.mnr = s.MNR";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql2)) {
                if (!rs.next()) {
                    System.out.println("KEINE DATEN!");
                } else {
                    do {
                        System.out.printf("MNR: %d | Email: %s | Thema: %s | Betreuer: %s%n",
                            rs.getInt("mnr"),
                            rs.getString("Email"),
                            rs.getString("thema"),
                            rs.getString("betreuer_hft")
                        );
                    } while (rs.next());
                }
            }
            
            System.out.println("\n=== ANMELDUNGEN ===");
            String sql3 = "SELECT * FROM anmeldungen";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql3)) {
                if (!rs.next()) {
                    System.out.println("KEINE ANMELDUNGEN!");
                } else {
                    do {
                        System.out.printf("Student MNR: %d | Datum: %s%n",
                            rs.getInt("student_mnr"),
                            rs.getDate("anmeldedatum")
                        );
                    } while (rs.next());
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
