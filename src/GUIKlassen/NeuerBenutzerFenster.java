package GUIKlassen;

import Datenbank.DBConnection;
import Util.PasswortUtil;
import Util.UIColors;
import Util.UIImageLoader;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;



public class NeuerBenutzerFenster extends BaseFrame {

    public NeuerBenutzerFenster() {
        super("Neuer Benutzer - Hochschule");
        JButton createButton = new JButton("Benutzer erstellen");
        
        
     // =========================
     // FENSTER & FORMULAR-BREITE
     // =========================
     int windowWidth = BaseFrame.WIDTH;
     int formWidth = 320;
     int labelWidth = 120;
     int fieldWidth = 180;

     // Formular mittig ausrichten
     int startX = (windowWidth - formWidth) / 2;
     int labelX = startX;
     int fieldX = startX + labelWidth + 10;

     // Start-Y für Formular
     int y = 230;
        
        
        
        getContentPane().setBackground(UIColors.BACKGROUND);

        // Logo oben mittig
       
        int logoWidth = 200;
        int logoX = (BaseFrame.WIDTH - logoWidth) / 2;
        add(UIImageLoader.createLogoLabel(logoX, 20, logoWidth, 100));
       

        // Roter Header ähnlich DashboardStudent
        JLabel header = new JLabel("Neuen Benutzer erstellen", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(UIColors.HFT_RED);
        header.setForeground(UIColors.TEXT_WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBounds(0, 130, BaseFrame.WIDTH, 40);
        add(header);

        // Formularfelder
        
     // =======================
     // Benutzername
     // =======================
     JLabel nameLabel = new JLabel("Benutzername:");
     nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
     nameLabel.setBounds(labelX, y, labelWidth, 25);
     add(nameLabel);

     JTextField nameField = new JTextField();
     nameField.setBounds(fieldX, y, fieldWidth, 25);
     add(nameField);
     nameField.addActionListener(e -> createButton.doClick());

     y += 40;

     // =======================
     // Passwort
     // =======================
     JLabel passLabel = new JLabel("Passwort:");
     passLabel.setFont(new Font("Arial", Font.BOLD, 14));
     passLabel.setBounds(labelX, y, labelWidth, 25);
     add(passLabel);

     JPasswordField passField = new JPasswordField();
     passField.setBounds(fieldX, y, fieldWidth, 25);
     add(passField);
     
     JLabel passHint = new JLabel("Mindestens 6 Zeichen");
     passHint.setFont(new Font("Arial", Font.ITALIC, 11));
     passHint.setForeground(Color.GRAY);
     passHint.setBounds(fieldX, y + 28, fieldWidth, 15);
     add(passHint);
     
     passField.addActionListener(e -> createButton.doClick());

     y += 60;
     
     

     // =======================
     // E-Mail
     // =======================
     JLabel emailLabel = new JLabel("E-Mail:");
     emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
     emailLabel.setBounds(labelX, y, labelWidth, 25);
     add(emailLabel);

     JTextField emailField = new JTextField();
     emailField.setBounds(fieldX, y, fieldWidth, 25);
     add(emailField);
     emailField.addActionListener(e -> createButton.doClick());

     y += 55;
     
     nameField.addActionListener(e -> createButton.doClick());
     passField.addActionListener(e -> createButton.doClick());
     emailField.addActionListener(e -> createButton.doClick());

     // =======================
     // Rolle
     // =======================
     JLabel rolleLabel = new JLabel("Rolle:");
     rolleLabel.setFont(new Font("Arial", Font.BOLD, 14));
     rolleLabel.setBounds(labelX, y, labelWidth, 25);
     add(rolleLabel);

     String[] rollen = {"Student", "Betreuer", "Dekan"};
     JComboBox<String> rolleBox = new JComboBox<>(rollen);
     rolleBox.setBounds(fieldX, y, fieldWidth, 25);
     add(rolleBox);

     y += 60;
      
       
        
        // Erstellen Button
        
        
     int buttonY = y + 10;

     createButton.setBounds(
         startX + (formWidth - 180) / 2,
         buttonY,
         180,
         35
     );
     
        createButton.setBackground(UIColors.PRIMARY_BLUE);
        createButton.setForeground(UIColors.TEXT_WHITE);
        createButton.setFont(new Font("Arial", Font.BOLD, 14));
        createButton.setFocusPainted(false);
        createButton.setOpaque(true);
        createButton.setBorderPainted(false);
        add(createButton);
        
        y += 20;

        createButton.addActionListener(e -> {

            String user = nameField.getText().trim();
            String pass = new String(passField.getPassword());
            String email = emailField.getText().trim();
            String rolle = (String) rolleBox.getSelectedItem();
            

            // =========================
            // 1. Pflichtfelder prüfen
            // =========================
            if (user.isEmpty() || pass.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Bitte alle Felder ausfüllen!");
                return;
            }

            // =========================
            // 2. E-Mail-Format prüfen
            // =========================
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                JOptionPane.showMessageDialog(this,
                        "Bitte eine gültige E-Mail-Adresse eingeben!");
                return;
            }

            // =========================
            // 3. Passwortlänge prüfen
            // =========================
            if (pass.length() < 6) {
                JOptionPane.showMessageDialog(this,
                        "Passwort muss mindestens 6 Zeichen lang sein!");
                return;
            }

            // =========================
            // 4. AB HIER Datenbank
            // =========================

            try {
                Connection conn = DBConnection.getConnection();
                
                String checkSql = "SELECT COUNT(*) FROM studentendb WHERE email = ?";
                PreparedStatement checkPs = conn.prepareStatement(checkSql);
                checkPs.setString(1, email);

                ResultSet rs = checkPs.executeQuery();
                rs.next();

                if (rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Diese E-Mail existiert bereits!");
                    conn.close();
                    return;
                }

                String sql = """
                	    INSERT INTO studentendb (Nachname, Vorname, email, rolle, passwort)
                	    VALUES (?, ?, ?, ?, ?)
                	""";

                	PreparedStatement ps = conn.prepareStatement(sql);
                	ps.setString(1, user);
                	ps.setString(2, user);
                	ps.setString(3, email);
                	ps.setString(4, rolle);
                	ps.setString(5, PasswortUtil.hash(pass));

                	ps.executeUpdate();
                	conn.close();

                JOptionPane.showMessageDialog(null, "Benutzer erfolgreich erstellt!");
                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                	    "Benutzer konnte nicht erstellt werden.");
            }
        
        
        });

        JLabel backToLoginLabel =
                new JLabel("<HTML><U>Bereits ein Konto? Zum Login</U></HTML>");
        backToLoginLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        backToLoginLabel.setForeground(UIColors.PRIMARY_BLUE);
        backToLoginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        int linkWidth = backToLoginLabel.getPreferredSize().width;
        int linkY = buttonY + 50;

        backToLoginLabel.setBounds(
            startX + (formWidth - linkWidth) / 2,
            linkY,
            linkWidth,
            25
        );

        add(backToLoginLabel);
        	
        	backToLoginLabel.addMouseListener(new MouseAdapter() {
        	    @Override
        	    public void mouseClicked(MouseEvent e) {
        	        dispose();            // 🔴 NeuerBenutzerFenster schließen
        	        new LoginFenster();   // 🔵 LoginFenster öffnen
        	    }
        	});
        
        
        setVisible(true);
        setAlwaysOnTop(true);
    }
}