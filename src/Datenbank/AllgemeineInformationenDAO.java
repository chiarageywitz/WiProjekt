package Datenbank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AllgemeineInformationenDAO {
    
    // NEUE Methode mit zusätzlichem Parameter für Betreuer-MNR
    public static void speichern(
            int mnr,
            String thema,
            String unternehmen,
            String zeitraum,
            String betreuerHft,
            String betreuerUnternehmen,
            boolean ndaNoetig,
            String ndaPfad,
            Integer betreuerMnr  // NEU: MNR des ausgewählten Betreuers
    ) throws Exception {

        String sql = """
            INSERT INTO allgemeine_informationen
            (mnr, thema, unternehmen, zeitraum, betreuer_hft, betreuer_unternehmen, nda_noetig, nda_dateipfad)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                thema = VALUES(thema),
                unternehmen = VALUES(unternehmen),
                zeitraum = VALUES(zeitraum),
                betreuer_hft = VALUES(betreuer_hft),
                betreuer_unternehmen = VALUES(betreuer_unternehmen),
                nda_noetig = VALUES(nda_noetig),
                nda_dateipfad = VALUES(nda_dateipfad)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mnr);
            ps.setString(2, thema);
            ps.setString(3, unternehmen);
            ps.setString(4, zeitraum);
            ps.setString(5, betreuerHft);
            ps.setString(6, betreuerUnternehmen);
            ps.setBoolean(7, ndaNoetig);
            ps.setString(8, ndaPfad);

            ps.executeUpdate();
            
            // NEU: Benachrichtigung für spezifischen Betreuer oder alle Betreuer
            erstelleBenachrichtigung(conn, mnr, betreuerMnr);
        }
    }
    
    // Alte Methode für Kompatibilität (falls noch irgendwo aufgerufen)
    public static void speichern(
            int mnr,
            String thema,
            String unternehmen,
            String zeitraum,
            String betreuerHft,
            String betreuerUnternehmen,
            boolean ndaNoetig,
            String ndaPfad
    ) throws Exception {
        speichern(mnr, thema, unternehmen, zeitraum, betreuerHft, 
                 betreuerUnternehmen, ndaNoetig, ndaPfad, null);
    }
    
    // NEU: Erstellt Benachrichtigung für spezifischen Betreuer oder alle Betreuer
    private static void erstelleBenachrichtigung(Connection conn, int studentMnr, Integer betreuerMnr) throws Exception {
        // Studentenname und Thema holen
        String studentName = "";
        String thema = "";
        
        String sqlStudent = """
            SELECT s.Vorname, s.Nachname, a.thema 
            FROM studentendb s
            LEFT JOIN allgemeine_informationen a ON s.MNR = a.mnr
            WHERE s.MNR = ?
        """;
        
        try (PreparedStatement psStudent = conn.prepareStatement(sqlStudent)) {
            psStudent.setInt(1, studentMnr);
            ResultSet rs = psStudent.executeQuery();
            if (rs.next()) {
                studentName = rs.getString("Vorname") + " " + rs.getString("Nachname");
                thema = rs.getString("thema");
            }
        }
        
        String benachrichtigungstext = "Neuer Antrag von " + studentName + 
                                      (thema != null && !thema.isEmpty() ? 
                                       " zum Thema: " + thema : "");
        
        if (betreuerMnr != null) {
            // Benachrichtigung nur an spezifischen Betreuer
            String sqlNotif = """
                INSERT INTO benachrichtigungen 
                (mnr, text, datum, empfaenger_rolle, sender_mnr, empfanger_mnr)
                VALUES (?, ?, CURDATE(), 'betreuer', ?, ?)
            """;
            
            try (PreparedStatement psNotif = conn.prepareStatement(sqlNotif)) {
                psNotif.setInt(1, betreuerMnr); // Spezifischer Empfänger-MNR
                psNotif.setString(2, benachrichtigungstext);
                psNotif.setInt(3, studentMnr); // Sender
                psNotif.setInt(4, betreuerMnr); // Empfänger
                psNotif.executeUpdate();
            }
        } else {
            // Benachrichtigung an alle Betreuer (Fallback)
            String sqlNotif = """
                INSERT INTO benachrichtigungen 
                (mnr, text, datum, empfaenger_rolle, sender_mnr)
                VALUES (NULL, ?, CURDATE(), 'betreuer', ?)
            """;
            
            try (PreparedStatement psNotif = conn.prepareStatement(sqlNotif)) {
                psNotif.setString(1, benachrichtigungstext);
                psNotif.setInt(2, studentMnr);
                psNotif.executeUpdate();
            }
        }
    }
}