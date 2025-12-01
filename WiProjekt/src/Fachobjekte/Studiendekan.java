package Fachobjekte;

public class Studiendekan {
	private String benutzername;
	private String passwort;

	public Studiendekan(String benutzername, String passwort, String matrikelnummer) {
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
