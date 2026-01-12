package Fachobjekte;

/**
 * Repräsentiert einen Betreuer mit Benutzername und Passwort
 */
public class Betreuer {

	private String benutzername;
	private String passwort;

	/**
	 * Konstruktor Erstellt einen Betreuer mit Benutzername und Passwort
	 *
	 * @param benutzername Name des Betreuers
	 * @param passwort     Passwort des Betreuers
	 */
	public Betreuer(String benutzername, String passwort) {
		this.benutzername = benutzername;
		this.passwort = passwort;
	}

	/**
	 * Gibt den Benutzernamen zurück
	 *
	 * @return Benutzername des Betreuers
	 */
	public String getBenutzername() {
		return benutzername;
	}

	/**
	 * Setzt den Benutzernamen
	 *
	 * @param benutzername Neuer Benutzername
	 */
	public void setBenutzername(String benutzername) {
		this.benutzername = benutzername;
	}

	/**
	 * Gibt das Passwort zurück
	 *
	 * @return Passwort des Betreuers
	 */
	public String getPasswort() {
		return passwort;
	}

	/**
	 * Setzt das Passwort
	 *
	 * @param passwort Neues Passwort
	 */
	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}
}
