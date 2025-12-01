package GUIKlassen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFenster extends JFrame {

    public LoginFenster() {
        setTitle("Login - Hochschule");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(null);

        getContentPane().setBackground(new Color(240, 240, 240));

        // =======================
        // Hochschul-Logo oben mittig (proportional skaliert)
        // =======================
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/GUIKlassen/hochschule.png"));

        // Zielgröße für Logo
        int targetWidth = 200;
        int targetHeight = 100;

        // Originalgröße des Bildes
        int originalWidth = originalIcon.getIconWidth();
        int originalHeight = originalIcon.getIconHeight();

        // Berechne Skalierung unter Beibehaltung des Seitenverhältnisses
        double widthRatio = (double) targetWidth / originalWidth;
        double heightRatio = (double) targetHeight / originalHeight;
        double scale = Math.min(widthRatio, heightRatio);

        int finalWidth = (int) (originalWidth * scale);
        int finalHeight = (int) (originalHeight * scale);

        Image scaledImage = originalIcon.getImage().getScaledInstance(finalWidth, finalHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel logoLabel = new JLabel(scaledIcon);
        // Mittig positionieren
        int x = (getWidth() - finalWidth) / 2;
        logoLabel.setBounds(x, 20, finalWidth, finalHeight);
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
        // Einloggen Button
        // =======================
        JButton loginButton = new JButton("Einloggen");
        loginButton.setBounds(180, 230, 140, 35);
        loginButton.setBackground(new Color(0, 45, 150));
        loginButton.setForeground(Color.WHITE);
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
        newUserLabel.setForeground(new Color(0, 45, 150));
        newUserLabel.setBounds(200, 290, 150, 25);
        newUserLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(newUserLabel);

        // =======================
        // ActionListener Login
        // =======================
        loginButton.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if (user.equals("admin") && pass.equals("1234")) {
                JOptionPane.showMessageDialog(null, "Login erfolgreich!");
                new DashboardStudent(); // Dashboard öffnen
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Benutzername oder Passwort falsch!");
            }
        });

        // =======================
        // Neuer Benutzer Action
        // =======================
        newUserLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new NeuerBenutzerFenster(); // Neues Fenster für Benutzererstellung öffnen
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFenster::new);
    }
}
