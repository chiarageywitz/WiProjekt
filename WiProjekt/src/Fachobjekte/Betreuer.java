package Fachobjekte;

public class Betreuer {

	private String benutzername;
	private String passwort;

	public Betreuer(String benutzername, String passwort, String matrikelnummer) {
		this.benutzername = benutzername;
		this.passwort = passwort;
	}

	public String getBenutzername() {
		return benutzername;
	}

	public void setBenutzername(String benutzername) {
		this.benutzername = benutzername;
	}

	public String getPasswort() {
		return passwort;
	}

	public void setPasswort(String passwort) {
		this.passwort = passwort;

	}
}
