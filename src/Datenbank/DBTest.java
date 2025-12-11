package Datenbank;

public class DBTest {

    public static void main(String[] args) {

        try {
            if (DBConnection.getConnection() != null) {
                System.out.println("✅ Verbindung zur Datenbank erfolgreich!");
            } else {
                System.out.println("❌ Verbindung fehlgeschlagen!");
            }
        } catch (Exception e) {
            System.out.println("❌ Fehler bei der Datenbankverbindung:");
            e.printStackTrace();
        }

    }
}