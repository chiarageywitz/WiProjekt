package GUIKlassen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import Datenbank.UserDAO; 
import Datenbank.UserLoginResult;
import Util.UIColors;
import Util.UIImageLoader;


public class LoginFenster extends BaseFrame {

    public LoginFenster() {
        super("Login - Hochschule");

        getContentPane().setBackground(UIColors.BACKGROUND);

     // =======================
     // Hochschul-Logo oben mittig (STABIL)
     // =======================

        int logoWidth = 200;
        int logoX = (BaseFrame.WIDTH - logoWidth) / 2;
        add(UIImageLoader.createLogoLabel(logoX, 60, logoWidth, 100));
        
     // =========================
     // FENSTERBREITE FESTLEGEN
     // =========================
     int windowWidth = BaseFrame.WIDTH;

        
     // =========================
        // FORMULAR-AUSRICHTUNG (HIER!)
        // =========================
        int formWidth = 300;
        int labelWidth = 100;
        int fieldWidth = 180;
        int formHeight = 220; // Höhe des gesamten Login-Formulars

        int startX = (windowWidth - formWidth) / 2;
        int labelX = startX;
        int fieldX = startX + labelWidth + 10;

        
        // =======================
        // E-Mail
        // =======================
        JLabel userLabel = new JLabel("E-Mail:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        int y = (BaseFrame.HEIGHT - formHeight) / 2;
        userLabel.setBounds(labelX, y, labelWidth, 25);
        add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(fieldX, y, fieldWidth, 25);
        add(userField);

        // =======================
        // Passwort
        // =======================
        JLabel passLabel = new JLabel("Passwort:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        y += 40;
        passLabel.setBounds(labelX, y, labelWidth, 25);
        add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(fieldX, y, fieldWidth, 25);
        add(passField);

        // =======================
        // Einloggen Button
        // =======================
        JButton loginButton = new JButton("Einloggen");
        y += 60;

        loginButton.setBounds(
            startX + (formWidth - 140) / 2,
            y,
            140,
            35
        );
      
        loginButton.setBackground(UIColors.PRIMARY_BLUE);
        loginButton.setForeground(UIColors.TEXT_WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        add(loginButton);

        // =======================
        // Neuer Benutzer Label
        // =======================
        JLabel newUserLabel = new JLabel("<HTML><U>Neuer Benutzer?</U></HTML>");
        newUserLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        newUserLabel.setForeground(UIColors.PRIMARY_BLUE);
        y += 45;

        newUserLabel.setBounds(
        	    startX + (formWidth - newUserLabel.getPreferredSize().width) / 2,
        	    y,
        	    newUserLabel.getPreferredSize().width,
        	    25
        	);
        newUserLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(newUserLabel);

        // =======================
        // ActionListener Login
        // =======================
  
        loginButton.addActionListener(e -> {
            String email = userField.getText();
            String pass = new String(passField.getPassword());

            try {
                UserLoginResult result = UserDAO.login(email, pass);

                if (result == null) {
                    JOptionPane.showMessageDialog(this, "E-Mail oder Passwort falsch!");
                    return;
                }

                String rolle = result.getRolle();
                int mnr = result.getMnr();

                switch (rolle.toLowerCase()) {
                    case "student" -> new DashboardStudent(mnr);
                    case "betreuer" -> new DashboardBetreuer();
                    case "dekan" -> new DashboardStudiendekan();
                    default -> JOptionPane.showMessageDialog(this, "Unbekannte Rolle!");
                }

                dispose(); // Login-Fenster schließen

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Fehler beim Login!");
            }
        });
        
        // =======================
        // Neuer Benutzer Action
        // =======================
        
        newUserLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();                 // ✅ LoginFenster schließen
                new NeuerBenutzerFenster(); // ✅ Registrierung öffnen
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFenster::new);
    }
}