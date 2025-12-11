package Datenbank;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;



public class NeuerBenutzerFenster extends JFrame {

    public NeuerBenutzerFenster() {
        setTitle("Neuer Benutzer - Hochschule");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        // Logo oben mittig
        java.net.URL imgUrl = getClass().getClassLoader().getResource("hochschule.png");

        if (imgUrl != null) {
        	ImageIcon icon = new ImageIcon(imgUrl);

            Image scaledImg = icon.getImage().getScaledInstance(200, 100, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImg);

            JLabel logoLabel = new JLabel(scaledIcon);

            logoLabel.setBounds(150, 20, 200, 100);
            add(logoLabel);
        } else {
            System.out.println("⚠️ Logo nicht gefunden!");
        }
       

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

        JLabel rolleLabel = new JLabel("Rolle:");
        rolleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rolleLabel.setBounds(100, 310, 120, 25);
        add(rolleLabel);

        String[] rollen = {"student", "betreuer", "dekan"};
        JComboBox<String> rolleBox = new JComboBox<>(rollen);
        rolleBox.setBounds(220, 310, 180, 25);
        add(rolleBox);
        
       
        
        // Erstellen Button
        JButton createButton = new JButton("Benutzer erstellen");
        createButton.setBounds(160, 360, 180, 35);
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
            String rolle = (String) rolleBox.getSelectedItem();

            if (user.isEmpty() || pass.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Bitte alle Felder ausfüllen!");
                return;
            }

            try {
                Connection conn = DBConnection.getConnection();

                String sql = "INSERT INTO studentendb (MNR, Nachname, Vorname, email, rolle) VALUES (?, ?, ?, ?, ?)";

                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(pass)); // Passwort = MNR
                ps.setString(2, user);                // Nachname
                ps.setString(3, user);                // Vorname (später ändern)
                ps.setString(4, email);               // E-Mail
                ps.setString(5, rolle);               // Rolle

                ps.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(null, "Benutzer erfolgreich erstellt!");
                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Fehler: Benutzer konnte nicht gespeichert werden!");
            }
        
        
        });

        setVisible(true);
        setAlwaysOnTop(true);
    }
}