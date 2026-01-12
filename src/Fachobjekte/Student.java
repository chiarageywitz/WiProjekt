package Fachobjekte;

/**
 * Repräsentiert einen Studenten mit Benutzername, Passwort und Matrikelnummer.
 */
public class Student {

    private String benutzername;
    private String passwort;
    private String matrikelnummer; // Beispiel zusätzliches Attribut

    /**
     * Konstruktor zur Initialisierung eines Studenten.
     *
     * @param benutzername Benutzername des Studenten
     * @param passwort     Passwort des Studenten
     * @param matrikelnummer Matrikelnummer des Studenten
     */
    public Student(String benutzername, String passwort, String matrikelnummer) {
        this.benutzername = benutzername;
        this.passwort = passwort;
        this.matrikelnummer = matrikelnummer;
    }

    /**
     * Gibt den Benutzernamen des Studenten zurück.
     *
     * @return Benutzername
     */
    public String getBenutzername() {
        return benutzername;
    }

    /**
     * Setzt den Benutzernamen des Studenten.
     *
     * @param benutzername Neuer Benutzername
     */
    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }

    /**
     * Gibt das Passwort des Studenten zurück.
     *
     * @return Passwort
     */
    public String getPasswort() {
        return passwort;
    }

    /**
     * Setzt das Passwort des Studenten.
     *
     * @param passwort Neues Passwort
     */
    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    /**
     * Gibt die Matrikelnummer des Studenten zurück.
     *
     * @return Matrikelnummer
     */
    public String getMatrikelnummer() {
        return matrikelnummer;
    }

    /**
     * Setzt die Matrikelnummer des Studenten.
     *
     * @param matrikelnummer Neue Matrikelnummer
     */
    public void setMatrikelnummer(String matrikelnummer) {
        this.matrikelnummer = matrikelnummer;
    }
}
