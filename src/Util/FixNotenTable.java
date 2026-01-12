package Util;

import java.sql.*;

public class FixNotenTable {
    public static void main(String[] args) {
        String url = "jdbc:mysql://3.69.96.96:80/db1";
        String user = "db1";
        String password = "!db1.wip25?SS1";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Tabelle löschen und neu erstellen
            String drop = "DROP TABLE IF EXISTS noten";
            conn.createStatement().executeUpdate(drop);
            System.out.println("✓ Alte Tabelle gelöscht");
            
            String create = """
                CREATE TABLE noten (
                    mnr INT PRIMARY KEY,
                    note_betreuer DECIMAL(2,1),
                    note_studiendekan DECIMAL(2,1),
                    endnote DECIMAL(2,1),
                    freigegeben BOOLEAN DEFAULT FALSE,
                    freigabedatum DATETIME,
                    FOREIGN KEY (mnr) REFERENCES studentendb(MNR) ON DELETE CASCADE
                )
                """;
            conn.createStatement().executeUpdate(create);
            System.out.println("✓ Neue Noten-Tabelle erstellt mit korrekter Struktur!");
            
            // Struktur anzeigen
            System.out.println("\n=== NEUE STRUKTUR ===");
            ResultSet rs = conn.createStatement().executeQuery("DESCRIBE noten");
            while (rs.next()) {
                System.out.printf("%-20s %-15s %s%n",
                    rs.getString("Field"),
                    rs.getString("Type"),
                    rs.getString("Null")
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
