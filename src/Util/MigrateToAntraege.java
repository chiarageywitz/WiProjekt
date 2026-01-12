package Util;

import java.sql.*;

public class MigrateToAntraege {
    public static void main(String[] args) {
        String url = "jdbc:mysql://3.69.96.96:80/db1";
        String user = "db1";
        String password = "!db1.wip25?SS1";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Alle Daten aus allgemeine_informationen holen
            String selectSql = """
                SELECT ai.*, s.MNR as betreuer_mnr
                FROM allgemeine_informationen ai
                LEFT JOIN studentendb s ON TRIM(ai.betreuer_hft) = CONCAT(s.Vorname, ' ', s.Nachname)
                WHERE s.Rolle = 'betreuer'
                """;
            
            String insertSql = """
                INSERT INTO antraege 
                (student_mnr, thema, unternehmen, zeitraum, betreuer_hft, betreuer_unternehmen, 
                 nda_noetig, nda_dateipfad, betreuer_mnr, status, datum)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'offen', CURDATE())
                """;
            
            int count = 0;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql);
                 PreparedStatement ps = conn.prepareStatement(insertSql)) {
                
                while (rs.next()) {
                    ps.setInt(1, rs.getInt("mnr"));
                    ps.setString(2, rs.getString("thema"));
                    ps.setString(3, rs.getString("unternehmen"));
                    ps.setString(4, rs.getString("zeitraum"));
                    ps.setString(5, rs.getString("betreuer_hft"));
                    ps.setString(6, rs.getString("betreuer_unternehmen"));
                    ps.setBoolean(7, rs.getBoolean("nda_noetig"));
                    ps.setString(8, rs.getString("nda_dateipfad"));
                    
                    int betreuerMnr = rs.getInt("betreuer_mnr");
                    if (rs.wasNull() || betreuerMnr == 0) {
                        // Fallback: Suche nach "Sumeyra Sumeyra"
                        ps.setInt(9, 2); // MNR 2 ist Sumeyra
                    } else {
                        ps.setInt(9, betreuerMnr);
                    }
                    
                    ps.executeUpdate();
                    count++;
                    
                    System.out.printf("Migriert: MNR %d, Thema: %s, Betreuer-MNR: %d%n",
                        rs.getInt("mnr"),
                        rs.getString("thema"),
                        betreuerMnr == 0 ? 2 : betreuerMnr
                    );
                }
            }
            
            System.out.println("\n✓ " + count + " Einträge erfolgreich migriert!");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
