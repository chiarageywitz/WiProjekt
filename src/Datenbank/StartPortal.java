package Datenbank;

import javax.swing.SwingUtilities;

public class StartPortal {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFenster login = new LoginFenster();
            login.setVisible(true);
        });
    }
}