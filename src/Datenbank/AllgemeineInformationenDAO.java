/**
 * Package fuer den Zugriff auf die Datenbank.
 * Enthaelt DAO Klassen fuer die Persistenz von Daten.
 */
package Datenbank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO Klasse fuer allgemeine Informationen zu einer Bachelorarbeit. Diese
 * Klasse speichert Angaben wie Thema, Unternehmen, Betreuer und NDA
 * Informationen und erstellt Benachrichtigungen fuer Betreuer.
 */
public class AllgemeineInformationenDAO {

	/**
	 * Speichert allgemeine Informationen zu einer Bachelorarbeit. Falls bereits
	 * Daten fuer die Matrikelnummer existieren, werden diese aktualisiert.
	 * Zusaetzlich wird eine Benachrichtigung fuer einen bestimmten Betreuer oder
	 * alle Betreuer erstellt.
	 *
	 * @param mnr                 Matrikelnummer des Studierenden
	 * @param thema               Thema der Bachelorarbeit
	 * @param unternehmen         Unternehmen der Bachelorarbeit
	 * @param zeitraum            Zeitraum der Arbeit
	 * @param betreuerHft         Betreuer der Hochschule
	 * @param betreuerUnternehmen Betreuer im Unternehmen
	 * @param ndaNoetig           Gibt an ob eine NDA benoetigt wird
	 * @param ndaPfad             Dateipfad der NDA
	 * @param betreuerMnr         Matrikelnummer des Betreuers oder null
	 * @throws Exception Fehler beim Datenbankzugriff
	 */
	public static void speichern(int mnr, String thema, String unternehmen, String zeitraum, String betreuerHft,
			String betreuerUnternehmen, boolean ndaNoetig, String ndaPfad, Integer betreuerMnr) throws Exception {

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

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, mnr);
			ps.setString(2, thema);
			ps.setString(3, unternehmen);
			ps.setString(4, zeitraum);
			ps.setString(5, betreuerHft);
			ps.setString(6, betreuerUnternehmen);
			ps.setBoolean(7, ndaNoetig);
			ps.setString(8, ndaPfad);

			ps.executeUpdate();

			erstelleBenachrichtigung(conn, mnr, betreuerMnr);
		}
	}

	/**
	 * Alte Methode zur Speicherung allgemeiner Informationen. Diese Methode dient
	 * der Kompatibilitaet und ruft die neue Methode auf.
	 *
	 * @param mnr                 Matrikelnummer des Studierenden
	 * @param thema               Thema der Bachelorarbeit
	 * @param unternehmen         Unternehmen der Bachelorarbeit
	 * @param zeitraum            Zeitraum der Arbeit
	 * @param betreuerHft         Betreuer der Hochschule
	 * @param betreuerUnternehmen Betreuer im Unternehmen
	 * @param ndaNoetig           Gibt an ob eine NDA benoetigt wird
	 * @param ndaPfad             Dateipfad der NDA
	 * @throws Exception Fehler beim Datenbankzugriff
	 */
	public static void speichern(int mnr, String thema, String unternehmen, String zeitraum, String betreuerHft,
			String betreuerUnternehmen, boolean ndaNoetig, String ndaPfad) throws Exception {
		speichern(mnr, thema, unternehmen, zeitraum, betreuerHft, betreuerUnternehmen, ndaNoetig, ndaPfad, null);
	}

	/**
	 * Erstellt eine Benachrichtigung fuer einen bestimmten Betreuer oder fuer alle
	 * Betreuer.
	 *
	 * @param conn        Datenbankverbindung
	 * @param studentMnr  Matrikelnummer des Studierenden
	 * @param betreuerMnr Matrikelnummer des Betreuers oder null
	 * @throws Exception Fehler beim Datenbankzugriff
	 */
	private static void erstelleBenachrichtigung(Connection conn, int studentMnr, Integer betreuerMnr)
			throws Exception {
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

		String benachrichtigungstext = "Neuer Antrag von " + studentName
				+ (thema != null && !thema.isEmpty() ? " zum Thema: " + thema : "");

		if (betreuerMnr != null) {
			String sqlNotif = """
					    INSERT INTO benachrichtigungen
					    (mnr, text, datum, empfaenger_rolle, sender_mnr, empfanger_mnr)
					    VALUES (?, ?, CURDATE(), 'betreuer', ?, ?)
					""";

			try (PreparedStatement psNotif = conn.prepareStatement(sqlNotif)) {
				psNotif.setInt(1, betreuerMnr);
				psNotif.setString(2, benachrichtigungstext);
				psNotif.setInt(3, studentMnr);
				psNotif.setInt(4, betreuerMnr);
				psNotif.executeUpdate();
			}
		} else {
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
