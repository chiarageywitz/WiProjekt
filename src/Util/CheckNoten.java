package Util;

import java.sql.*;

public class CheckNoten {
    public static void main(String[] args) {
        String url = "jdbc:mysql://3.69.96.96:80/db1";
        String user = "db1";
        String password = "!db1.wip25?SS1";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("\n=== NOTEN TABELLE ===");
            String sql = "SELECT * FROM noten";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (!rs.next()) {
                    System.out.println("KEINE NOTEN EINGETRAGEN!");
                } else {
                    do {
                        System.out.printf("MNR: %d | Betreuer: %s | Dekan: %s | Endnote: %s | Freigegeben: %s%n",
                            rs.getInt("mnr"),
                            rs.getObject("note_betreuer") != null ? rs.getDouble("note_betreuer") : "NULL",
                            rs.getObject("note_studiendekan") != null ? rs.getDouble("note_studiendekan") : "NULL",
                            rs.getObject("endnote") != null ? rs.getDouble("endnote") : "NULL",
                            rs.getBoolean("freigegeben") ? "JA" : "NEIN"
                        );
                    } while (rs.next());
                }
            }
            
            System.out.println("\n=== TABELLEN-STRUKTUR ===");
            String structSql = "DESCRIBE noten";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(structSql)) {
                while (rs.next()) {
                    System.out.printf("Feld: %-20s Typ: %-20s NULL: %s%n",
                        rs.getString("Field"),
                        rs.getString("Type"),
                        rs.getString("Null")
                    );
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
