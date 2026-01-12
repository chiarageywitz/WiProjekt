package Datenbank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO-Klasse für Notenabgabe.
 * Verwaltet Noteneingabe und Benachrichtigungen nach Notenfrist.
 */
public class NotenDAO {

    /**
     * Speichert/aktualisiert eine Note für einen Studenten
     */
    public static void noteSpeichern(int studentMnr, double note, int betreuerMnr, String kommentar) throws Exception {
        String sql = """
            INSERT INTO noten 
            (student_mnr, betreuer_mnr, note, kommentar, datum)
            VALUES (?, ?, ?, ?, NOW())
            ON DUPLICATE KEY UPDATE
                note = VALUES(note),
                kommentar = VALUES(kommentar),
                datum = NOW()
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentMnr);
            ps.setInt(2, betreuerMnr);
            ps.setDouble(3, note);
            ps.setString(4, kommentar);

            ps.executeUpdate();

            // Benachrichtigung wird NICHT sofort gesendet, nur nach Notenfrist
        }
    }

    /**
     * Holt die Note eines Studenten
     */
    public static Double getNote(int studentMnr) throws Exception {
        String sql = "SELECT note FROM noten WHERE student_mnr = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentMnr);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("note");
            }
        }
        return null;
    }

    /**
     * Gibt alle Noten frei und versendet Benachrichtigungen an Studenten
     * Diese Methode wird vom Studiendekan nach Ablauf der Notenfrist aufgerufen
     */
    public static void notenFreigeben() throws Exception {
        String sql = """
            SELECT n.mnr, n.endnote, s.Vorname, s.Nachname
            FROM noten n
            JOIN studentendb s ON n.mnr = s.MNR
            WHERE n.freigegeben = false AND n.endnote IS NOT NULL
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int studentMnr = rs.getInt("mnr");
                double endnote = rs.getDouble("endnote");

                // Benachrichtigung erstellen
                String text = "Ihre Bachelorarbeit wurde bewertet. Endnote: " + 
                             String.format("%.1f", endnote).replace(".", ",");

                BenachrichtigungDAO.erstellen(
                    studentMnr,
                    text,
                    "student",
                    null,
                    studentMnr
                );

                // Als freigegeben markieren
                markiereFreigegeben(conn, studentMnr);
            }
        }
    }

    /**
     * Markiert eine Note als freigegeben
     */
    private static void markiereFreigegeben(Connection conn, int studentMnr) throws Exception {
        String sql = "UPDATE noten SET freigegeben = true, freigabedatum = CURDATE() WHERE mnr = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentMnr);
            ps.executeUpdate();
        }
    }

    /**
     * Prüft, ob ein Student bereits eine Note hat
     */
    public static boolean hatNote(int studentMnr) throws Exception {
        String sql = "SELECT COUNT(*) as count FROM noten WHERE student_mnr = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentMnr);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        }
        return false;
    }
}
