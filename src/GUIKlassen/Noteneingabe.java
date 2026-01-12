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

        y += 70;

        // Note Dekan
        JLabel dekanLabel = new JLabel("Note Dekan:");
        dekanLabel.setBounds(20, y, 200, 25);
        main.add(dekanLabel);

        noteDekanField = createNoteField();
        noteDekanField.setBounds(20, y + 25, 200, 30);
        main.add(noteDekanField);

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
     * Berechnet die Endnote aus der Datenbank, sofern beide Einzelnoten vorhanden sind.
     * Blinkt das Endnotenfeld grün und setzt Tooltip, wenn die Berechnung erfolgreich war.
     */
    private void berechneEndnote() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT note_betreuer, note_studiendekan FROM noten WHERE mnr = ?"
            );
            ps.setInt(1, mnr);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Double betreuer = rs.getObject("note_betreuer", Double.class);
                Double dekan = rs.getObject("note_studiendekan", Double.class);
                
                System.out.println("DEBUG berechneEndnote aus DB: Betreuer=" + betreuer + ", Dekan=" + dekan);
                
                if (betreuer != null && dekan != null) {
                    // Endnote berechnen: 80% Betreuer + 20% Dekan
                    double endnote = 0.8 * betreuer + 0.2 * dekan;
                    endnoteField.setText(String.format("%.2f", endnote).replace(".", ","));
                    
                    System.out.println("DEBUG: Berechne Endnote = " + endnote + " für MNR " + mnr);
                    
                    // Endnote in Datenbank speichern
                    PreparedStatement psUpdate = conn.prepareStatement(
                        "UPDATE noten SET endnote = ? WHERE mnr = ?"
                    );
                    psUpdate.setDouble(1, endnote);
                    psUpdate.setInt(2, mnr);
                    int rows = psUpdate.executeUpdate();
                    System.out.println("DEBUG: Endnote gespeichert, rows affected: " + rows);
                    
                    // Tooltip setzen
                    endnoteField.setToolTipText("Endnote aktualisiert");
                    
                    // Hintergrund kurz grün aufblinken lassen
                    Color original = endnoteField.getBackground();
                    endnoteField.setBackground(Color.GREEN);
                    new javax.swing.Timer(1000, e -> endnoteField.setBackground(original)).start();
                }
            }
        } catch (Exception ex) {
            System.err.println("FEHLER beim Berechnen/Speichern der Endnote:");
            ex.printStackTrace();
        }
    }


    /**
     * Speichert die Note des aktuellen Benutzers (Betreuer oder Dekan).
     */
    private void speichernAlles() {
        try (Connection conn = DBConnection.getConnection()) {
            if (rolle.equals("betreuer")) {
                // Betreuer speichert nur seine Note
                Double noteBetreuer = parse(noteBetreuerField.getText());
                
                if (noteBetreuer == null) {
                    JOptionPane.showMessageDialog(this, "Bitte geben Sie Ihre Note ein!");
                    return;
                }
                
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO noten (mnr, note_betreuer) VALUES (?, ?) " +
                        "ON DUPLICATE KEY UPDATE note_betreuer = ?"
                );
                ps.setInt(1, mnr);
                ps.setDouble(2, noteBetreuer);
                ps.setDouble(3, noteBetreuer);
                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Betreuer-Note erfolgreich gespeichert!");
                
            } else if (rolle.equals("studiendekan")) {
                // Dekan speichert nur seine Note
                Double noteDekan = parse(noteDekanField.getText());
                
                if (noteDekan == null) {
                    JOptionPane.showMessageDialog(this, "Bitte geben Sie Ihre Note ein!");
                    return;
                }
                
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO noten (mnr, note_studiendekan) VALUES (?, ?) " +
                        "ON DUPLICATE KEY UPDATE note_studiendekan = ?"
                );
                ps.setInt(1, mnr);
                ps.setDouble(2, noteDekan);
                ps.setDouble(3, noteDekan);
                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Dekan-Note erfolgreich gespeichert!");
            }

            // Noten neu laden (zeigt ggf. die Note des anderen Prüfers an)
            ladeNoten();
            
            // Endnote berechnen (falls beide Noten jetzt vorhanden sind)
            berechneEndnote();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fehler beim Speichern: " + ex.getMessage());
        }
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