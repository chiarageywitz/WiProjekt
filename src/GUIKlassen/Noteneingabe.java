package GUIKlassen;

import Datenbank.DBConnection;
import Datenbank.StudentDAO.StudentInfo;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Frame-Klasse für die Noteneingabe eines Studenten.
 * Ermöglicht rollenabhängige Eingabe (Betreuer oder Studiendekan),
 * automatische Berechnung der Endnote und Speicherung in der DB.
 * Die Noten bleiben beim nächsten Öffnen in den Feldern sichtbar.
 */
public class Noteneingabe extends JFrame {

    private StudentInfo student;
    private String rolle;
    private int mnr;
    private JFrame parent;

    private final Color dashboardBlue = new Color(0, 45, 150);

    private JTextField noteBetreuerField;
    private JTextField noteDekanField;
    private JTextField endnoteField;

    /**
     * Konstruktor für die Noteneingabe.
     *
     * @param student StudentInfo-Objekt
     * @param rolle   Rolle des Benutzers ("betreuer" oder "studiendekan")
     * @param parent  Parent-Frame
     */
    public Noteneingabe(StudentInfo student, String rolle, JFrame parent) {
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
        setSize(700, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(null);
        main.setBackground(Color.WHITE);
        add(main);

        int y = 15;

        // Header
        JPanel header = new JPanel();
        header.setBackground(dashboardBlue);
        header.setBounds(20, y, 300, 35);
        JLabel headerLabel = new JLabel(
                rolle.equals("betreuer") ? "Noteneingabe (Betreuer)" : "Noteneingabe (Studiendekan)");
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
        themaField.setEditable(false);
        main.add(themaField);

        y += 70;

        // Note Betreuer
        JLabel betreuerLabel = new JLabel("Note Betreuer:");
        betreuerLabel.setBounds(20, y, 200, 25);
        main.add(betreuerLabel);

        noteBetreuerField = createNoteField();
        noteBetreuerField.setBounds(20, y + 25, 200, 30);
        main.add(noteBetreuerField);

        JButton speichernBetreuerBtn = new JButton("Note speichern");
        speichernBetreuerBtn.setBounds(240, y + 25, 160, 30);
        styleButton(speichernBetreuerBtn);
        speichernBetreuerBtn.addActionListener(e -> speichereEinzelNote("note_betreuer", noteBetreuerField));
        main.add(speichernBetreuerBtn);

        y += 70;

        // Note Dekan
        JLabel dekanLabel = new JLabel("Note Dekan:");
        dekanLabel.setBounds(20, y, 200, 25);
        main.add(dekanLabel);

        noteDekanField = createNoteField();
        noteDekanField.setBounds(20, y + 25, 200, 30);
        main.add(noteDekanField);

        JButton speichernDekanBtn = new JButton("Note speichern");
        speichernDekanBtn.setBounds(240, y + 25, 160, 30);
        styleButton(speichernDekanBtn);
        speichernDekanBtn.addActionListener(e -> speichereEinzelNote("note_studiendekan", noteDekanField));
        main.add(speichernDekanBtn);

        y += 70;

        // Endnote
        JLabel endnoteLabel = new JLabel("Zusammen berechnete Note:");
        endnoteLabel.setBounds(20, y, 300, 25);
        main.add(endnoteLabel);

        endnoteField = new JTextField();
        endnoteField.setBounds(20, y + 25, 200, 30);
        endnoteField.setEditable(false);
        main.add(endnoteField);

        y += 70;

        // Buttons Absenden und Zurück
        JButton speichernBtn = new JButton("Absenden");
        speichernBtn.setBounds(20, y, 140, 35);
        styleButton(speichernBtn);
        speichernBtn.addActionListener(e -> speichernAlles());
        main.add(speichernBtn);

        JButton zurueckBtn = new JButton("Zurück");
        zurueckBtn.setBounds(180, y, 140, 35);
        styleButton(zurueckBtn);
        zurueckBtn.addActionListener(e -> {
            parent.setVisible(true);
            dispose();
        });
        main.add(zurueckBtn);

        // Lade vorhandene Noten
        ladeNoten();

        // Setze Editierbarkeit
        setEditierbarkeit();

        // Automatisches Speichern beim Fokusverlust
        addAutoSave();

        // Berechne Endnote, falls beide vorhanden
        berechneEndnote();

        setVisible(true);
    }

    /**
     * Erstellt ein JTextField für Noteneingaben mit Filter für Zahlen und Komma.
     *
     * @return konfiguriertes JTextField
     */
    private JTextField createNoteField() {
        JTextField field = new JTextField();
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text.matches("[0-9,]*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        return field;
    }

    /**
     * Setzt die Editierbarkeit der Notenfelder abhängig von der Rolle.
     */
    private void setEditierbarkeit() {
        noteBetreuerField.setEditable(rolle.equals("betreuer"));
        noteDekanField.setEditable(rolle.equals("studiendekan"));
    }

    /**
     * Lädt vorhandene Noten aus der Datenbank und zeigt sie in den Feldern an.
     */
    private void ladeNoten() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT note_betreuer, note_studiendekan, endnote FROM noten WHERE mnr = ?");
            ps.setInt(1, mnr);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getObject("note_betreuer") != null)
                    noteBetreuerField.setText(rs.getDouble("note_betreuer") + "");
                if (rs.getObject("note_studiendekan") != null)
                    noteDekanField.setText(rs.getDouble("note_studiendekan") + "");
                if (rs.getObject("endnote") != null)
                    endnoteField.setText(rs.getDouble("endnote") + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fügt automatisches Speichern beim Verlassen der Notenfelder hinzu.
     */
    private void addAutoSave() {
        noteBetreuerField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (rolle.equals("betreuer")) {
                    speichereEinzelNote("note_betreuer", noteBetreuerField);
                }
            }
        });

