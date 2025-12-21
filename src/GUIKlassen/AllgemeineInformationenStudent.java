 package GUIKlassen;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import Datenbank.AllgemeineInformationenDAO;

public class AllgemeineInformationenStudent extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField tfThema;
    private JTextField tfUnternehmen;
    private JTextField tfZeitraum;
    private JTextField tfBetreuerHFT;
    private JTextField tfBetreuerUnternehmen;
    private JTextField tfNdaPfad;

    private JCheckBox cbNdaJa;
    private JCheckBox cbNdaNein;

    private int mnr;

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

        tfThema = addField("Thema:", y); y += 55;
        tfUnternehmen = addField("Unternehmen, Ort:", y); y += 55;
        tfZeitraum = addField("Zeitraum:", y); y += 55;
        tfBetreuerHFT = addField("Betreuer an der HFT:", y); y += 55;
        tfBetreuerUnternehmen = addField("Betreuer im Unternehmen:", y); y += 60;

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

        JButton speichernBtn = new JButton("Absenden");
        speichernBtn.setBounds(20, y, 160, 40);
        speichernBtn.setBackground(new Color(0, 102, 204));
        speichernBtn.setForeground(Color.WHITE);
        speichernBtn.setOpaque(true);
        speichernBtn.setBorderPainted(false);
        add(speichernBtn);

        speichernBtn.addActionListener(e -> speichern());
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

            JOptionPane.showMessageDialog(this,
                    "<html><center>Daten erfolgreich gespeichert!</center></html>",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE);

            new DashboardStudent(mnr);
            SwingUtilities.getWindowAncestor(this).dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Fehler beim Speichern!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
