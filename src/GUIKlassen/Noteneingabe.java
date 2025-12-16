package GUIKlassen;

import Datenbank.StudentDAO;
import Datenbank.StudentDAO.StudentInfo;

import javax.swing.*;
import java.awt.*;

public class Noteneingabe extends JFrame {

    private final int mnr;
    private final String rolle;

    public Noteneingabe(int mnr, String rolle) {
        this.mnr = mnr;
        this.rolle = rolle.toLowerCase();

        setTitle("Noteneingabe");
        setSize(700, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel main = new JPanel(null);
        main.setBackground(Color.WHITE);
        add(main);

        // ================= HEADER =================
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 102, 204));
        header.setBounds(20, 15, 220, 35);

        JLabel headerLabel = new JLabel(
                rolle.equals("betreuer") ? "Noteneingabe (Betreuer)" : "Noteneingabe (Studiendekan)"
        );

        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        header.add(headerLabel);
        main.add(header);

        int y = 70;

        // ================= THEMA =================
        JLabel themaLabel = new JLabel("Thema:");
        themaLabel.setBounds(20, y, 200, 25);
        main.add(themaLabel);

        JTextField themaField = new JTextField();
        themaField.setBounds(20, y + 25, 640, 30);
        themaField.setEditable(false); // Thema kann nicht geändert werden
        main.add(themaField);

        // ================= NOTE =================
        y += 70;
        JLabel noteLabel = new JLabel(
                rolle.equals("betreuer") ? "Note (Betreuer):" : "Note (Studiendekan):"
        );
        noteLabel.setBounds(20, y, 250, 25);
        main.add(noteLabel);

        JTextField noteField = new JTextField();
        noteField.setBounds(20, y + 25, 200, 30);
        main.add(noteField);

        // ================= BUTTONS =================
        y += 70;
        JButton speichernBtn = new JButton("Absenden");
        speichernBtn.setBounds(20, y, 140, 35);
        speichernBtn.setBackground(new Color(0, 102, 204));
        speichernBtn.setForeground(Color.WHITE);
        speichernBtn.setFocusPainted(false);

        speichernBtn.addActionListener(e -> {
            String note = noteField.getText();
            if (note.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bitte Note eingeben!");
                return;
            }
            // TODO: hier Note speichern (DAO)
            JOptionPane.showMessageDialog(this, "Note für " + rolle + " gespeichert (Dummy)");
        });

        JButton zurückBtn = new JButton("Zurück");
        zurückBtn.setBounds(180, y, 140, 35);
        zurückBtn.addActionListener(e -> {
            new DashboardBetreuer();
            dispose();
        });

        main.add(speichernBtn);
        main.add(zurückBtn);

        // ================= STUDENTEN DATEN LADEN =================
        ladeStudentDaten(themaField);

        setVisible(true);
    }

    private void ladeStudentDaten(JTextField themaField) {
        try {
            StudentInfo info = StudentDAO.getStudentInfo(mnr);
            if (info != null) {
                themaField.setText(info.thema); // Thema aus DB einfügen
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Studentendaten!");
            e.printStackTrace();
        }
    }
}
