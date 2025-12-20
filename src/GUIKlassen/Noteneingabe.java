package GUIKlassen;

import Datenbank.DBConnection;
import Datenbank.StudentDAO;
import Datenbank.StudentDAO.StudentInfo;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Noteneingabe extends JFrame {

    private final int mnr;
    private final String rolle;
    private final StudentInfo student;
    private final JFrame parent;
    private final Color dashboardBlue = new Color(0, 45, 150); // Dashboard-Farbe

    public Noteneingabe(StudentInfo student, String rolle, JFrame parent) {
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

        // Header Panel
        JPanel header = new JPanel();
        header.setBackground(dashboardBlue); // Dashboard-Blau
        header.setBounds(20, 15, 220, 35);

        JLabel headerLabel = new JLabel(
                rolle.equals("betreuer") ? "Noteneingabe (Betreuer)" : "Noteneingabe (Studiendekan)"
        );
        headerLabel.setForeground(Color.WHITE); // weiße Schrift
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        header.add(headerLabel);
        main.add(header);

        int y = 70;

        JLabel themaLabel = new JLabel("Thema:");
        themaLabel.setBounds(20, y, 200, 25);
        main.add(themaLabel);

        JTextField themaField = new JTextField();
        themaField.setBounds(20, y + 25, 640, 30);
        themaField.setEditable(false);
        main.add(themaField);

        y += 70;
        JLabel noteLabel = new JLabel(
                rolle.equals("betreuer") ? "Note (Betreuer):" : "Note (Studiendekan):"
        );
        noteLabel.setBounds(20, y, 250, 25);
        main.add(noteLabel);

        JTextField noteField = new JTextField();
        noteField.setBounds(20, y + 25, 200, 30);
        main.add(noteField);

        y += 70;
        JButton speichernBtn = new JButton("Absenden");
        speichernBtn.setBounds(20, y, 140, 35);
        styleButton(speichernBtn); // Blau + weiße Schrift
        speichernBtn.addActionListener(e -> {
            String note = noteField.getText();

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
        });

        JButton zurueckBtn = new JButton("Zurück");
        zurueckBtn.setBounds(180, y, 140, 35);
        styleButton(zurueckBtn); // Blau + weiße Schrift
        zurueckBtn.addActionListener(e -> {
            parent.setVisible(true);
            dispose();
        });

        main.add(speichernBtn);
        main.add(zurueckBtn);

        ladeStudentDaten(themaField);
        setVisible(true);
    }

    // Einheitliche Button-Stil Methode
    private void styleButton(JButton button) {
        button.setBackground(dashboardBlue);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBorderPainted(false);
        button.setOpaque(true);
    }

    private void ladeStudentDaten(JTextField themaField) {
        try {
            StudentInfo info = StudentDAO.getStudentInfo(mnr);
            if (info != null) {
                themaField.setText(info.thema);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Studentendaten!");
        }
    }
}