        noteDekanField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (rolle.equals("studiendekan")) {
                    speichereEinzelNote("note_studiendekan", noteDekanField);
                }
            }
        });
    }

    /**
     * Speichert eine einzelne Note in der Datenbank und berechnet die Endnote neu.
     * Zeigt eine Meldung, wenn die Note erfolgreich gespeichert wurde.
     *
     * @param spalte Name der Datenbankspalte ("note_betreuer" oder "note_studiendekan")
     * @param feld   JTextField mit der Note
     */
    private void speichereEinzelNote(String spalte, JTextField feld) {
        Double note = parse(feld.getText());
        if (note == null) {
            JOptionPane.showMessageDialog(this, "Bitte eine gültige Note eingeben!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Note in der Datenbank speichern
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO noten (mnr, " + spalte + ") VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE " + spalte + " = ?"
            );
            ps.setInt(1, mnr);
            ps.setDouble(2, note);
            ps.setDouble(3, note);
            int updated = ps.executeUpdate();

            // Endnote berechnen, wenn beide Noten vorhanden sind
            berechneEndnote();

            // Bestätigung anzeigen
            JOptionPane.showMessageDialog(this, "Note erfolgreich gespeichert!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fehler beim Speichern der Note: " + ex.getMessage());
        }
    }


    /**
     * Berechnet die Endnote, sofern beide Einzelnoten vorhanden sind.
     */
    /**
     * Berechnet die Endnote, sofern beide Einzelnoten vorhanden sind.
     * Blinkt das Endnotenfeld grün und setzt Tooltip, wenn die Berechnung erfolgreich war.
     */
    private void berechneEndnote() {
        Double betreuer = parse(noteBetreuerField.getText());
        Double dekan = parse(noteDekanField.getText());

        if (betreuer != null && dekan != null) {
            double endnote = (3 * dekan + 12 * betreuer) / 15;
            endnoteField.setText(String.format("%.2f", endnote).replace(".", ","));
            
            // Tooltip setzen
            endnoteField.setToolTipText("Endnote aktualisiert");
            
            // Hintergrund kurz grün aufblinken lassen
            Color original = endnoteField.getBackground();
            endnoteField.setBackground(Color.GREEN);
            new javax.swing.Timer(1000, e -> endnoteField.setBackground(original)).start();
        }
    }


    /**
     * Speichert alle Notenfelder (Betreuer und Dekan) manuell.
     */
    private void speichernAlles() {
        speichereEinzelNote("note_betreuer", noteBetreuerField);
        speichereEinzelNote("note_studiendekan", noteDekanField);
        JOptionPane.showMessageDialog(this, "Alle Noten erfolgreich gespeichert!");
    }

    /**
     * Parst einen Texteingabewert in einen Double.
     *
     * @param text Eingabetext
     * @return Double-Wert oder null
     */
    private Double parse(String text) {
        if (text == null || text.isBlank()) return null;
        return Double.parseDouble(text.replace(",", "."));
    }

    /**
     * Formatiert einen Button im Dashboard-Stil.
     *
     * @param button JButton
     */
    private void styleButton(JButton button) {
        button.setBackground(dashboardBlue);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 13));
        button.setBorderPainted(false);
        button.setOpaque(true);
    }
}
