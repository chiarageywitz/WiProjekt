/**
 * Package fuer den Zugriff auf die Datenbank.
 * Enthaelt Klassen zur Datenuebertragung.
 */
package Datenbank;

/**
 * Diese Klasse repraesentiert das Ergebnis eines erfolgreichen Benutzer Logins.
 * Sie enthaelt die Matrikelnummer und die Rolle des Benutzers.
 */
public class UserLoginResult {

	private final int mnr;
	private final String rolle;

	/**
	 * Erstellt ein neues UserLoginResult Objekt.
	 *
	 * @param mnr   Matrikelnummer des Benutzers
	 * @param rolle Rolle des Benutzers
	 */
	public UserLoginResult(int mnr, String rolle) {
		this.mnr = mnr;
		this.rolle = rolle;
	}

	/**
	 * Gibt die Matrikelnummer zurueck.
	 *
	 * @return Matrikelnummer des Benutzers
	 */
	public int getMnr() {
		return mnr;
	}

	/**
	 * Gibt die Rolle des Benutzers zurueck.
	 *
	 * @return Rolle des Benutzers
	 */
	public String getRolle() {
		return rolle;
	}
}
