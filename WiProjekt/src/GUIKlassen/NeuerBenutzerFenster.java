package GUIKlassen;

import javax.swing.*;
import java.awt.*;

public class NeuerBenutzerFenster extends JFrame {

    public NeuerBenutzerFenster() {
        setTitle("Neuer Benutzer - Hochschule");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        // Logo oben mittig
        ImageIcon logo = new ImageIcon(getClass().getResource("/GUIKlassen/hochschule.png"));
        JLabel logoLabel = new JLabel(logo);
        logoLabel.setBounds(150, 20, 200, 100);
        add(logoLabel);

        // Roter Header ähnlich DashboardStudent
        JLabel header = new JLabel("Neuen Benutzer erstellen", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(220, 53, 69));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBounds(0, 130, 500, 40);
        add(header);

        // Formularfelder
        JLabel nameLabel = new JLabel("Benutzername:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setBounds(100, 190, 120, 25);
        add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(220, 190, 180, 25);
        add(nameField);

        JLabel passLabel = new JLabel("Passwort:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setBounds(100, 230, 120, 25);
        add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(220, 230, 180, 25);
        add(passField);

        JLabel emailLabel = new JLabel("E-Mail:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setBounds(100, 270, 120, 25);
        add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(220, 270, 180, 25);
        add(emailField);

        // Erstellen Button
        JButton createButton = new JButton("Benutzer erstellen");
        createButton.setBounds(160, 320, 180, 35);
        createButton.setBackground(new Color(0, 45, 150));
        createButton.setForeground(Color.WHITE);
        createButton.setFont(new Font("Arial", Font.BOLD, 14));
        createButton.setFocusPainted(false);
        createButton.setOpaque(true);
        createButton.setBorderPainted(false);
        add(createButton);

        createButton.addActionListener(e -> {
            String user = nameField.getText();
            String pass = new String(passField.getPassword());
            String email = emailField.getText();

            JOptionPane.showMessageDialog(null, "Benutzer erstellt:\nName: " + user + "\nE-Mail: " + email);
            dispose(); // Fenster schließen
        });

        setVisible(true);
    }
}
