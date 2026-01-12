package Fachobjekte;

/**
 * Repräsentiert einen Studiendekan mit Benutzername und Passwort
 */
public class Studiendekan {

    private String benutzername;
    private String passwort;

    /**
     * Konstruktor
     * Erstellt einen Studiendekan mit Benutzername und Passwort
     *
     * @param benutzername Name des Benutzers
     * @param passwort Passwort des Benutzers
     */
    public Studiendekan(String benutzername, String passwort) {
        this.benutzername = benutzername;
        this.passwort = passwort;
    }

    /**
     * Gibt den Benutzernamen zurück
     *
     * @return Benutzername des Studiendekans
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
     * @return Passwort des Studiendekans
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
