package Datenbank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO-Klasse für Zwischenversionen und finale Abgabe.
 * Verwaltet Uploads, Feedback und Kommentare zwischen Student und Betreuer.
 */
public class VersionDAO {

    /**
     * Version-Datenklasse
     */
    public static class Version {
        public int versionId;
        public int studentMnr;
        public String studentName;
        public Integer betreuerMnr;
        public String betreuerName;
        public String dateipfad;
        public String typ; // "zwischenversion" oder "finale_abgabe"
        public String datum;
        public String hochgeladenVon; // "student" oder "betreuer"
        public String kommentar;
        public boolean hatFeedback;

        public Version(int versionId, int studentMnr, String studentName, Integer betreuerMnr,
                      String betreuerName, String dateipfad, String typ, String datum, 
                      String hochgeladenVon, String kommentar, boolean hatFeedback) {
            this.versionId = versionId;
            this.studentMnr = studentMnr;
            this.studentName = studentName;
            this.betreuerMnr = betreuerMnr;
            this.betreuerName = betreuerName;
            this.dateipfad = dateipfad;
            this.typ = typ;
            this.datum = datum;
            this.hochgeladenVon = hochgeladenVon;
            this.kommentar = kommentar;
            this.hatFeedback = hatFeedback;
        }
    }

    /**
     * Feedback-Datenklasse
     */
    public static class Feedback {
        public int feedbackId;
        public int versionId;
        public String text;
        public String datum;
        public String verfasser; // MNR oder Name

        public Feedback(int feedbackId, int versionId, String text, String datum, String verfasser) {
            this.feedbackId = feedbackId;
            this.versionId = versionId;
            this.text = text;
            this.datum = datum;
            this.verfasser = verfasser;
        }
    }

