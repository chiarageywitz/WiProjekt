package GUIKlassen;

import Datenbank.AntragDAO;
import Datenbank.AntragDAO.Antrag;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ansicht für Betreuer zur Verwaltung eingehender Anträge.
 * Betreuer können Anträge genehmigen oder ablehnen.
 */
public class AntragsverwaltungBetreuer extends JFrame {

    private int betreuerMnr;
    private DefaultTableModel tableModel;
    private JTable table;

    public AntragsverwaltungBetreuer(int betreuerMnr) {
        this.betreuerMnr = betreuerMnr;

        setTitle("Antragsverwaltung - Betreuer");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("Anträge zur Bachelorarbeit");
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(220, 53, 69));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(header, BorderLayout.NORTH);

        // Tabelle
        String[] columns = {"Antrag-ID", "Student", "Thema", "Unternehmen", "Status", "Datum"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnGenehmigen = new JButton("Antrag genehmigen");
        btnGenehmigen.setBackground(new Color(40, 167, 69));
        btnGenehmigen.setForeground(Color.WHITE);
        btnGenehmigen.setFocusPainted(false);
        btnGenehmigen.setPreferredSize(new Dimension(180, 40));

        JButton btnAblehnen = new JButton("Antrag ablehnen");
        btnAblehnen.setBackground(new Color(220, 53, 69));
        btnAblehnen.setForeground(Color.WHITE);
        btnAblehnen.setFocusPainted(false);
        btnAblehnen.setPreferredSize(new Dimension(180, 40));

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
        buttonPanel.add(btnAktualisieren);
        buttonPanel.add(btnSchliessen);
        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        btnGenehmigen.addActionListener(e -> antragGenehmigen());
        btnAblehnen.addActionListener(e -> antragAblehnen());
        btnAktualisieren.addActionListener(e -> ladeAntraege());
        btnSchliessen.addActionListener(e -> dispose());

        ladeAntraege();
        setVisible(true);
    }

    private void ladeAntraege() {
        tableModel.setRowCount(0);
        try {
            List<Antrag> antraege = AntragDAO.getAntraegeForBetreuer(betreuerMnr);
            for (Antrag a : antraege) {
                if ("offen".equals(a.status)) { // Nur offene Anträge anzeigen
                    tableModel.addRow(new Object[]{
                        a.antragId,
                        a.studentName,
                        a.thema,
                        a.unternehmen,
                        a.status,
                        a.datum
                    });
                }
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

        int confirm = JOptionPane.showConfirmDialog(this,
            "Möchten Sie den Antrag von " + studentName + " wirklich genehmigen?\n" +
            "Der Antrag wird an den Studiendekan weitergeleitet.",
            "Antrag genehmigen", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                AntragDAO.betreuerGenehmigen(antragId, betreuerMnr);
                JOptionPane.showMessageDialog(this,
                    "Antrag wurde genehmigt und an den Studiendekan weitergeleitet.",
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
                AntragDAO.betreuerAblehnen(antragId, grund);
                JOptionPane.showMessageDialog(this,
                    "Antrag wurde abgelehnt. Der Student wurde benachrichtigt.",
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
}
