/**
 * Package fuer den Zugriff auf die Datenbank.
 * Enthaelt DAO Klassen fuer Studenten.
 */
package Datenbank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Klasse fuer den Zugriff auf Studentendaten. Diese Klasse ermoeglicht die
 * Suche nach Studenten sowie das Laden einzelner oder aller Studenten.
 */
public class StudentDAO {

	/**
	 * Datenklasse fuer Studenteninformationen. Enthaelt Matrikelnummer, Name und
	 * Thema.
	 */
	public static class StudentInfo {
		public int mnr;
		public String name;
		public String thema;

		/**
		 * Erstellt ein StudentInfo Objekt.
		 *
		 * @param mnr   Matrikelnummer des Studenten
		 * @param name  Name des Studenten
		 * @param thema Thema der Arbeit
		 */
		public StudentInfo(int mnr, String name, String thema) {
			this.mnr = mnr;
			this.name = name;
			this.thema = thema;
		}

		/**
		 * Gibt eine lesbare Darstellung des Studenten zurueck.
		 *
		 * @return Name und Matrikelnummer als Text
		 */
		@Override
		public String toString() {
			return name + " (" + mnr + ")";
		}
	}

	/**
	 * Sucht Studenten anhand des Vornamens.
	 *
	 * @param name Name oder Namensbestandteil
	 * @return Liste gefundener Studenten
	 * @throws Exception Fehler beim Datenbankzugriff
	 */
	public static List<StudentInfo> sucheStudenten(String name) throws Exception {
		List<StudentInfo> result = new ArrayList<>();

		String sql = """
				    SELECT s.MNR, s.Vorname, a.thema
				    FROM studentendb s
				    LEFT JOIN antraege a ON s.MNR = a.student_mnr
				    WHERE s.Vorname LIKE ?
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, "%" + name + "%");

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result.add(new StudentInfo(rs.getInt("MNR"), rs.getString("Vorname"), rs.getString("thema")));
			}
		}

		return result;
	}

	/**
	 * Liefert die Informationen eines einzelnen Studenten.
	 *
	 * @param mnr Matrikelnummer des Studenten
	 * @return StudentInfo Objekt oder null falls nicht gefunden
	 * @throws Exception Fehler beim Datenbankzugriff
	 */
	public static StudentInfo getStudentInfo(int mnr) throws Exception {
		String sql = """
				    SELECT s.MNR, s.Vorname, a.thema
				    FROM studentendb s
				    LEFT JOIN antraege a ON s.MNR = a.student_mnr
				    WHERE s.MNR = ?
				    LIMIT 1
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, mnr);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return new StudentInfo(rs.getInt("MNR"), rs.getString("Vorname"), rs.getString("thema"));
			}
			return null;
		}
	}

	/**
	 * Liefert alle Studenten mit der Rolle student.
	 *
	 * @return Liste aller Studenten
	 * @throws Exception Fehler beim Datenbankzugriff
	 */
	public static List<StudentInfo> getAllStudents() throws Exception {
		List<StudentInfo> result = new ArrayList<>();

		String sql = """
				    SELECT s.MNR, s.Vorname, a.thema
				    FROM studentendb s
				    LEFT JOIN antraege a ON s.MNR = a.student_mnr
				    WHERE s.rolle = 'student'
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result.add(new StudentInfo(rs.getInt("MNR"), rs.getString("Vorname"), rs.getString("thema")));
			}
		}

		return result;
	}
}
