package GUIKlassen;

import Datenbank.DBConnection;
import javax.swing.*;

import java.awt.*;
import java.sql.*;

public class DashboardStudent extends JFrame {

    private int mnr;
    
    public DashboardStudent(int mnr) {
    	this.mnr = mnr;

        setTitle("Ansicht Student");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setLayout(null);
       

        // =======================
        // AUSLOGGEN BUTTON (oben rechts)
        // =======================
        JButton logoutBtn = new JButton("Ausloggen");
        logoutBtn.setBounds(900, 20, 140, 40);
        logoutBtn.setBackground(new Color(180, 30, 50)); // dunkleres Rot
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.setOpaque(true);
        logoutBtn.setBorderPainted(false);
        add(logoutBtn);

        // =======================
        // LINKES PANEL
        // =======================
        JPanel leftPanel = new JPanel(null);
        leftPanel.setBounds(60, 100, 380, 450);
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        add(leftPanel);

        // Rote Überschrift oben links
        JLabel leftTitle = new JLabel("Meine Funktionen", SwingConstants.CENTER);
        leftTitle.setOpaque(true);
        leftTitle.setBackground(new Color(220, 53, 69));
        leftTitle.setForeground(Color.WHITE);
        leftTitle.setFont(new Font("Arial", Font.BOLD, 16));
        leftTitle.setBounds(0, 0, 380, 50); // größere Höhe, damit Text nicht abgeschnitten wird
        leftPanel.add(leftTitle);

        // =======================
        // DUNKLE BLAUE BUTTONS LINKS
        // =======================
        JButton btnInfo = createBlueButton(
                "Allgemeine Informationen",
                "Thema, Firma, Zeitraum, Betreuer Vorschlag und NDA Status"
        );
        btnInfo.setBounds(50, 70, 280, 75);
        leftPanel.add(btnInfo);

        JButton btnAnmeldung = createBlueButton(
                "Anmeldung zur Bachelor-Arbeit",
                "Offizielles Formular (IDP bestanden)"
        );
        btnAnmeldung.setBounds(50, 170, 280, 75);
        leftPanel.add(btnAnmeldung);

        JButton btnAbgabe = createBlueButton(
                "Abgabe Bachelorarbeit",
                ""
        );
        btnAbgabe.setBounds(50, 270, 280, 75);
        leftPanel.add(btnAbgabe);

        // =======================
        // RECHTES PANEL
        // =======================
        JPanel rightPanel = new JPanel(null);
        rightPanel.setBounds(480, 100, 500, 450);
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        add(rightPanel);

        // Roter Tab oben rechts
        JLabel rightTitle = new JLabel("Portal - Benachrichtigung", SwingConstants.CENTER);
        rightTitle.setOpaque(true);
        rightTitle.setBackground(new Color(220, 53, 69));
        rightTitle.setForeground(Color.WHITE);
        rightTitle.setFont(new Font("Arial", Font.BOLD, 16));
        rightTitle.setBounds(0, 0, 500, 50); // größere Höhe
        rightPanel.add(rightTitle);

        // =======================
        // ACTION HINZUFÜGEN
        // =======================
        
        btnInfo.addActionListener(e -> {
            JFrame frame = new JFrame("Allgemeine Informationen");
            frame.setSize(650, 750);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(new AllgemeineInformationenStudent());
            frame.setVisible(true);

            dispose(); // ✅ Dashboard schließen
        });
        
        btnAnmeldung.addActionListener(e -> {
            new AnmeldungZurBachelorarbeitStudent();
            dispose(); // ✅ Dashboard schließen
        });
        
        btnAbgabe.addActionListener(e -> {
            JFrame frame = new JFrame("Abgabe Bachelorarbeit");
            frame.setSize(560, 600);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(new AbgabeBachelorarbeit());
            frame.setVisible(true);

            dispose(); // ✅ Dashboard schließen
        });
        
            
            logoutBtn.addActionListener(e -> {
                new LoginFenster();  // ✅ Login neu öffnen
                dispose();          // ✅ Dashboard schließen
            });
        
        
        ladeStudentDaten();
        setVisible(true);
    }

    // =======================
    // Methode für dunkle blaue Buttons
    // =======================
    private JButton createBlueButton(String title, String subtitle) {
        String text;
        if (subtitle.isEmpty()) {
            text = "<html><center><b>" + title + "</b></center></html>";
        } else {
            text = "<html><center><b>" + title + "</b><br><font size='2'>" + subtitle + "</font></center></html>";
        }

        JButton btn = new JButton(text);
        btn.setBackground(new Color(0, 45, 150)); // dunkles Blau
        btn.setForeground(Color.WHITE);           // Textfarbe weiß
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setBorderPainted(false);
        btn.setOpaque(true);                      // WICHTIG
        btn.setVerticalAlignment(SwingConstants.TOP);
        return btn;
    }

    // =======================
    // MAIN zum Starten
    // =======================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardStudent(4711));  // TODO: mnr aus Login übernehmen
    }
    private void ladeStudentDaten() {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM studentendb WHERE MNR = ?"
            );
            ps.setInt(1, mnr);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String vorname = rs.getString("Vorname");
                String nachname = rs.getString("Nachname");
                String email = rs.getString("email");
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Studentendaten!");
        }
    }
}