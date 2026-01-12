package GUIKlassen;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import Datenbank.AntragDAO;
import Datenbank.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel zur Eingabe und Anzeige allgemeiner Informationen eines Studenten.
 */
public class AllgemeineInformationenStudent extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField tfThema;
    private JTextField tfUnternehmen;
    private JTextField tfZeitraum;
    private JComboBox<String> cbBetreuerHFT;
    private JTextField tfBetreuerUnternehmen;
    private JTextField tfNdaPfad;
    private JCheckBox cbNdaJa;
    private JCheckBox cbNdaNein;
    private int mnr;
    private List<String> betreuerListe = new ArrayList<>();

    /**
     * Erzeugt das Panel für die allgemeinen Informationen des Studenten.
     *
     * @param mnr Matrikelnummer des Studenten
     */
    public AllgemeineInformationenStudent(int mnr) {
        this.mnr = mnr;

        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Allgemeine Informationen");
        title.setOpaque(true);
        title.setBackground(new Color(0, 102, 204));
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(10, 10, 300, 35);
        add(title);

        int y = 60;

        tfThema = addField("Thema:", y);
        y += 55;
        tfUnternehmen = addField("Unternehmen, Ort:", y);
        y += 55;
        tfZeitraum = addField("Zeitraum:", y);
        y += 55;

        JLabel betreuerLabel = new JLabel("Betreuer an der HFT:");
        betreuerLabel.setBounds(20, y, 250, 20);
        add(betreuerLabel);

        ladeBetreuerAusDatenbank();
        cbBetreuerHFT = new JComboBox<>(betreuerListe.toArray(new String[0]));
        cbBetreuerHFT.setBounds(20, y + 20, 330, 30);
        cbBetreuerHFT.setEditable(true);
        add(cbBetreuerHFT);
        y += 55;

        tfBetreuerUnternehmen = addField("Betreuer im Unternehmen:", y);
        y += 60;

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

        JButton zurueckBtn = new JButton("Zurück");
        zurueckBtn.setBounds(20, y, 160, 40);
        zurueckBtn.setBackground(new Color(0, 102, 204));
        zurueckBtn.setForeground(Color.WHITE);
        zurueckBtn.setOpaque(true);
        zurueckBtn.setBorderPainted(false);
        add(zurueckBtn);

        JButton speichernBtn = new JButton("Absenden");
        speichernBtn.setBounds(400, y, 160, 40);
        speichernBtn.setBackground(new Color(0, 102, 204));
        speichernBtn.setForeground(Color.WHITE);
        speichernBtn.setOpaque(true);
        speichernBtn.setBorderPainted(false);
        add(speichernBtn);

        zurueckBtn.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        speichernBtn.addActionListener(e -> speichern());

        ladeGespeicherteDaten();
    }

    /**
     * Fügt ein Textfeld mit Label hinzu.
     *
     * @param label Beschriftung
     * @param y Position Y
     * @return Das erzeugte Textfeld
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
     * Lädt die Liste der Betreuer aus der Datenbank.
     */
    private void ladeBetreuerAusDatenbank() {
        betreuerListe.clear();
        betreuerListe.add("");

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

    /**
     * Extrahiert die Betreuer-Matrikelnummer aus einem String.
     *
     * @param betreuerString Betreuer-String
     * @return Matrikelnummer oder null
     */
    private Integer extrahiereBetreuerMnr(String betreuerString) {
        if (betreuerString == null || betreuerString.isEmpty()) return null;

        int startIndex = betreuerString.indexOf("MNR: ");
        if (startIndex != -1) {
            startIndex += 5;
            int endIndex = betreuerString.indexOf(")", startIndex);
            if (endIndex != -1) {
                try {
                    return Integer.parseInt(betreuerString.substring(startIndex, endIndex).trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Extrahiert den Betreuername ohne MNR aus einem String.
     *
     * @param betreuerString Betreuer-String
     * @return Betreuername
     */
    private String extrahiereBetreuername(String betreuerString) {
        if (betreuerString == null || betreuerString.isEmpty()) return "";
        int mnrIndex = betreuerString.indexOf(" (MNR:");
        if (mnrIndex != -1) return betreuerString.substring(0, mnrIndex).trim();
        return betreuerString.trim();
    }

    /**
     * Lädt gespeicherte Daten des Studenten aus der Datenbank.
     */
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

                    String betreuerHft = rs.getString("betreuer_hft");
                    if (betreuerHft != null && !betreuerHft.isEmpty()) {
                        boolean gefunden = false;
                        for (int i = 0; i < cbBetreuerHFT.getItemCount(); i++) {
                            String item = cbBetreuerHFT.getItemAt(i);
                            if (item.contains(betreuerHft)) {
                                cbBetreuerHFT.setSelectedIndex(i);
                                gefunden = true;
                                break;
                            }
                        }
                        if (!gefunden) cbBetreuerHFT.setSelectedItem(betreuerHft);
                    }

                    tfBetreuerUnternehmen.setText(rs.getString("betreuer_unternehmen"));

                    boolean ndaNoetig = rs.getBoolean("nda_noetig");
                    if (!rs.wasNull()) {
                        cbNdaJa.setSelected(ndaNoetig);
                        cbNdaNein.setSelected(!ndaNoetig);
                    }

                    String ndaPfad = rs.getString("nda_dateipfad");
                    if (ndaPfad != null && !ndaPfad.isEmpty()) tfNdaPfad.setText(ndaPfad);

                    if (!rs.getString("thema").isEmpty()) disableAll();
                }
            }
        } catch (Exception e) {
            System.out.println("Keine gespeicherten Daten gefunden für MNR: " + mnr);
        }
    }

    /**
     * Speichert die eingegebenen Daten in der Datenbank.
     */
    private void speichern() {
        try {
            if (tfThema.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bitte geben Sie ein Thema ein!");
                return;
            }
            if (tfUnternehmen.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bitte geben Sie ein Unternehmen ein!");
                return;
            }

            String betreuerHft = "";
            Object selectedItem = cbBetreuerHFT.getSelectedItem();
            if (selectedItem != null) betreuerHft = extrahiereBetreuername(selectedItem.toString());

            Integer betreuerMnr = extrahiereBetreuerMnr(selectedItem != null ? selectedItem.toString() : "");
            if (betreuerMnr == null) {
                JOptionPane.showMessageDialog(this, "Bitte wählen Sie einen Betreuer aus der Liste aus!");
                return;
            }

            AntragDAO.antragErstellen(
                    mnr,
                    tfThema.getText().trim(),
                    tfUnternehmen.getText().trim(),
                    tfZeitraum.getText().trim(),
                    betreuerHft,
                    tfBetreuerUnternehmen.getText().trim(),
                    cbNdaJa.isSelected(),
                    tfNdaPfad.getText().trim(),
                    betreuerMnr
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Antrag erfolgreich eingereicht! Der Betreuer " + betreuerHft + " wurde benachrichtigt.",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE
            );

            disableAll();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fehler beim Speichern!", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deaktiviert alle Eingabefelder und Buttons.
     */
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
