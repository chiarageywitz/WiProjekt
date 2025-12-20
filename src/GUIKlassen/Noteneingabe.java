package GUIKlassen;

import Datenbank.DBConnection;
import Datenbank.StudentDAO.StudentInfo;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Noteneingabe extends JFrame {

    private StudentInfo student;        // nicht final
    private  String rolle;
    private  int mnr;
    private  JFrame parent;

    private final Color dashboardBlue = new Color(0, 45, 150);

    public Noteneingabe(StudentInfo student, String rolle, JFrame parent) {

        // Sicherheitscheck
        if (student == null) {
            JOptionPane.showMessageDialog(null, "Kein Student übergeben!");
            dispose();
            return;
        }

        this.student = student;
        this.rolle = rolle.toLowerCase();
        this.mnr = student.mnr;
        this.parent = parent;

        setTitle("Noteneingabe");
        setSize(700, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(null);
        main.setBackground(Color.WHITE);
        add(main);

        // Header
        JPanel header = new JPanel();
        header.setBackground(dashboardBlue);
        header.setBounds(20, 15, 260, 35);

        JLabel headerLabel = new JLabel(
                rolle.equals("betreuer") ? "Noteneingabe (Betreuer)" : "Noteneingabe (Studiendekan)"
        );
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        header.add(headerLabel);
        main.add(header);

        int y = 70;

        // Thema
        JLabel themaLabel = new JLabel("Thema:");
        themaLabel.setBounds(20, y, 200, 25);
        main.add(themaLabel);

        JTextField themaField = new JTextField(student.thema);
        themaField.setBounds(20, y + 25, 640, 30);
        themaField.setEditable(false);
        main.add(themaField);

        y += 70;

        // Note
        JLabel noteLabel = new JLabel(
                rolle.equals("betreuer") ? "Note (Betreuer):" : "Note (Studiendekan):"
        );
        noteLabel.setBounds(20, y, 250, 25);
        main.add(noteLabel);

        JTextField noteField = new JTextField();
        noteField.setBounds(20, y + 25, 200, 30);
        main.add(noteField);

        y += 70;

        // Absenden
        JButton speichernBtn = new JButton("Absenden");
        speichernBtn.setBounds(20, y, 140, 35);
        styleButton(speichernBtn);
        speichernBtn.addActionListener(e -> speichern(noteField));
        main.add(speichernBtn);

        // Zurück
        JButton zurueckBtn = new JButton("Zurück");
        zurueckBtn.setBounds(180, y, 140, 35);
        styleButton(zurueckBtn);
        zurueckBtn.addActionListener(e -> {
            parent.setVisible(true);
            dispose();
        });
        main.add(zurueckBtn);

        setVisible(true);
    }

    // =============================
    // Logik
    // =============================

    private void speichern(JTextField noteField) {
        String note = noteField.getText().trim();

        if (note.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte Note eingeben!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO benachrichtigungen (mnr, text) VALUES (?, ?)"
            );
            ps.setInt(1, mnr);
            ps.setString(2, "Neue Note vom " + rolle + ": " + note);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Note erfolgreich gespeichert!");
            parent.setVisible(true);
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fehler beim Speichern der Note!");
        }
    }

    private void styleButton(JButton button) {
        button.setBackground(dashboardBlue);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBorderPainted(false);
        button.setOpaque(true);
    }
}
