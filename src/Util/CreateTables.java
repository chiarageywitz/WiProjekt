package Util;

import java.sql.Connection;
import java.sql.Statement;
import Datenbank.DBConnection;

/**
 * Erstellt alle benötigten Tabellen für das Bachelorarbeit-System.
 */
public class CreateTables {
    
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  Erstelle Datenbank-Tabellen");
        System.out.println("==============================================\n");
        
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            
            // Tabelle: antraege
            System.out.print("Erstelle Tabelle 'antraege'... ");
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS antraege (" +
                "    antrag_id INT PRIMARY KEY AUTO_INCREMENT," +
                "    student_mnr INT NOT NULL," +
                "    thema VARCHAR(500)," +
                "    unternehmen VARCHAR(300)," +
                "    zeitraum VARCHAR(100)," +
                "    betreuer_hft VARCHAR(200)," +
                "    betreuer_unternehmen VARCHAR(200)," +
                "    nda_noetig BOOLEAN DEFAULT false," +
                "    nda_dateipfad VARCHAR(500)," +
                "    betreuer_mnr INT," +
                "    status VARCHAR(50) DEFAULT 'offen'," +
                "    datum DATE," +
                "    ablehnungsgrund TEXT" +
                ")"
            );
            System.out.println("OK");
            
            // Tabelle: versionen
            System.out.print("Erstelle Tabelle 'versionen'... ");
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS versionen (" +
                "    version_id INT PRIMARY KEY AUTO_INCREMENT," +
                "    student_mnr INT NOT NULL," +
                "    betreuer_mnr INT NOT NULL," +
                "    dateipfad VARCHAR(500) NOT NULL," +
                "    typ VARCHAR(50) NOT NULL," +
                "    datum DATETIME NOT NULL," +
                "    hochgeladen_von VARCHAR(50) NOT NULL," +
                "    kommentar TEXT" +
                ")"
            );
            System.out.println("OK");
            
            // Tabelle: feedback
            System.out.print("Erstelle Tabelle 'feedback'... ");
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS feedback (" +
                "    feedback_id INT PRIMARY KEY AUTO_INCREMENT," +
                "    version_id INT NOT NULL," +
                "    feedback_text TEXT NOT NULL," +
                "    datum DATETIME NOT NULL," +
                "    verfasser_mnr INT NOT NULL" +
                ")"
            );
            System.out.println("OK");
            
            // Tabelle: anmeldungen
            System.out.print("Erstelle Tabelle 'anmeldungen'... ");
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS anmeldungen (" +
                "    anmeldung_id INT PRIMARY KEY AUTO_INCREMENT," +
                "    student_mnr INT NOT NULL UNIQUE," +
                "    betreuer_mnr INT NOT NULL," +
                "    datum DATE NOT NULL," +
                "    veroeffentlichung_erlaubt BOOLEAN DEFAULT false" +
                ")"
            );
            System.out.println("OK");
            
            // Status-Spalte zu allgemeine_informationen hinzufügen
            System.out.print("Aktualisiere Tabelle 'allgemeine_informationen'... ");
            try {
                stmt.executeUpdate(
                    "ALTER TABLE allgemeine_informationen ADD COLUMN status VARCHAR(50) DEFAULT 'entwurf'"
                );
                System.out.println("OK (Spalte hinzugefügt)");
            } catch (Exception e) {
                System.out.println("OK (bereits vorhanden)");
            }
            
            // Indices
            System.out.print("Erstelle Indices... ");
            try { stmt.executeUpdate("CREATE INDEX idx_antraege_student ON antraege(student_mnr)"); } catch (Exception e) {}
            try { stmt.executeUpdate("CREATE INDEX idx_antraege_betreuer ON antraege(betreuer_mnr)"); } catch (Exception e) {}
            try { stmt.executeUpdate("CREATE INDEX idx_antraege_status ON antraege(status)"); } catch (Exception e) {}
            try { stmt.executeUpdate("CREATE INDEX idx_benachrichtigungen_mnr ON benachrichtigungen(mnr)"); } catch (Exception e) {}
            try { stmt.executeUpdate("CREATE INDEX idx_benachrichtigungen_gelesen ON benachrichtigungen(gelesen)"); } catch (Exception e) {}
            try { stmt.executeUpdate("CREATE INDEX idx_versionen_student ON versionen(student_mnr)"); } catch (Exception e) {}
            try { stmt.executeUpdate("CREATE INDEX idx_versionen_betreuer ON versionen(betreuer_mnr)"); } catch (Exception e) {}
            try { stmt.executeUpdate("CREATE INDEX idx_feedback_version ON feedback(version_id)"); } catch (Exception e) {}
            System.out.println("OK");
            
            // Tabelle noten prüfen/aktualisieren
            System.out.print("Aktualisiere Tabelle 'noten'... ");
            try {
                stmt.executeUpdate(
                    "ALTER TABLE noten ADD COLUMN benachrichtigt BOOLEAN DEFAULT false"
                );
                System.out.println("OK (Spalte hinzugefügt)");
            } catch (Exception e) {
                System.out.println("OK (bereits vorhanden)");
            }
            
            // Tabelle benachrichtigungen prüfen/aktualisieren
            System.out.print("Aktualisiere Tabelle 'benachrichtigungen'... ");
            try {
                stmt.executeUpdate(
                    "ALTER TABLE benachrichtigungen ADD COLUMN empfanger_mnr INT"
                );
            } catch (Exception e) {}
            try {
                stmt.executeUpdate(
                    "ALTER TABLE benachrichtigungen ADD COLUMN sender_mnr INT"
                );
            } catch (Exception e) {}
            try {
                stmt.executeUpdate(
                    "ALTER TABLE benachrichtigungen ADD COLUMN empfaenger_rolle VARCHAR(50)"
                );
            } catch (Exception e) {}
            try {
                stmt.executeUpdate(
                    "ALTER TABLE benachrichtigungen ADD COLUMN gelesen BOOLEAN DEFAULT false"
                );
            } catch (Exception e) {}
            System.out.println("OK");
            
            System.out.println("\n==============================================");
            System.out.println("? Alle Tabellen erfolgreich erstellt!");
            System.out.println("==============================================\n");
            
        } catch (Exception e) {
            System.err.println("\n? Fehler:");
            e.printStackTrace();
        }
    }
}
