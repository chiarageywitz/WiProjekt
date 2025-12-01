package GUIKlassen;

import javax.swing.*;
import java.awt.*;

public class LoginFenster extends JFrame {

    public LoginFenster() {
        setTitle("Login - Hochschule");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(null); // wir positionieren alles manuell, ähnlich DashboardStudent

        // =======================
        // Hintergrundfarbe
        // =======================
        getContentPane().setBackground(new Color(240, 240, 240)); // helles Grau

        // =======================
        // Hochschul-Logo oben mittig
        // =======================
        ImageIcon logo = new ImageIcon("hochschule.png"); // Pfad zu eurem Bild
        JLabel logoLabel = new JLabel(logo);
        logoLabel.setBounds(150, 20, 200, 100); // Logo-Größe und Position
        add(logoLabel);

        // =======================
        // Benutzername
        // =======================
        JLabel userLabel = new JLabel("Benutzername:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setBounds(100, 140, 120, 25);
        add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(220, 140, 180, 25);
        add(userField);

        // =======================
        // Passwort
        // =======================
        JLabel passLabel = new JLabel("Passwort:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setBounds(100, 180, 120, 25);
        add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(220, 180, 180, 25);
        add(passField);

        // =======================
        // Sign In Button
        // =======================
        JButton loginButton = new JButton("Einloggen");
        loginButton.setBounds(180, 230, 140, 35);
        loginButton.setBackground(new Color(0, 45, 150)); // Dunkelblau
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        add(loginButton);

        // =======================
        // New User Label unten
        // =======================
        JLabel newUserLabel = new JLabel("<HTML><U>Neuer Benutzer?</U></HTML>");
        newUserLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        newUserLabel.setForeground(new Color(0, 45, 150));
        newUserLabel.setBounds(200, 290, 150, 25);
        add(newUserLabel);

        // =======================
        // ActionListener für Login
        // =======================
        loginButton.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            // Dummy-Login
            if (user.equals("admin") && pass.equals("1234")) {
                JOptionPane.showMessageDialog(null, "Login erfolgreich!");
                // Hier könnt ihr z.B. DashboardStudent starten:
                new DashboardStudent();
                dispose(); // Login-Fenster schließen
            } else {
                JOptionPane.showMessageDialog(null, "Benutzername oder Passwort falsch!");
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFenster::new);
    }
}
