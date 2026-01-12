package GUIKlassen;

import Datenbank.VersionDAO;
import Datenbank.VersionDAO.Version;
import Datenbank.VersionDAO.Feedback;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * GUI zur Verwaltung von Zwischenversionen und zugehörigem Feedback.
 *
 * Abhängig von der Rolle können Studenten und Betreuer Dateien hochladen,
 * Feedback erfassen und vorhandene Rückmeldungen einsehen.
 */
public class ZwischenversionenVerwaltung extends JFrame {

    /** Matrikelnummer des Studenten */
    private int studentMnr;

    /** Matrikelnummer des Betreuers */
    private int betreuerMnr;

    /** Rolle des angemeldeten Nutzers */
    private String rolle;

    /** Tabellenmodell für die Anzeige der Zwischenversionen */
    private DefaultTableModel tableModel;

    /** Tabelle zur Darstellung der Versionen */
    private JTable table;

    /**
     * Erstellt die GUI zur Verwaltung von Zwischenversionen.
     *
     * @param studentMnr Matrikelnummer des Studenten
     * @param betreuerMnr Matrikelnummer des Betreuers
     * @param rolle Rolle des Nutzers
     */
    public ZwischenversionenVerwaltung(int studentMnr, int betreuerMnr, String rolle) {
        this.studentMnr = studentMnr;
        this.betreuerMnr = betreuerMnr;
        this.rolle = rolle;

        setTitle("Zwischenversionen & Feedback");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Zwischenversionen und Feedback");
        add(header, BorderLayout.NORTH);

        String[] columns = {
            "Version-ID", "Hochgeladen von", "Typ", "Datum", "Kommentar", "Hat Feedback"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            /**
             * Verhindert das Bearbeiten von Tabellenzellen.
             *
             * @param row Zeilenindex
             * @param column Spaltenindex
             * @return false
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnUpload = new JButton("Neue Version hochladen");
        JButton btnFeedback = new JButton("Feedback hinzufügen");
        JButton btnAnzeigen = new JButton("Feedbacks anzeigen");
        JButton btnAktualisieren = new JButton("Aktualisieren");
        JButton btnSchliessen = new JButton("Schließen");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnUpload);
        buttonPanel.add(btnFeedback);
        buttonPanel.add(btnAnzeigen);
        buttonPanel.add(btnAktualisieren);
        buttonPanel.add(btnSchliessen);
        add(buttonPanel, BorderLayout.SOUTH);

        btnUpload.addActionListener(e -> versionHochladen());
        btnFeedback.addActionListener(e -> feedbackHinzufuegen());
        btnAnzeigen.addActionListener(e -> feedbacksAnzeigen());
        btnAktualisieren.addActionListener(e -> ladeVersionen());
        btnSchliessen.addActionListener(e -> dispose());

        ladeVersionen();
        setVisible(true);
    }

    /**
     * Lädt die zugehörigen Zwischenversionen aus der Datenbank
     * und aktualisiert die Tabellenanzeige.
     */
    private void ladeVersionen() {
        tableModel.setRowCount(0);

        try {
            List<Version> versionen;

            if (rolle.equals("student")) {
                versionen = VersionDAO.getVersionenForStudent(studentMnr);
            } else {
                versionen = VersionDAO.getVersionenForBetreuer(betreuerMnr);
            }

            for (Version v : versionen) {
                tableModel.addRow(new Object[]{
                    v.versionId,
                    v.hochgeladenVon,
                    v.typ,
                    v.datum,
                    v.kommentar != null ? v.kommentar : "—",
                    v.hatFeedback ? "Ja" : "Nein"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden der Versionen",
                "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Öffnet einen Dateiauswahldialog und lädt eine neue PDF-Zwischenversion hoch.
     * Optional kann ein Kommentar zur Version hinterlegt werden.
     */
    private void versionHochladen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(
            new javax.swing.filechooser.FileNameExtensionFilter("PDF-Dateien", "pdf")
        );

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            String kommentar = JOptionPane.showInputDialog(
                this,
                "Kommentar zur Version"
            );

            try {
                VersionDAO.zwischenversionHochladen(
                    studentMnr,
                    betreuerMnr,
                    selectedFile.getAbsolutePath(),
                    kommentar,
                    rolle
                );
                ladeVersionen();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Fehler beim Hochladen",
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Fügt der ausgewählten Zwischenversion ein Feedback hinzu
     * und speichert dieses in der Datenbank.
     */
    private void feedbackHinzufuegen() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Bitte Version auswählen",
                "Hinweis", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int versionId = (int) tableModel.getValueAt(selectedRow, 0);

        JTextArea textArea = new JTextArea(10, 40);
        JScrollPane scrollPane = new JScrollPane(textArea);

        if (JOptionPane.showConfirmDialog(
            this,
            scrollPane,
            "Feedback eingeben",
            JOptionPane.OK_CANCEL_OPTION
        ) == JOptionPane.OK_OPTION) {

            try {
                int verfasserMnr = rolle.equals("student")
                    ? studentMnr
                    : betreuerMnr;

                VersionDAO.feedbackHinzufuegen(
                    versionId,
                    textArea.getText(),
                    verfasserMnr
                );
                ladeVersionen();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Fehler beim Speichern des Feedbacks",
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Zeigt alle Feedbackeinträge zur ausgewählten Version an.
     */
    private void feedbacksAnzeigen() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Bitte Version auswählen",
                "Hinweis", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int versionId = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            List<Feedback> feedbacks =
                VersionDAO.getFeedbacksForVersion(versionId);

            if (feedbacks.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Keine Feedbacks vorhanden",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog(this, "Feedbacks", true);
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);

            JTextArea feedbackArea = new JTextArea();
            feedbackArea.setEditable(false);

            StringBuilder sb = new StringBuilder();
            for (Feedback f : feedbacks) {
                sb.append("Von: ").append(f.verfasser).append("\n");
                sb.append("Datum: ").append(f.datum).append("\n\n");
                sb.append(f.text).append("\n\n");
            }

            feedbackArea.setText(sb.toString());
            dialog.add(new JScrollPane(feedbackArea), BorderLayout.CENTER);

            JButton closeBtn = new JButton("Schließen");
            closeBtn.addActionListener(e -> dialog.dispose());
            dialog.add(closeBtn, BorderLayout.SOUTH);

            dialog.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden der Feedbacks",
                "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}
