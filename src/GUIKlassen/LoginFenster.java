package GUIKlassen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import Datenbank.UserDAO; 
import Datenbank.UserLoginResult;
import Util.UIColors;
import Util.UIImageLoader;
import Util.LoginSession;

public class LoginFenster extends BaseFrame {

    // Textfelder als Instanzvariablen, damit wir darauf zugreifen können
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;

    public LoginFenster() {
        super("Login - Hochschule");

        getContentPane().setBackground(UIColors.BACKGROUND);

        // =======================
        // Hochschul-Logo oben mittig
        // =======================
        int logoWidth = 200;
        int logoX = (BaseFrame.WIDTH - logoWidth) / 2;
        add(UIImageLoader.createLogoLabel(logoX, 60, logoWidth, 100));
        
        // =========================
        // FENSTERBREITE FESTLEGEN
        // =========================
        int windowWidth = BaseFrame.WIDTH;

        // =========================
        // FORMULAR-AUSRICHTUNG
        // =========================
        int formWidth = 300;
        int labelWidth = 100;
        int fieldWidth = 180;
        int formHeight = 220;

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

        userField = new JTextField();
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

        passField = new JPasswordField();
        passField.setBounds(fieldX, y, fieldWidth, 25);
        add(passField);

        // =======================
        // Einloggen Button
        // =======================
        loginButton = new JButton("Einloggen");
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
        loginButton.addActionListener(e -> performLogin());

        // Auch Enter-Taste in Passwort-Feld soll Login auslösen
        passField.addActionListener(e -> performLogin());
        
        // Automatisches Vorausfüllen wenn Daten vorhanden
        autoFillLoginData();

        // =======================
        // Neuer Benutzer Action
        // =======================
        newUserLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new NeuerBenutzerFenster();
            }
        });

        setVisible(true);
    }
    
    // Methode zum automatischen Vorausfüllen
    private void autoFillLoginData() {
        String lastEmail = NeuerBenutzerFenster.getLastCreatedEmail();
        String lastPassword = NeuerBenutzerFenster.getLastCreatedPassword();
        
        if (!lastEmail.isEmpty() && !lastPassword.isEmpty()) {
            userField.setText(lastEmail);
            passField.setText(lastPassword);
            
            // Optional: Fokus auf Login-Button setzen
            loginButton.requestFocus();
            
            // Optional: Nach 100ms automatisch einloggen
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(100);
                    performLogin();
                } catch (InterruptedException e) {
                    // Ignorieren
                }
            });
            
            // Daten zurücksetzen
            NeuerBenutzerFenster.clearLastCreatedData();
        }
    }
    
    // Login-Logik in separate Methode ausgelagert
    private void performLogin() {
        String email = userField.getText();
        String pass = new String(passField.getPassword());

        // Validierung
        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Bitte E-Mail und Passwort eingeben!", 
                "Eingabe fehlt", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UserLoginResult result = UserDAO.login(email, pass);

            if (result == null) {
                JOptionPane.showMessageDialog(this, 
                    "E-Mail oder Passwort falsch!", 
                    "Login fehlgeschlagen", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String rolle = result.getRolle();
            int mnr = result.getMnr();

            // NEU: LoginSession setzen
            LoginSession.setCurrentUser(mnr, rolle, email);
            
            // Erfolgreiche Login-Meldung
            JOptionPane.showMessageDialog(this,
                "<html><center>Login erfolgreich!<br>" +
                "Willkommen, " + email + "<br>" +
                "Rolle: " + rolle + "</center></html>",
                "Willkommen",
                JOptionPane.INFORMATION_MESSAGE);

            // Weiterleitung basierend auf Rolle
            switch (rolle.toLowerCase()) {
                case "student" -> {
                    new DashboardStudent(mnr);
                    dispose();
                }
                case "betreuer" -> {
                    new StudentenSucheView();
                    dispose();
                }
                case "dekan" -> {
                    new DashboardStudiendekan(null);
                    dispose();
                }
                default -> {
                    JOptionPane.showMessageDialog(this, 
                        "Unbekannte Rolle: " + rolle, 
                        "Fehler", 
                        JOptionPane.ERROR_MESSAGE);
                    LoginSession.clear(); // Session zurücksetzen bei Fehler
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Fehler beim Login: " + ex.getMessage(), 
                "Login-Fehler", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // NEU: Sicherstellen, dass Session beim Start geleert ist
        LoginSession.clear();
        
        SwingUtilities.invokeLater(() -> {
            LoginFenster login = new LoginFenster();
            login.setVisible(true);
        });
    }
}