    /**
     * Lädt eine neue Zwischenversion hoch
     */
    public static void zwischenversionHochladen(int studentMnr, int betreuerMnr, 
                                                String dateipfad, String kommentar, 
                                                String hochgeladenVon) throws Exception {
        String sql = """
            INSERT INTO versionen 
            (student_mnr, betreuer_mnr, dateipfad, typ, datum, hochgeladen_von, kommentar)
            VALUES (?, ?, ?, 'zwischenversion', NOW(), ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, studentMnr);
            ps.setInt(2, betreuerMnr);
            ps.setString(3, dateipfad);
            ps.setString(4, hochgeladenVon);
            ps.setString(5, kommentar);

            ps.executeUpdate();

            // Benachrichtigung erstellen
            if ("student".equals(hochgeladenVon)) {
                BenachrichtigungDAO.erstellen(
                    betreuerMnr,
                    "Neuer Upload von Student (MNR: " + studentMnr + "): " + 
                    (kommentar != null && !kommentar.isEmpty() ? kommentar : "Zwischenversion hochgeladen"),
                    "betreuer",
                    studentMnr,
                    betreuerMnr
                );
            } else {
                BenachrichtigungDAO.erstellen(
                    studentMnr,
                    "Neuer Upload von Betreuer: " + 
                    (kommentar != null && !kommentar.isEmpty() ? kommentar : "Dokument hochgeladen"),
                    "student",
                    betreuerMnr,
                    studentMnr
                );
            }
        }
    }

    /**
     * Lädt die finale Bachelorarbeit hoch
     */
    public static void finaleAbgabe(int studentMnr, int betreuerMnr, String dateipfad, 
                                    String kommentar) throws Exception {
        String sql = """
            INSERT INTO versionen 
            (student_mnr, betreuer_mnr, dateipfad, typ, datum, hochgeladen_von, kommentar)
            VALUES (?, ?, ?, 'finale_abgabe', NOW(), 'student', ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentMnr);
            ps.setInt(2, betreuerMnr);
            ps.setString(3, dateipfad);
            ps.setString(4, kommentar);

            ps.executeUpdate();

            // Auch in alte abgabe_bachelorarbeit Tabelle eintragen
            AbgabeDAO.speichern(studentMnr, dateipfad);

            // Benachrichtigungen erstellen
            BenachrichtigungDAO.erstellen(
                betreuerMnr,
                "Student (MNR: " + studentMnr + ") hat die finale Bachelorarbeit abgegeben.",
                "betreuer",
                studentMnr,
                betreuerMnr
            );

            BenachrichtigungDAO.erstellen(
                studentMnr,
                "Ihre Bachelorarbeit wurde erfolgreich abgegeben. Sie werden nach der Notenvergabe benachrichtigt.",
                "student",
                null,
                studentMnr
            );
        }
    }

    /**
     * Fügt Feedback zu einer Version hinzu
     */
    public static void feedbackHinzufuegen(int versionId, String feedback, int verfasserMnr) throws Exception {
        String sql = """
            INSERT INTO feedback 
            (version_id, feedback_text, datum, verfasser_mnr)
            VALUES (?, ?, NOW(), ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, versionId);
            ps.setString(2, feedback);
            ps.setInt(3, verfasserMnr);

            ps.executeUpdate();

            // Benachrichtigung erstellen (an den jeweils anderen)
            int studentMnr = getStudentMnrFromVersion(conn, versionId);
            int betreuerMnr = getBetreuerMnrFromVersion(conn, versionId);

            // Bestimme, wer benachrichtigt werden soll
            if (verfasserMnr == studentMnr) {
                // Student hat kommentiert -> Betreuer benachrichtigen
                BenachrichtigungDAO.erstellen(
                    betreuerMnr,
                    "Neuer Kommentar vom Student zu Version #" + versionId,
                    "betreuer",
                    studentMnr,
                    betreuerMnr
                );
            } else {
                // Betreuer hat kommentiert -> Student benachrichtigen
                BenachrichtigungDAO.erstellen(
                    studentMnr,
                    "Neues Feedback vom Betreuer zu Ihrer Zwischenversion",
                    "student",
                    betreuerMnr,
                    studentMnr
                );
            }
        }
    }

    /**
     * Holt alle Versionen eines Studenten
     */
    public static List<Version> getVersionenForStudent(int studentMnr) throws Exception {
        List<Version> versionen = new ArrayList<>();

        String sql = """
            SELECT v.version_id, v.student_mnr, s.Vorname, s.Nachname, v.betreuer_mnr,
                   b.Vorname as BVorname, b.Nachname as BNachname, v.dateipfad, v.typ,
                   v.datum, v.hochgeladen_von, v.kommentar,
                   (SELECT COUNT(*) FROM feedback f WHERE f.version_id = v.version_id) as feedback_count
            FROM versionen v
            JOIN studentendb s ON v.student_mnr = s.MNR
            LEFT JOIN studentendb b ON v.betreuer_mnr = b.MNR
            WHERE v.student_mnr = ?
            ORDER BY v.datum DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentMnr);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                versionen.add(new Version(
                    rs.getInt("version_id"),
                    rs.getInt("student_mnr"),
                    rs.getString("Vorname") + " " + rs.getString("Nachname"),
                    rs.getInt("betreuer_mnr"),
                    rs.getString("BVorname") + " " + rs.getString("BNachname"),
                    rs.getString("dateipfad"),
                    rs.getString("typ"),
                    rs.getString("datum"),
                    rs.getString("hochgeladen_von"),
                    rs.getString("kommentar"),
                    rs.getInt("feedback_count") > 0
                ));
            }
        }

        return versionen;
    }

    /**
     * Holt alle Versionen für einen Betreuer
     */
    public static List<Version> getVersionenForBetreuer(int betreuerMnr) throws Exception {
        List<Version> versionen = new ArrayList<>();

        String sql = """
            SELECT v.version_id, v.student_mnr, s.Vorname, s.Nachname, v.betreuer_mnr,
                   v.dateipfad, v.typ, v.datum, v.hochgeladen_von, v.kommentar,
                   (SELECT COUNT(*) FROM feedback f WHERE f.version_id = v.version_id) as feedback_count
            FROM versionen v
            JOIN studentendb s ON v.student_mnr = s.MNR
            WHERE v.betreuer_mnr = ?
            ORDER BY v.datum DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, betreuerMnr);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                versionen.add(new Version(
                    rs.getInt("version_id"),
                    rs.getInt("student_mnr"),
                    rs.getString("Vorname") + " " + rs.getString("Nachname"),
                    rs.getInt("betreuer_mnr"),
                    null,
                    rs.getString("dateipfad"),
                    rs.getString("typ"),
                    rs.getString("datum"),
                    rs.getString("hochgeladen_von"),
                    rs.getString("kommentar"),
                    rs.getInt("feedback_count") > 0
                ));
            }
        }

        return versionen;
    }

    /**
     * Holt alle Feedbacks zu einer Version
     */
    public static List<Feedback> getFeedbacksForVersion(int versionId) throws Exception {
        List<Feedback> feedbacks = new ArrayList<>();

        String sql = """
            SELECT f.feedback_id, f.version_id, f.feedback_text, f.datum, 
                   s.Vorname, s.Nachname, s.rolle
            FROM feedback f
            JOIN studentendb s ON f.verfasser_mnr = s.MNR
            WHERE f.version_id = ?
            ORDER BY f.datum ASC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, versionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String verfasser = rs.getString("Vorname") + " " + rs.getString("Nachname") + 
                                  " (" + rs.getString("rolle") + ")";
                feedbacks.add(new Feedback(
                    rs.getInt("feedback_id"),
                    rs.getInt("version_id"),
                    rs.getString("feedback_text"),
                    rs.getString("datum"),
                    verfasser
                ));
            }
        }

        return feedbacks;
    }

    /**
     * Hilfsmethode: Holt Student-MNR aus Version
     */
    private static int getStudentMnrFromVersion(Connection conn, int versionId) throws Exception {
        String sql = "SELECT student_mnr FROM versionen WHERE version_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, versionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("student_mnr");
            }
        }
        return -1;
    }

    /**
     * Hilfsmethode: Holt Betreuer-MNR aus Version
     */
    private static int getBetreuerMnrFromVersion(Connection conn, int versionId) throws Exception {
        String sql = "SELECT betreuer_mnr FROM versionen WHERE version_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, versionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("betreuer_mnr");
            }
        }
        return -1;
    }
}
