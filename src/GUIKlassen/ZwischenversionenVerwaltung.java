package GUIKlassen;

import Datenbank.VersionDAO;
import Datenbank.VersionDAO.Version;
import Datenbank.VersionDAO.Feedback;
import Util.LoginSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * GUI für Zwischenversionen-Upload und Feedback-Verwaltung.
 * Student und Betreuer können Dateien hochladen und Feedback geben.
 */
public class ZwischenversionenVerwaltung extends JFrame {

    private int studentMnr;
    private int betreuerMnr;
    private String rolle; // "student" oder "betreuer"
    private DefaultTableModel tableModel;
    private JTable table;

    public ZwischenversionenVerwaltung(int studentMnr, int betreuerMnr, String rolle) {
        this.studentMnr = studentMnr;
        this.betreuerMnr = betreuerMnr;
        this.rolle = rolle;

        setTitle("Zwischenversionen & Feedback - " + (rolle.equals("student") ? "Student" : "Betreuer"));
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("Zwischenversionen und Feedback");
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(0, 45, 150));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        // Tabelle
        String[] columns = {"Version-ID", "Hochgeladen von", "Typ", "Datum", "Kommentar", "Hat Feedback"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnUpload = new JButton("Neue Version hochladen");
        btnUpload.setBackground(new Color(40, 167, 69));
        btnUpload.setForeground(Color.WHITE);
        btnUpload.setFocusPainted(false);
        btnUpload.setPreferredSize(new Dimension(220, 45));

        JButton btnFeedback = new JButton("Feedback hinzufügen");
        btnFeedback.setBackground(new Color(0, 123, 255));
        btnFeedback.setForeground(Color.WHITE);
        btnFeedback.setFocusPainted(false);
        btnFeedback.setPreferredSize(new Dimension(200, 45));

        JButton btnAnzeigen = new JButton("Feedbacks anzeigen");
        btnAnzeigen.setBackground(new Color(255, 193, 7));
        btnAnzeigen.setForeground(Color.BLACK);
        btnAnzeigen.setFocusPainted(false);
        btnAnzeigen.setPreferredSize(new Dimension(200, 45));

        JButton btnAktualisieren = new JButton("Aktualisieren");
        btnAktualisieren.setBackground(new Color(108, 117, 125));
        btnAktualisieren.setForeground(Color.WHITE);
        btnAktualisieren.setFocusPainted(false);
        btnAktualisieren.setPreferredSize(new Dimension(150, 45));

        JButton btnSchliessen = new JButton("Schließen");
        btnSchliessen.setBackground(Color.GRAY);
        btnSchliessen.setForeground(Color.WHITE);
        btnSchliessen.setFocusPainted(false);
        btnSchliessen.setPreferredSize(new Dimension(120, 45));

        buttonPanel.add(btnUpload);
        buttonPanel.add(btnFeedback);
        buttonPanel.add(btnAnzeigen);
        buttonPanel.add(btnAktualisieren);
        buttonPanel.add(btnSchliessen);
        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        btnUpload.addActionListener(e -> versionHochladen());
        btnFeedback.addActionListener(e -> feedbackHinzufuegen());
        btnAnzeigen.addActionListener(e -> feedbacksAnzeigen());
        btnAktualisieren.addActionListener(e -> ladeVersionen());
        btnSchliessen.addActionListener(e -> dispose());

        ladeVersionen();
        setVisible(true);
    }

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
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden der Versionen: " + ex.getMessage(),
                "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void versionHochladen() {
        // Datei auswählen
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Datei auswählen");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "PDF-Dateien", "pdf"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Kommentar eingeben
            String kommentar = JOptionPane.showInputDialog(this,
                "Kommentar zur Version (optional):",
                "Kommentar hinzufügen",
                JOptionPane.QUESTION_MESSAGE);

            try {
                String dateipfad = selectedFile.getAbsolutePath();
                VersionDAO.zwischenversionHochladen(studentMnr, betreuerMnr, dateipfad, 
                                                   kommentar, rolle);
                JOptionPane.showMessageDialog(this,
                    "Version wurde erfolgreich hochgeladen!\n" +
                    (rolle.equals("student") ? "Ihr Betreuer wurde benachrichtigt." : 
                                              "Der Student wurde benachrichtigt."),
                    "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                ladeVersionen();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Fehler beim Hochladen: " + ex.getMessage(),
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void feedbackHinzufuegen() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie eine Version aus.",
                "Keine Version ausgewählt", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int versionId = (int) tableModel.getValueAt(selectedRow, 0);

        // Feedback eingeben
        JTextArea textArea = new JTextArea(10, 40);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        int result = JOptionPane.showConfirmDialog(this, scrollPane,
            "Feedback eingeben", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String feedback = textArea.getText();
            if (feedback != null && !feedback.trim().isEmpty()) {
                try {
                    int verfasserMnr = rolle.equals("student") ? studentMnr : betreuerMnr;
                    VersionDAO.feedbackHinzufuegen(versionId, feedback, verfasserMnr);
                    JOptionPane.showMessageDialog(this,
                        "Feedback wurde hinzugefügt. Die andere Partei wurde benachrichtigt.",
                        "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                    ladeVersionen();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Fehler beim Hinzufügen: " + ex.getMessage(),
                        "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void feedbacksAnzeigen() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie eine Version aus.",
                "Keine Version ausgewählt", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int versionId = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            List<Feedback> feedbacks = VersionDAO.getFeedbacksForVersion(versionId);
            
            if (feedbacks.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Keine Feedbacks vorhanden.",
                    "Feedbacks", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Feedback-Dialog erstellen
            JDialog dialog = new JDialog(this, "Feedbacks - Version #" + versionId, true);
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            JTextArea feedbackArea = new JTextArea();
            feedbackArea.setEditable(false);
            feedbackArea.setLineWrap(true);
            feedbackArea.setWrapStyleWord(true);
            feedbackArea.setFont(new Font("Arial", Font.PLAIN, 13));

            StringBuilder sb = new StringBuilder();
            for (Feedback f : feedbacks) {
                sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                sb.append("Von: ").append(f.verfasser).append("\n");
                sb.append("Datum: ").append(f.datum).append("\n\n");
                sb.append(f.text).append("\n\n");
            }

            feedbackArea.setText(sb.toString());
            JScrollPane scrollPane = new JScrollPane(feedbackArea);
            dialog.add(scrollPane, BorderLayout.CENTER);

            JButton closeBtn = new JButton("Schließen");
            closeBtn.addActionListener(e -> dialog.dispose());
            JPanel btnPanel = new JPanel();
            btnPanel.add(closeBtn);
            dialog.add(btnPanel, BorderLayout.SOUTH);

            dialog.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden der Feedbacks: " + ex.getMessage(),
                "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}
