package GUIKlassen;

import Datenbank.DBConnection;
import Datenbank.StudentDAO.StudentInfo;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Noteneingabe extends JFrame {

    private StudentInfo student;        
    private String rolle;
    private int mnr;
    private JFrame parent;

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
        setSize(700, 500); // etwas größer für Hinweis + Bemerkung
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(null);
        main.setBackground(Color.WHITE);
        add(main);

        int y = 15;

        // Header
        JPanel header = new JPanel();
        header.setBackground(dashboardBlue);
        header.setBounds(20, y, 260, 35);
        JLabel headerLabel = new JLabel(
                rolle.equals("betreuer") ? "Noteneingabe (Betreuer)" : "Noteneingabe (Studiendekan)"
        );
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        header.add(headerLabel);
        main.add(header);

        y += 60;

        // Thema
        JLabel themaLabel = new JLabel("Thema:");
        themaLabel.setBounds(20, y, 200, 25);
        main.add(themaLabel);

        JTextField themaField = new JTextField(student.thema);
        themaField.setBounds(20, y + 25, 640, 30);
        themaField.setEditable(false); // Thema nicht editierbar
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

        // DocumentFilter: nur Zahlen + Komma
        ((AbstractDocument) noteField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (isValidInput(string)) super.insertString(fb, offset, string, attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (isValidInput(text)) super.replace(fb, offset, length, text, attrs);
            }
            private boolean isValidInput(String text) {
                return text.matches("[0-9,]*"); // nur Ziffern + Komma
            }
        });

        main.add(noteField);
        y += 70;

        // Hinweis
        JLabel hinweisLabel = new JLabel("Hinweis: Diese Note wird für das Bachelorseminar vergeben. Benotung wie folgt: 12:3 (Studiendekan : Betreuer).");
        hinweisLabel.setBounds(20, y, 640, 25);
        hinweisLabel.setForeground(Color.DARK_GRAY);
        hinweisLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        main.add(hinweisLabel);

        y += 50;

        // Bemerkung (für Dekan)
        JLabel bemerkungLabel = new JLabel("Bemerkung:");
        bemerkungLabel.setBounds(20, y, 200, 25);
        main.add(bemerkungLabel);

        JTextField bemerkungField = new JTextField();
        bemerkungField.setBounds(20, y + 25, 640, 30);
        main.add(bemerkungField);

        y += 70;

        // Absenden
        JButton speichernBtn = new JButton("Absenden");
        speichernBtn.setBounds(20, y, 140, 35);
        styleButton(speichernBtn);
        speichernBtn.addActionListener(e -> speichern(noteField, bemerkungField));
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
    
    
    
    
    private void speichern(JTextField noteField, JTextField bemerkungField) {
        String noteText = noteField.getText().trim();
        String bemerkung = bemerkungField.getText().trim();

        if (noteText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte Note eingeben!");
            return;
        }

        try {
            // Komma zu Punkt für Dezimal
            noteText = noteText.replace(',', '.');
            double note = Double.parseDouble(noteText);

            try (Connection conn = DBConnection.getConnection()) {
                // Upsert: falls Student schon eine Note hat, aktualisieren
                String sql = "INSERT INTO noten (mnr, note_studiendekan, bemerkung) VALUES (?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE note_studiendekan = VALUES(note_studiendekan), bemerkung = VALUES(bemerkung)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, mnr);
                ps.setDouble(2, note);
                ps.setString(3, bemerkung);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Note erfolgreich gespeichert!");
                parent.setVisible(true);
                dispose();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Bitte eine gültige Zahl eingeben!");
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
