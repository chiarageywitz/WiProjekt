package Datenbank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFenster extends JFrame {

    public LoginFenster() {
        setTitle("Login - Hochschule");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(null);

        getContentPane().setBackground(new Color(240, 240, 240));

     // =======================
     // Hochschul-Logo oben mittig (STABIL)
     // =======================

        java.net.URL imgUrl = getClass().getClassLoader().getResource("hochschule.png");

        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            Image img = icon.getImage().getScaledInstance(200, 100, Image.SCALE_SMOOTH);

            JLabel logoLabel = new JLabel(new ImageIcon(img));
            logoLabel.setBounds(150, 20, 200, 100);
            add(logoLabel);
        } else {
            System.out.println("❌ Bild nicht gefunden!");
        }
            
        
        
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

            try {
                Connection conn = DBConnection.getConnection();

                String sql = "SELECT * FROM studentendb WHERE Nachname = ? AND MNR = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, user);
                ps.setInt(2, Integer.parseInt(pass)); // Passwort = MNR

                ResultSet rs = ps.executeQuery();

                	if (rs.next()) {

                	    int mnr = rs.getInt("MNR");
                	    String rolle = rs.getString("rolle");   // ✅ WICHTIG

                	    JOptionPane.showMessageDialog(null, "Login erfolgreich!");

                	    if (rolle.equalsIgnoreCase("student")) {
                	        new DashboardStudent(mnr);
                	    } 
                	    else if (rolle.equalsIgnoreCase("betreuer")) {
                	        new DashboardBetreuer();
                	    } 
                	    else if (rolle.equalsIgnoreCase("dekan")) {
                	        new DashboardStudiendekan();
                	    }

                	    dispose(); // ✅ Login schließen
                	}
                	else {
                	    JOptionPane.showMessageDialog(null, "Benutzername oder Passwort falsch!");
                	}
                	
                	
                	

                conn.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage());
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