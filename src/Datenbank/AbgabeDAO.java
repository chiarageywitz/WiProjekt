/**
 * Package fuer den Zugriff auf die Datenbank.
 * Enthaelt Klassen der DAO Schicht.
 */
package Datenbank;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * DAO Klasse fuer die Abgabe von Bachelorarbeiten. Diese Klasse kapselt den
 * Zugriff auf die Tabelle abgabe_bachelorarbeit.
 */
public class AbgabeDAO {

	/**
	 * Speichert den Dateipfad einer Bachelorarbeit zu einer bestimmten
	 * Matrikelnummer. Falls bereits ein Eintrag existiert, wird dieser
	 * aktualisiert.
	 *
	 * @param mnr       Matrikelnummer des Studierenden
	 * @param dateipfad Pfad zur abgegebenen Datei
	 * @throws Exception Fehler beim Datenbankzugriff
	 */
	public static void speichern(int mnr, String dateipfad) throws Exception {
		String sql = """
				    INSERT INTO abgabe_bachelorarbeit (mnr, dateipfad)
				    VALUES (?, ?)
				    ON DUPLICATE KEY UPDATE
				        dateipfad = VALUES(dateipfad)
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, mnr);
			ps.setString(2, dateipfad);
			ps.executeUpdate();
		}
	}
}
