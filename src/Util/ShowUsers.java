package Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Datenbank.DBConnection;

/**
 * Hilfsprogramm zum Anzeigen aller User in der Datenbank.
 * Nützlich zum Testen und um Login-Daten zu sehen.
 */
public class ShowUsers {
    
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  Bachelorarbeit-System - Benutzerübersicht  ");
        System.out.println("==============================================\n");
        
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("✓ Datenbankverbindung erfolgreich!\n");
            
            String sql = "SELECT MNR, Vorname, Nachname, email, rolle FROM studentendb ORDER BY rolle, MNR";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            System.out.println("Vorhandene Benutzer:");
            System.out.println("─────────────────────────────────────────────────────────────────────");
            System.out.printf("%-6s | %-15s | %-15s | %-25s | %-12s%n", 
                            "MNR", "Vorname", "Nachname", "Email", "Rolle");
            System.out.println("─────────────────────────────────────────────────────────────────────");
            
            int count = 0;
            while (rs.next()) {
                System.out.printf("%-6d | %-15s | %-15s | %-25s | %-12s%n",
                    rs.getInt("MNR"),
                    rs.getString("Vorname"),
                    rs.getString("Nachname"),
                    rs.getString("email"),
                    rs.getString("rolle")
                );
                count++;
            }
            
            System.out.println("─────────────────────────────────────────────────────────────────────");
            System.out.println("\nGesamt: " + count + " Benutzer gefunden\n");
            
            if (count == 0) {
                System.out.println("⚠️  KEINE BENUTZER GEFUNDEN!");
                System.out.println("Bitte führen Sie testdaten.sql aus, um Testbenutzer anzulegen:\n");
                System.out.println("mysql -h 3.69.96.96 -P 80 -u db1 -p db1 < testdaten.sql");
                System.out.println("\nOder verwenden Sie ein DB-Tool wie MySQL Workbench, DBeaver, etc.\n");
            } else {
                System.out.println("💡 Login-Test:");
                System.out.println("Alle Testbenutzer haben das Passwort: test123\n");
                System.out.println("Beispiele:");
                System.out.println("  • Student:      student1@test.de / test123");
                System.out.println("  • Betreuer:     betreuer1@test.de / test123");
                System.out.println("  • Studiendekan: dekan@test.de / test123\n");
            }
            
            // Tabellen prüfen
            System.out.println("Prüfe Systemtabellen...");
            checkTable(conn, "benachrichtigungen");
            checkTable(conn, "antraege");
            checkTable(conn, "versionen");
            checkTable(conn, "feedback");
            checkTable(conn, "noten");
            checkTable(conn, "anmeldungen");
            checkTable(conn, "allgemeine_informationen");
            
        } catch (Exception e) {
            System.err.println("✗ Fehler bei der Datenbankverbindung:");
            System.err.println("  " + e.getMessage());
            e.printStackTrace();
            System.out.println("\n💡 Prüfen Sie:");
            System.out.println("  1. Ist die Datenbank erreichbar?");
            System.out.println("  2. Sind die Zugangsdaten in DBConnection.java korrekt?");
            System.out.println("  3. Existiert die Datenbank 'db1'?");
        }
        
        System.out.println("==============================================");
    }
    
    private static void checkTable(Connection conn, String tableName) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) as count FROM " + tableName
            );
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("  ✓ Tabelle '" + tableName + "' vorhanden (" + count + " Einträge)");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Tabelle '" + tableName + "' NICHT vorhanden - bitte database_schema.sql ausführen!");
        }
    }
}
