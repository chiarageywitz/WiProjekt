package Datenbank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO-Klasse für Anträge zur Bachelorarbeit.
 * Verwaltet Anträge, deren Status und Genehmigungsworkflow.
 */
public class AntragDAO {

    /**
     * Antrag-Datenklasse
     */
    public static class Antrag {
        public int antragId;
        public int studentMnr;
        public String studentName;
        public String thema;
        public String unternehmen;
        public Integer betreuerMnr;
        public String betreuerName;
        public String status; // "offen", "betreuer_genehmigt", "betreuer_abgelehnt", "dekan_genehmigt", "dekan_abgelehnt"
        public String datum;
        public String ablehnungsgrund;

        public Antrag(int antragId, int studentMnr, String studentName, String thema, 
                     String unternehmen, Integer betreuerMnr, String betreuerName, 
                     String status, String datum, String ablehnungsgrund) {
            this.antragId = antragId;
            this.studentMnr = studentMnr;
            this.studentName = studentName;
            this.thema = thema;
            this.unternehmen = unternehmen;
            this.betreuerMnr = betreuerMnr;
            this.betreuerName = betreuerName;
            this.status = status;
            this.datum = datum;
            this.ablehnungsgrund = ablehnungsgrund;
        }
    }

    /**
     * Erstellt einen neuen Antrag
     */
    public static void antragErstellen(int studentMnr, String thema, String unternehmen, 
                                       String zeitraum, String betreuerHft, String betreuerUnternehmen, 
                                       boolean ndaNoetig, String ndaPfad, Integer betreuerMnr) throws Exception {
        String sql = """
            INSERT INTO antraege 
            (student_mnr, thema, unternehmen, zeitraum, betreuer_hft, betreuer_unternehmen, 
             nda_noetig, nda_dateipfad, betreuer_mnr, status, datum)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'offen', CURDATE())
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentMnr);
            ps.setString(2, thema);
            ps.setString(3, unternehmen);
            ps.setString(4, zeitraum);
            ps.setString(5, betreuerHft);
            ps.setString(6, betreuerUnternehmen);
            ps.setBoolean(7, ndaNoetig);
            ps.setString(8, ndaPfad);
            if (betreuerMnr != null) {
                ps.setInt(9, betreuerMnr);
            } else {
                ps.setNull(9, java.sql.Types.INTEGER);
            }

            ps.executeUpdate();

            // Benachrichtigung an Betreuer erstellen
            BenachrichtigungDAO.erstellen(
                betreuerMnr != null ? betreuerMnr : null,
                "Neuer Antrag von Student (MNR: " + studentMnr + ") zum Thema: " + thema,
                "betreuer",
                studentMnr,
                betreuerMnr
            );
        }
    }

    /**
     * Holt alle offenen Anträge für einen Betreuer
     */
    public static List<Antrag> getAntraegeForBetreuer(int betreuerMnr) throws Exception {
        List<Antrag> antraege = new ArrayList<>();

        String sql = """
            SELECT a.antrag_id, a.student_mnr, s.Vorname, s.Nachname, a.thema, 
                   a.unternehmen, a.betreuer_mnr, a.status, a.datum, a.ablehnungsgrund
            FROM antraege a
            JOIN studentendb s ON a.student_mnr = s.MNR
            WHERE a.betreuer_mnr = ? OR a.betreuer_mnr IS NULL
            ORDER BY a.datum DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, betreuerMnr);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                antraege.add(new Antrag(
                    rs.getInt("antrag_id"),
                    rs.getInt("student_mnr"),
                    rs.getString("Vorname") + " " + rs.getString("Nachname"),
                    rs.getString("thema"),
                    rs.getString("unternehmen"),
                    rs.getInt("betreuer_mnr"),
                    null,
                    rs.getString("status"),
                    rs.getString("datum"),
                    rs.getString("ablehnungsgrund")
                ));
            }
        }

        return antraege;
    }

    /**
     * Holt alle Anträge, die vom Betreuer genehmigt wurden (für Studiendekan)
     */
    public static List<Antrag> getAntraegeForStudiendekan() throws Exception {
        List<Antrag> antraege = new ArrayList<>();

        String sql = """
            SELECT a.antrag_id, a.student_mnr, s.Vorname, s.Nachname, a.thema, 
                   a.unternehmen, a.betreuer_mnr, b.Vorname as BVorname, b.Nachname as BNachname,
                   a.status, a.datum, a.ablehnungsgrund
            FROM antraege a
            JOIN studentendb s ON a.student_mnr = s.MNR
            LEFT JOIN studentendb b ON a.betreuer_mnr = b.MNR
            WHERE a.status = 'betreuer_genehmigt'
            ORDER BY a.datum DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                antraege.add(new Antrag(
                    rs.getInt("antrag_id"),
                    rs.getInt("student_mnr"),
                    rs.getString("Vorname") + " " + rs.getString("Nachname"),
                    rs.getString("thema"),
                    rs.getString("unternehmen"),
                    rs.getInt("betreuer_mnr"),
                    rs.getString("BVorname") + " " + rs.getString("BNachname"),
                    rs.getString("status"),
                    rs.getString("datum"),
                    rs.getString("ablehnungsgrund")
                ));
            }
        }

        return antraege;
    }

    /**
     * Betreuer genehmigt Antrag
     */
    public static void betreuerGenehmigen(int antragId, int betreuerMnr) throws Exception {
        String sql = """
            UPDATE antraege 
            SET status = 'betreuer_genehmigt', betreuer_mnr = ?
            WHERE antrag_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, betreuerMnr);
            ps.setInt(2, antragId);
            ps.executeUpdate();

            // Student benachrichtigen
            int studentMnr = getStudentMnrFromAntrag(conn, antragId);
            BenachrichtigungDAO.erstellen(
                studentMnr,
                "Ihr Antrag wurde vom Betreuer genehmigt und an den Studiendekan weitergeleitet.",
                "student",
                betreuerMnr,
                studentMnr
            );

            // Studiendekan benachrichtigen
            BenachrichtigungDAO.erstellen(
                null,
                "Neuer Antrag zur Genehmigung (Student MNR: " + studentMnr + ")",
                "studiendekan",
                betreuerMnr,
                null
            );
        }
    }

    /**
     * Betreuer lehnt Antrag ab
     */
    public static void betreuerAblehnen(int antragId, String grund) throws Exception {
        String sql = """
            UPDATE antraege 
            SET status = 'betreuer_abgelehnt', ablehnungsgrund = ?
            WHERE antrag_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, grund);
            ps.setInt(2, antragId);
            ps.executeUpdate();

            // Student benachrichtigen
            int studentMnr = getStudentMnrFromAntrag(conn, antragId);
            BenachrichtigungDAO.erstellen(
                studentMnr,
                "Ihr Antrag wurde abgelehnt. Grund: " + grund + ". Bitte reichen Sie einen neuen Antrag ein.",
                "student",
                null,
                studentMnr
            );
        }
    }

    /**
     * Studiendekan genehmigt Antrag
     */
    public static void dekanGenehmigen(int antragId) throws Exception {
        String sql = """
            UPDATE antraege 
            SET status = 'dekan_genehmigt'
            WHERE antrag_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, antragId);
            ps.executeUpdate();

            // Student benachrichtigen - kann sich jetzt anmelden
            int studentMnr = getStudentMnrFromAntrag(conn, antragId);
            BenachrichtigungDAO.erstellen(
                studentMnr,
                "Ihr Antrag wurde vom Studiendekan genehmigt. Sie können sich jetzt für die Bachelorarbeit anmelden.",
                "student",
                null,
                studentMnr
            );

            // Daten in allgemeine_informationen übertragen
            uebertrageInAllgemeineInfo(conn, antragId);
        }
    }

    /**
     * Studiendekan lehnt Antrag ab
     */
    public static void dekanAblehnen(int antragId, String grund) throws Exception {
        String sql = """
            UPDATE antraege 
            SET status = 'dekan_abgelehnt', ablehnungsgrund = ?
            WHERE antrag_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, grund);
            ps.setInt(2, antragId);
            ps.executeUpdate();

            // Student und Betreuer benachrichtigen
            int studentMnr = getStudentMnrFromAntrag(conn, antragId);
            BenachrichtigungDAO.erstellen(
                studentMnr,
                "Ihr Antrag wurde vom Studiendekan abgelehnt. Grund: " + grund + ". Bitte reichen Sie einen überarbeiteten Antrag ein.",
                "student",
                null,
                studentMnr
            );
        }
    }

    /**
     * Hilfsmethode: Holt Student-MNR aus Antrag
     */
    private static int getStudentMnrFromAntrag(Connection conn, int antragId) throws Exception {
        String sql = "SELECT student_mnr FROM antraege WHERE antrag_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, antragId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("student_mnr");
            }
        }
        return -1;
    }

    /**
     * Überträgt genehmigte Antragsdaten in allgemeine_informationen
     */
    private static void uebertrageInAllgemeineInfo(Connection conn, int antragId) throws Exception {
        String selectSql = """
            SELECT student_mnr, thema, unternehmen, zeitraum, betreuer_hft, 
                   betreuer_unternehmen, nda_noetig, nda_dateipfad, betreuer_mnr
            FROM antraege WHERE antrag_id = ?
        """;

        String insertSql = """
            INSERT INTO allgemeine_informationen 
            (mnr, thema, unternehmen, zeitraum, betreuer_hft, betreuer_unternehmen, 
             nda_noetig, nda_dateipfad, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'genehmigt')
            ON DUPLICATE KEY UPDATE
                thema = VALUES(thema),
                unternehmen = VALUES(unternehmen),
                zeitraum = VALUES(zeitraum),
                betreuer_hft = VALUES(betreuer_hft),
                betreuer_unternehmen = VALUES(betreuer_unternehmen),
                nda_noetig = VALUES(nda_noetig),
                nda_dateipfad = VALUES(nda_dateipfad),
                status = 'genehmigt'
        """;

        try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
            psSelect.setInt(1, antragId);
            ResultSet rs = psSelect.executeQuery();

            if (rs.next()) {
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                    psInsert.setInt(1, rs.getInt("student_mnr"));
                    psInsert.setString(2, rs.getString("thema"));
                    psInsert.setString(3, rs.getString("unternehmen"));
                    psInsert.setString(4, rs.getString("zeitraum"));
                    psInsert.setString(5, rs.getString("betreuer_hft"));
                    psInsert.setString(6, rs.getString("betreuer_unternehmen"));
                    psInsert.setBoolean(7, rs.getBoolean("nda_noetig"));
                    psInsert.setString(8, rs.getString("nda_dateipfad"));
                    psInsert.executeUpdate();
                }
            }
        }
    }

    /**
     * Prüft, ob Student einen genehmigten Antrag hat
     */
    public static boolean hatGenehmigtenAntrag(int studentMnr) throws Exception {
        String sql = """
            SELECT COUNT(*) as count FROM antraege 
            WHERE student_mnr = ? AND status = 'dekan_genehmigt'
        """;

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
