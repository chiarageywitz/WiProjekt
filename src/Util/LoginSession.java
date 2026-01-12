package Util;

public class LoginSession {
    private static int currentMnr = 0;
    private static String currentRole = "";
    private static String currentEmail = "";
    
    public static void setCurrentUser(int mnr, String role) {
        currentMnr = mnr;
        currentRole = role;
    }
    
    public static void setCurrentUser(int mnr, String role, String email) {
        currentMnr = mnr;
        currentRole = role;
        currentEmail = email;
    }
    
    public static int getCurrentMnr() {
        return currentMnr;
    }
    
    public static String getCurrentRole() {
        return currentRole;
    }
    
    public static String getCurrentEmail() {
        return currentEmail;
    }
    
    public static void clear() {
        currentMnr = 0;
        currentRole = "";
        currentEmail = "";
    }
    
    public static boolean isLoggedIn() {
        return currentMnr > 0;
    }
}