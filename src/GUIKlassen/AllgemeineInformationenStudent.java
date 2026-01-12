<<<<<<< HEAD
package GUIKlassen;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import Datenbank.AllgemeineInformationenDAO;
import Datenbank.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * JPanel zur Anzeige und Eingabe allgemeiner Informationen eines Studenten
 * im Rahmen der Bachelorarbeit.
 */
public class AllgemeineInformationenStudent extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField tfThema;
    private JTextField tfUnternehmen;
    private JTextField tfZeitraum;
    private JComboBox<String> cbBetreuerHFT; // NEU: ComboBox statt TextField
    private JTextField tfBetreuerUnternehmen;
    private JTextField tfNdaPfad;
    private JCheckBox cbNdaJa;
    private JCheckBox cbNdaNein;
    private int mnr;
    
    // NEU: Liste für Betreuer
    private List<String> betreuerListe = new ArrayList<>();

    public AllgemeineInformationenStudent(int mnr) {
        this.mnr = mnr;

        setLayout(null);
        setBackground(Color.WHITE);

        // Titel
        JLabel title = new JLabel("Allgemeine Informationen");
        title.setOpaque(true);
        title.setBackground(new Color(0, 102, 204));
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(10, 10, 300, 35);
        add(title);

        int y = 60;

        // Eingabefelder
        tfThema = addField("Thema:", y);
        y += 55;
        tfUnternehmen = addField("Unternehmen, Ort:", y);
        y += 55;
        tfZeitraum = addField("Zeitraum:", y);
        y += 55;
        
        // NEU: Betreuer HFT als ComboBox
        JLabel betreuerLabel = new JLabel("Betreuer an der HFT:");
        betreuerLabel.setBounds(20, y, 250, 20);
        add(betreuerLabel);
        
        // Betreuerliste aus Datenbank laden
        ladeBetreuerAusDatenbank();
        cbBetreuerHFT = new JComboBox<>(betreuerListe.toArray(new String[0]));
        cbBetreuerHFT.setBounds(20, y + 20, 330, 30);
        cbBetreuerHFT.setEditable(true); // Ermöglicht auch manuelle Eingabe
        add(cbBetreuerHFT);
        
        y += 55;
        tfBetreuerUnternehmen = addField("Betreuer im Unternehmen:", y);
        y += 60;

        // NDA Auswahl
        JLabel ndaLabel = new JLabel("NDA nötig?");
        ndaLabel.setBounds(20, y, 200, 20);
        add(ndaLabel);

        cbNdaJa = new JCheckBox("Ja");
        cbNdaNein = new JCheckBox("Nein");

        cbNdaJa.setBounds(140, y, 50, 20);
        cbNdaNein.setBounds(200, y, 70, 20);

        cbNdaJa.addActionListener(e -> cbNdaNein.setSelected(false));
        cbNdaNein.addActionListener(e -> cbNdaJa.setSelected(false));

        add(cbNdaJa);
        add(cbNdaNein);

        y += 50;

        // NDA Upload
        JLabel uploadLabel = new JLabel("NDA Upload:");
        uploadLabel.setBounds(20, y, 200, 20);
        add(uploadLabel);

        tfNdaPfad = new JTextField();
        tfNdaPfad.setBounds(20, y + 25, 330, 35);
        tfNdaPfad.setEditable(false);
        add(tfNdaPfad);

        JButton uploadBtn = new JButton("Datei auswählen");
        uploadBtn.setBounds(360, y + 25, 200, 35);
        add(uploadBtn);

        uploadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                tfNdaPfad.setText(file.getAbsolutePath());
            }
        });

        y += 100;

        // Zurück-Button (links)
        JButton zurueckBtn = new JButton("Zurück");
        zurueckBtn.setBounds(20, y, 160, 40);
        zurueckBtn.setBackground(new Color(0, 102, 204));
        zurueckBtn.setForeground(Color.WHITE);
        zurueckBtn.setOpaque(true);
        zurueckBtn.setBorderPainted(false);
        add(zurueckBtn);

        // Absenden-Button (rechts)
        JButton speichernBtn = new JButton("Absenden");
        speichernBtn.setBounds(400, y, 160, 40);
        speichernBtn.setBackground(new Color(0, 102, 204));
        speichernBtn.setForeground(Color.WHITE);
        speichernBtn.setOpaque(true);
        speichernBtn.setBorderPainted(false);
        add(speichernBtn);

        // Aktionen
        zurueckBtn.addActionListener(e -> {
            new DashboardStudent(mnr);
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        speichernBtn.addActionListener(e -> speichern());
        
        // Gespeicherte Daten beim Start laden
        ladeGespeicherteDaten();
    }

    private JTextField addField(String label, int y) {
        JLabel l = new JLabel(label);
        l.setBounds(20, y, 250, 20);
        add(l);

        JTextField tf = new JTextField();
        tf.setBounds(20, y + 20, 330, 30);
        add(tf);

        return tf;
    }
    
    // NEU: Betreuer aus Datenbank laden
    private void ladeBetreuerAusDatenbank() {
        betreuerListe.clear();
        betreuerListe.add(""); // Leerer Eintrag für keine Auswahl
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT Vorname, Nachname, MNR FROM studentendb WHERE rolle = 'betreuer' ORDER BY Nachname, Vorname";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    String name = rs.getString("Vorname") + " " + rs.getString("Nachname");
                    int betreuerMnr = rs.getInt("MNR");
                    betreuerListe.add(name + " (MNR: " + betreuerMnr + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Betreuerliste");
        }
    }
    
    // NEU: MNR aus Betreuer-String extrahieren
    private Integer extrahiereBetreuerMnr(String betreuerString) {
        if (betreuerString == null || betreuerString.isEmpty()) {
            return null;
        }
        
        // Sucht nach "MNR: XXXX)" im String
        int startIndex = betreuerString.indexOf("MNR: ");
        if (startIndex != -1) {
            startIndex += 5; // Länge von "MNR: "
            int endIndex = betreuerString.indexOf(")", startIndex);
            if (endIndex != -1) {
                try {
                    String mnrStr = betreuerString.substring(startIndex, endIndex).trim();
                    return Integer.parseInt(mnrStr);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }
    
    // NEU: Betreuername ohne MNR extrahieren
    private String extrahiereBetreuername(String betreuerString) {
        if (betreuerString == null || betreuerString.isEmpty()) {
            return "";
        }
        
        // Entfernt den "(MNR: XXXX)" Teil
        int mnrIndex = betreuerString.indexOf(" (MNR:");
        if (mnrIndex != -1) {
            return betreuerString.substring(0, mnrIndex).trim();
        }
        return betreuerString.trim();
    }

    private void ladeGespeicherteDaten() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT thema, unternehmen, zeitraum, betreuer_hft, " +
                        "betreuer_unternehmen, nda_noetig, nda_dateipfad " +
                        "FROM allgemeine_informationen WHERE mnr = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, mnr);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    tfThema.setText(rs.getString("thema"));
                    tfUnternehmen.setText(rs.getString("unternehmen"));
                    tfZeitraum.setText(rs.getString("zeitraum"));
                    
                    // Betreuer HFT setzen
                    String betreuerHft = rs.getString("betreuer_hft");
                    if (betreuerHft != null && !betreuerHft.isEmpty()) {
                        // Suche ob Betreuer in der Liste ist
                        boolean gefunden = false;
                        for (int i = 0; i < cbBetreuerHFT.getItemCount(); i++) {
                            String item = cbBetreuerHFT.getItemAt(i);
                            if (item.contains(betreuerHft)) {
                                cbBetreuerHFT.setSelectedIndex(i);
                                gefunden = true;
                                break;
                            }
                        }
                        // Falls nicht in Liste, als manueller Text setzen
                        if (!gefunden) {
                            cbBetreuerHFT.setSelectedItem(betreuerHft);
                        }
                    }
                    
                    tfBetreuerUnternehmen.setText(rs.getString("betreuer_unternehmen"));
                    
                    boolean ndaNoetig = rs.getBoolean("nda_noetig");
                    if (!rs.wasNull()) {
                        if (ndaNoetig) {
                            cbNdaJa.setSelected(true);
                            cbNdaNein.setSelected(false);
                        } else {
                            cbNdaJa.setSelected(false);
                            cbNdaNein.setSelected(true);
                        }
                    }
                    
                    String ndaPfad = rs.getString("nda_dateipfad");
                    if (ndaPfad != null && !ndaPfad.isEmpty()) {
                        tfNdaPfad.setText(ndaPfad);
                    }
                    
                    if (!rs.getString("thema").isEmpty()) {
                        disableAll();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Keine gespeicherten Daten gefunden für MNR: " + mnr);
        }
    }

    private void speichern() {
        try {
            // Betreuername extrahieren (ohne MNR)
            String betreuerHft = "";
            Object selectedItem = cbBetreuerHFT.getSelectedItem();
            if (selectedItem != null) {
                betreuerHft = extrahiereBetreuername(selectedItem.toString());
            }
            
            // Betreuer MNR extrahieren
            Integer betreuerMnr = null;
            if (selectedItem != null) {
                betreuerMnr = extrahiereBetreuerMnr(selectedItem.toString());
            }
            
            // Speichern mit erweiterter Methode
            AllgemeineInformationenDAO.speichern(
                    mnr,
                    tfThema.getText(),
                    tfUnternehmen.getText(),
                    tfZeitraum.getText(),
                    betreuerHft, // Nur den Namen
                    tfBetreuerUnternehmen.getText(),
                    cbNdaJa.isSelected(),
                    tfNdaPfad.getText(),
                    betreuerMnr // MNR des ausgewählten Betreuers
            );

            JOptionPane.showMessageDialog(
                    this,
                    "<html><center>Daten erfolgreich gespeichert!<br>" + 
                    (betreuerMnr != null ? 
                     "Der Betreuer " + betreuerHft + " wurde benachrichtigt." :
                     "Ihr Betreuer wurde benachrichtigt.") + 
                    "</center></html>",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE
            );

            disableAll();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Fehler beim Speichern!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void disableAll() {
        tfThema.setEditable(false);
        tfUnternehmen.setEditable(false);
        tfZeitraum.setEditable(false);
        cbBetreuerHFT.setEnabled(false);
        tfBetreuerUnternehmen.setEditable(false);
        tfNdaPfad.setEditable(false);

        cbNdaJa.setEnabled(false);
        cbNdaNein.setEnabled(false);

        for (Component c : getComponents()) {
            if (c instanceof JButton) {
                String text = ((JButton) c).getText();
                if (text.equals("Absenden") || text.equals("Datei auswählen")) {
                    c.setEnabled(false);
                }
            }
        }
    }
}
=======
package GUIKlassen;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import Datenbank.AllgemeineInformationenDAO;

/**
 * JPanel zur Anzeige und Eingabe allgemeiner Informationen eines Studenten
 * im Rahmen der Bachelorarbeit.
 * 
 * Nach dem Absenden werden alle Eingabefelder deaktiviert,
 * sodass keine Änderungen mehr möglich sind.
 */
public class AllgemeineInformationenStudent extends JPanel {

    private static final long serialVersionUID = 1L;

    /** 
     * Textfelder für die allgemeinen Informationen
     * */
    private JTextField tfThema;
    private JTextField tfUnternehmen;
    private JTextField tfZeitraum;
    private JTextField tfBetreuerHFT;
    private JTextField tfBetreuerUnternehmen;
    private JTextField tfNdaPfad;

    /** Checkboxen zur Auswahl, ob ein NDA benötigt wird */
    private JCheckBox cbNdaJa;
    private JCheckBox cbNdaNein;

    /** Matrikelnummer des Studenten */
    private int mnr;

    /**
     * Konstruktor für das Panel "Allgemeine Informationen".
     * 
     * @param mnr Matrikelnummer des Studenten
     */
    public AllgemeineInformationenStudent(int mnr) {
        this.mnr = mnr;

        setLayout(null);
        setBackground(Color.WHITE);

        // Titel
        JLabel title = new JLabel("Allgemeine Informationen");
        title.setOpaque(true);
        title.setBackground(new Color(0, 102, 204));
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(10, 10, 300, 35);
        add(title);

        int y = 60;

        // Eingabefelder
        tfThema = addField("Thema:", y);
        y += 55;
        tfUnternehmen = addField("Unternehmen, Ort:", y);
        y += 55;
        tfZeitraum = addField("Zeitraum:", y);
        y += 55;
        tfBetreuerHFT = addField("Betreuer an der HFT:", y);
        y += 55;
        tfBetreuerUnternehmen = addField("Betreuer im Unternehmen:", y);
        y += 60;

        // NDA Auswahl
        JLabel ndaLabel = new JLabel("NDA nötig?");
        ndaLabel.setBounds(20, y, 200, 20);
        add(ndaLabel);

        cbNdaJa = new JCheckBox("Ja");
        cbNdaNein = new JCheckBox("Nein");

        cbNdaJa.setBounds(140, y, 50, 20);
        cbNdaNein.setBounds(200, y, 70, 20);

        // Gegenseitiges Ausschließen der Checkboxen
        cbNdaJa.addActionListener(e -> cbNdaNein.setSelected(false));
        cbNdaNein.addActionListener(e -> cbNdaJa.setSelected(false));

        add(cbNdaJa);
        add(cbNdaNein);

        y += 50;

        // NDA Upload
        JLabel uploadLabel = new JLabel("NDA Upload:");
        uploadLabel.setBounds(20, y, 200, 20);
        add(uploadLabel);

        tfNdaPfad = new JTextField();
        tfNdaPfad.setBounds(20, y + 25, 330, 35);
        tfNdaPfad.setEditable(false);
        add(tfNdaPfad);

        JButton uploadBtn = new JButton("Datei auswählen");
        uploadBtn.setBounds(360, y + 25, 200, 35);
        add(uploadBtn);

        uploadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                tfNdaPfad.setText(file.getAbsolutePath());
            }
        });

        y += 100;

        // Zurück-Button (links)
        JButton zurueckBtn = new JButton("Zurück");
        zurueckBtn.setBounds(20, y, 160, 40);
        zurueckBtn.setBackground(new Color(0, 102, 204));
        zurueckBtn.setForeground(Color.WHITE);
        zurueckBtn.setOpaque(true);
        zurueckBtn.setBorderPainted(false);
        add(zurueckBtn);

        // Absenden-Button (rechts)
        JButton speichernBtn = new JButton("Absenden");
        speichernBtn.setBounds(400, y, 160, 40);
        speichernBtn.setBackground(new Color(0, 102, 204));
        speichernBtn.setForeground(Color.WHITE);
        speichernBtn.setOpaque(true);
        speichernBtn.setBorderPainted(false);
        add(speichernBtn);

        // Aktionen
        zurueckBtn.addActionListener(e -> {
            new DashboardStudent(mnr);
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        speichernBtn.addActionListener(e -> speichern());
    }

    /**
     * Erstellt ein Label mit zugehörigem Textfeld.
     *
     * @param label Text des Labels
     * @param y     Vertikale Position
     * @return JTextField für die Eingabe
     */
    private JTextField addField(String label, int y) {
        JLabel l = new JLabel(label);
        l.setBounds(20, y, 250, 20);
        add(l);

        JTextField tf = new JTextField();
        tf.setBounds(20, y + 20, 330, 30);
        add(tf);

        return tf;
    }

    /**
     * Speichert die eingegebenen Daten in der Datenbank
     * und deaktiviert anschließend alle Eingabeelemente.
     */
    private void speichern() {
        try {
            AllgemeineInformationenDAO.speichern(
                    mnr,
                    tfThema.getText(),
                    tfUnternehmen.getText(),
                    tfZeitraum.getText(),
                    tfBetreuerHFT.getText(),
                    tfBetreuerUnternehmen.getText(),
                    cbNdaJa.isSelected(),
                    tfNdaPfad.getText()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "<html><center>Daten erfolgreich gespeichert!</center></html>",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Nach dem Speichern alles sperren
            disableAll();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Fehler beim Speichern!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Deaktiviert alle Eingabefelder, Checkboxen und den Absenden-Button,
     * sodass nach dem Absenden keine Änderungen mehr möglich sind.
     */
    private void disableAll() {
        tfThema.setEditable(false);
        tfUnternehmen.setEditable(false);
        tfZeitraum.setEditable(false);
        tfBetreuerHFT.setEditable(false);
        tfBetreuerUnternehmen.setEditable(false);
        tfNdaPfad.setEditable(false);

        cbNdaJa.setEnabled(false);
        cbNdaNein.setEnabled(false);

        for (Component c : getComponents()) {
            if (c instanceof JButton && ((JButton) c).getText().equals("Absenden")) {
                c.setEnabled(false);
            }
        }
    }
}
>>>>>>> branch 'tuba-clean' of https://github.com/chiarageywitz/WiProjekt.git
