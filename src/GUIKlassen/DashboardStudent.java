package GUIKlassen;

import Datenbank.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DashboardStudent extends JFrame {

    private int mnr;
    private JTextArea portalArea; // ⬅ NEU

    public DashboardStudent(int mnr) {
        this.mnr = mnr;

        setTitle("Ansicht Student");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setLayout(null);

        // =======================
        // AUSLOGGEN BUTTON
        // =======================
        JButton logoutBtn = new JButton("Ausloggen");
        logoutBtn.setBounds(900, 20, 140, 40);
        logoutBtn.setBackground(new Color(180, 30, 50));
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

        JLabel leftTitle = new JLabel("Meine Funktionen", SwingConstants.CENTER);
        leftTitle.setOpaque(true);
        leftTitle.setBackground(new Color(220, 53, 69));
        leftTitle.setForeground(Color.WHITE);
        leftTitle.setFont(new Font("Arial", Font.BOLD, 16));
        leftTitle.setBounds(0, 0, 380, 50);
        leftPanel.add(leftTitle);

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
        // RECHTES PANEL – PORTAL
        // =======================
        JPanel rightPanel = new JPanel(null);
        rightPanel.setBounds(480, 100, 500, 450);
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        add(rightPanel);

        JLabel rightTitle = new JLabel("Portal - Benachrichtigungen", SwingConstants.CENTER);
        rightTitle.setOpaque(true);
        rightTitle.setBackground(new Color(220, 53, 69));
        rightTitle.setForeground(Color.WHITE);
        rightTitle.setFont(new Font("Arial", Font.BOLD, 16));
        rightTitle.setBounds(0, 0, 500, 50);
        rightPanel.add(rightTitle);

        portalArea = new JTextArea();
        portalArea.setEditable(false);
        portalArea.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(portalArea);
        scrollPane.setBounds(20, 70, 460, 350);
        rightPanel.add(scrollPane);

        // =======================
        // ACTIONS
        // =======================
        btnInfo.addActionListener(e -> {
            JFrame frame = new JFrame("Allgemeine Informationen");
            frame.setSize(650, 750);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(new AllgemeineInformationenStudent(mnr));
            frame.setVisible(true);
            dispose();
        });

        btnAnmeldung.addActionListener(e -> {
            new AnmeldungZurBachelorarbeitStudent();
            dispose();
        });

        btnAbgabe.addActionListener(e -> {
            JFrame frame = new JFrame("Abgabe Bachelorarbeit");
            frame.setSize(560, 600);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(new AbgabeBachelorarbeit());
            frame.setVisible(true);
            dispose();
        });

        logoutBtn.addActionListener(e -> {
            new LoginFenster();
            dispose();
        });

        ladeBenachrichtigungen(); // ⬅ NEU
        setVisible(true);
    }

    // =======================
    // BUTTON HELFER
    // =======================
    private JButton createBlueButton(String title, String subtitle) {
        String text = subtitle.isEmpty()
                ? "<html><center><b>" + title + "</b></center></html>"
                : "<html><center><b>" + title + "</b><br><font size='2'>" + subtitle + "</font></center></html>";

        JButton btn = new JButton(text);
        btn.setBackground(new Color(0, 45, 150));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setVerticalAlignment(SwingConstants.TOP);
        return btn;
    }

 // =======================
 // 🔔 BENACHRICHTIGUNGEN LADEN
 // =======================
 private void ladeBenachrichtigungen() {
     try (Connection conn = DBConnection.getConnection();
          PreparedStatement ps = conn.prepareStatement(
                  "SELECT text, datum FROM benachrichtigungen WHERE mnr = ? ORDER BY datum DESC"
          )) {

         ps.setInt(1, mnr);
         ResultSet rs = ps.executeQuery();

         portalArea.setText("");

         while (rs.next()) {
             String text = rs.getString("text");

             // 👉 NUR DATUM (ohne Uhrzeit)
             Date datum = rs.getDate("datum");

             portalArea.append(
                     "• " + text +
                     " (" + datum + ")\n\n"
             );
         }

         if (portalArea.getText().isEmpty()) {
             portalArea.setText("Keine Benachrichtigungen vorhanden.");
         }

     } catch (Exception e) {
         e.printStackTrace();
         portalArea.setText("Fehler beim Laden der Benachrichtigungen.");
     }
 }


    // =======================
    // MAIN (TEST)
    // =======================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardStudent(4711));
    }
}
