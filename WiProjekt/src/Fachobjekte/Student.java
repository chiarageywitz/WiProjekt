public class Student {

    private String benutzername;
    private String passwort;
    private String matrikelnummer; // Beispiel zusätzliches Attribut

    public Student(String benutzername, String passwort, String matrikelnummer) {
        this.benutzername = benutzername;
        this.passwort = passwort;
        this.matrikelnummer = matrikelnummer;
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

    public String getMatrikelnummer() {
        return matrikelnummer;
    }

    public void setMatrikelnummer(String matrikelnummer) {
        this.matrikelnummer = matrikelnummer;
    }
}