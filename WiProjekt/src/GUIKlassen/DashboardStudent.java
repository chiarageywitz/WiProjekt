package GUIKlassen;

import javax.swing.*;
import java.awt.*;

public class DashboardStudent extends JFrame {

    public DashboardStudent() {

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
        btnInfo.addActionListener(e -> System.out.println("INFO-Fenster öffnen…"));
        btnAnmeldung.addActionListener(e -> System.out.println("Anmeldung öffnen…"));
        btnAbgabe.addActionListener(e -> System.out.println("Abgabe öffnen…"));
        logoutBtn.addActionListener(e -> System.out.println("Logout…"));

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
        SwingUtilities.invokeLater(DashboardStudent::new);
    }
}
