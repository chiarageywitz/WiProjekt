package Datenbank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * DAO-Klasse für Benachrichtigungen.
 * Verwaltet alle Benachrichtigungen im System.
 */
public class BenachrichtigungDAO {

    /**
     * Erstellt eine neue Benachrichtigung
     * 
     * @param empfaengerMnr MNR des Empfängers (null wenn an alle einer Rolle)
     * @param text Benachrichtigungstext
     * @param empfaengerRolle Rolle des Empfängers ("student", "betreuer", "studiendekan")
     * @param senderMnr MNR des Absenders (kann null sein)
     * @param direkterEmpfaenger Spezifischer Empfänger (überschreibt Rolle)
     */
    public static void erstellen(Integer empfaengerMnr, String text, String empfaengerRolle, 
                                 Integer senderMnr, Integer direkterEmpfaenger) throws Exception {
        String sql;
        
        if (direkterEmpfaenger != null) {
            // Direkte Benachrichtigung an spezifischen Nutzer
            sql = """
                INSERT INTO benachrichtigungen 
                (mnr, text, datum, empfaenger_rolle, sender_mnr, empfanger_mnr, gelesen)
                VALUES (?, ?, NOW(), ?, ?, ?, false)
            """;
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, direkterEmpfaenger);
                ps.setString(2, text);
                ps.setString(3, empfaengerRolle);
                if (senderMnr != null) {
                    ps.setInt(4, senderMnr);
                } else {
                    ps.setNull(4, java.sql.Types.INTEGER);
                }
                ps.setInt(5, direkterEmpfaenger);
                ps.executeUpdate();
            }
        } else if (empfaengerMnr != null) {
            // Benachrichtigung an spezifischen Nutzer (alte Variante)
            sql = """
                INSERT INTO benachrichtigungen 
                (mnr, text, datum, empfaenger_rolle, sender_mnr, gelesen)
                VALUES (?, ?, NOW(), ?, ?, false)
            """;
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, empfaengerMnr);
                ps.setString(2, text);
                ps.setString(3, empfaengerRolle);
                if (senderMnr != null) {
                    ps.setInt(4, senderMnr);
                } else {
                    ps.setNull(4, java.sql.Types.INTEGER);
                }
                ps.executeUpdate();
            }
        } else {
            // Broadcast an alle Nutzer einer Rolle
            sql = """
                INSERT INTO benachrichtigungen 
                (mnr, text, datum, empfaenger_rolle, sender_mnr, gelesen)
                SELECT MNR, ?, NOW(), ?, ?, false
                FROM studentendb 
                WHERE rolle = ?
            """;
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, text);
                ps.setString(2, empfaengerRolle);
                if (senderMnr != null) {
                    ps.setInt(3, senderMnr);
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                ps.setString(4, empfaengerRolle);
                ps.executeUpdate();
            }
        }
    }

    /**
     * Markiert eine Benachrichtigung als gelesen
     */
    public static void markiereGelesen(int benachrichtigungId) throws Exception {
        String sql = "UPDATE benachrichtigungen SET gelesen = true WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, benachrichtigungId);
            ps.executeUpdate();
        }
    }

    /**
     * Markiert alle Benachrichtigungen eines Nutzers als gelesen
     */
    public static void markiereAlleGelesen(int mnr) throws Exception {
        String sql = "UPDATE benachrichtigungen SET gelesen = true WHERE mnr = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mnr);
            ps.executeUpdate();
        }
    }

    /**
     * Zählt ungelesene Benachrichtigungen für einen Nutzer
     */
    public static int zaehleUngelesene(int mnr) throws Exception {
        String sql = "SELECT COUNT(*) as count FROM benachrichtigungen WHERE mnr = ? AND gelesen = false";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mnr);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    /**
     * Löscht alte Benachrichtigungen (älter als X Tage)
     */
    public static void loescheAlte(int tage) throws Exception {
        String sql = "DELETE FROM benachrichtigungen WHERE datum < DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tage);
            ps.executeUpdate();
        }
    }
}
