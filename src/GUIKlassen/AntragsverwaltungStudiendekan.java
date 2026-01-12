package GUIKlassen;

import Datenbank.AntragDAO;
import Datenbank.AntragDAO.Antrag;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ansicht für Studiendekan zur Genehmigung von Anträgen.
 * Nach Genehmigung können Studenten sich anmelden.
 */
public class AntragsverwaltungStudiendekan extends JFrame {

    private DefaultTableModel tableModel;
    private JTable table;

    public AntragsverwaltungStudiendekan() {
        setTitle("Antragsverwaltung - Studiendekan");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("Anträge zur Genehmigung (vom Betreuer genehmigt)");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(220, 53, 69));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(header, BorderLayout.NORTH);

        // Tabelle
        String[] columns = {"Antrag-ID", "Student", "Thema", "Unternehmen", "Betreuer", "Datum"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnGenehmigen = new JButton("Antrag genehmigen");
        btnGenehmigen.setBackground(new Color(40, 167, 69));
        btnGenehmigen.setForeground(Color.WHITE);
        btnGenehmigen.setFocusPainted(false);
        btnGenehmigen.setPreferredSize(new Dimension(200, 40));

        JButton btnAblehnen = new JButton("Antrag ablehnen");
        btnAblehnen.setBackground(new Color(220, 53, 69));
        btnAblehnen.setForeground(Color.WHITE);
        btnAblehnen.setFocusPainted(false);
        btnAblehnen.setPreferredSize(new Dimension(180, 40));

        JButton btnDetails = new JButton("Details anzeigen");
        btnDetails.setBackground(new Color(0, 123, 255));
        btnDetails.setForeground(Color.WHITE);
        btnDetails.setFocusPainted(false);
        btnDetails.setPreferredSize(new Dimension(160, 40));

        JButton btnAktualisieren = new JButton("Aktualisieren");
        btnAktualisieren.setBackground(new Color(0, 45, 150));
        btnAktualisieren.setForeground(Color.WHITE);
        btnAktualisieren.setFocusPainted(false);
        btnAktualisieren.setPreferredSize(new Dimension(150, 40));

        JButton btnSchliessen = new JButton("Schließen");
        btnSchliessen.setBackground(Color.GRAY);
        btnSchliessen.setForeground(Color.WHITE);
        btnSchliessen.setFocusPainted(false);
        btnSchliessen.setPreferredSize(new Dimension(120, 40));

        buttonPanel.add(btnGenehmigen);
        buttonPanel.add(btnAblehnen);
        buttonPanel.add(btnDetails);
        buttonPanel.add(btnAktualisieren);
        buttonPanel.add(btnSchliessen);
        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        btnGenehmigen.addActionListener(e -> antragGenehmigen());
        btnAblehnen.addActionListener(e -> antragAblehnen());
        btnDetails.addActionListener(e -> detailsAnzeigen());
        btnAktualisieren.addActionListener(e -> ladeAntraege());
        btnSchliessen.addActionListener(e -> dispose());

        ladeAntraege();
        setVisible(true);
    }

    private void ladeAntraege() {
        tableModel.setRowCount(0);
        try {
            List<Antrag> antraege = AntragDAO.getAntraegeForStudiendekan();
            for (Antrag a : antraege) {
                tableModel.addRow(new Object[]{
                    a.antragId,
                    a.studentName,
                    a.thema,
                    a.unternehmen,
                    a.betreuerName,
                    a.datum
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Fehler beim Laden der Anträge: " + ex.getMessage(),
                "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void antragGenehmigen() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie einen Antrag aus.",
                "Kein Antrag ausgewählt", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int antragId = (int) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 1);
        String thema = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Möchten Sie den Antrag wirklich genehmigen?\n\n" +
            "Student: " + studentName + "\n" +
            "Thema: " + thema + "\n\n" +
            "Der Student kann sich danach für die Bachelorarbeit anmelden.",
            "Antrag genehmigen", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                AntragDAO.dekanGenehmigen(antragId);
                JOptionPane.showMessageDialog(this,
                    "Antrag wurde genehmigt. Der Student wurde benachrichtigt.",
                    "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                ladeAntraege();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Fehler beim Genehmigen: " + ex.getMessage(),
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void antragAblehnen() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie einen Antrag aus.",
                "Kein Antrag ausgewählt", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int antragId = (int) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 1);

        String grund = JOptionPane.showInputDialog(this,
            "Bitte geben Sie einen Ablehnungsgrund ein:",
            "Antrag ablehnen - " + studentName,
            JOptionPane.QUESTION_MESSAGE);

        if (grund != null && !grund.trim().isEmpty()) {
            try {
                AntragDAO.dekanAblehnen(antragId, grund);
                JOptionPane.showMessageDialog(this,
                    "Antrag wurde abgelehnt. Student und Betreuer wurden benachrichtigt.",
                    "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                ladeAntraege();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Fehler beim Ablehnen: " + ex.getMessage(),
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void detailsAnzeigen() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Bitte wählen Sie einen Antrag aus.",
                "Kein Antrag ausgewählt", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String details = "Antrag-Details:\n\n" +
                        "Student: " + tableModel.getValueAt(selectedRow, 1) + "\n" +
                        "Thema: " + tableModel.getValueAt(selectedRow, 2) + "\n" +
                        "Unternehmen: " + tableModel.getValueAt(selectedRow, 3) + "\n" +
                        "Betreuer: " + tableModel.getValueAt(selectedRow, 4) + "\n" +
                        "Datum: " + tableModel.getValueAt(selectedRow, 5);

        JOptionPane.showMessageDialog(this, details, "Antrag-Details",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
