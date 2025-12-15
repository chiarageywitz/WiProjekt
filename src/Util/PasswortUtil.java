package Util;

import java.security.MessageDigest;

public class PasswortUtil {
	
	// verhindert Instanziierung
    private PasswortUtil() {}

    // Hash-Funktion mit SHA-256
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Hashen des Passworts", e);
        }
    }

    // Vergleichsfunktion
    public static boolean pruefePasswort(String eingegebenesPasswort, String gespeicherterHash) {
        String eingegebenerHash = hash(eingegebenesPasswort);
        return gespeicherterHash.equals(eingegebenerHash);
    }
}
