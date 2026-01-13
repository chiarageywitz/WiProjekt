/**
 * Package fuer den Zugriff auf die Datenbank.
 * Enthaelt Hilfsklassen fuer Datenbankverbindungen.
 */
package Datenbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Diese Klasse stellt eine Verbindung zur Datenbank her. Sie kapselt die
 * Zugangsdaten und den Aufbau der JDBC Verbindung.
 */
public class DBConnection {

	private static final String URL = "jdbc:mysql://3.69.96.96:80/db1";
	private static final String USER = "db1";
	private static final String PASSWORD = "!db1.wip25?SS1";

	/**
	 * Erstellt und liefert eine neue Datenbankverbindung.
	 *
	 * @return aktive Datenbankverbindung
	 * @throws SQLException Fehler beim Aufbau der Verbindung
	 */
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}
