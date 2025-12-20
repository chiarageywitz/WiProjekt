package GUIKlassen;

import Datenbank.AllgemeineInformationenDAO;
import Datenbank.StudentDAO.StudentInfo;
import Datenbank.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GenehmigungDerBachelorarbeitStudiendekan extends JFrame {

    private DashboardStudiendekan dashboard;
    private StudentInfo student;

    private JTextField nameField;
    private JTextField mnrField;
    private JTextField studiengangField;
    private JTextField themaField;
    private JTextField betreuerField;
    private JTextArea begrField;
    private JCheckBox approve;
    private JCheckBox decline;

    public GenehmigungDerBachelorarbeitStudiendekan(DashboardStudiendekan dashboard, StudentInfo student) {
        this.dashboard = dashboard;
        this.student = student;

        setTitle("Genehmigung der Bachelorarbeit");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);

        JPanel main = new JPanel();
        main.setLayout(null);
        add(main);

        // Header
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 45, 150));
        titlePanel.setBounds(10, 10, 660, 35);
        JLabel titleLabel = new JLabel("Genehmigung der Bachelorarbeit");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titlePanel.add(titleLabel);
        main.add(titlePanel);

        // Info Text
        JTextArea info = new JTextArea(
                "Bitte prüfen Sie die eingereichte Anmeldung und erteilen Sie Ihre Genehmigung.\n"
                        + "Nach der Genehmigung kann der Student mit der Bearbeitung beginnen."
        );
        info.setEditable(false);
        info.setOpaque(false);
        info.setBounds(20, 55, 640, 40);
        info.setFont(new Font("Arial", Font.PLAIN, 12));
        main.add(info);

        int y = 110;

        // Felder vorausfüllen
        nameField = addLabelTextfield(main, "Name", y, student.name);
        y += 35;
        mnrField = addLabelTextfield(main, "Matrikelnummer", y, String.valueOf(student.mnr));
        y += 35;

        // Studiengang und Betreuer aus DB laden
        String studiengang = "";
        String betreuer = "";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT studiengang, betreuer_hft FROM allgemeine_informationen WHERE mnr = ?")) {
            ps.setInt(1, student.mnr);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                studiengang = rs.getString("studiengang") != null ? rs.getString("studiengang") : "";
                betreuer = rs.getString("betreuer_hft") != null ? rs.getString("betreuer_hft") : "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        studiengangField = addLabelTextfield(main, "Studiengang", y, studiengang);
        y += 35;
        themaField = addLabelTextfield(main, "Thema", y, student.thema);
        y += 35;
        betreuerField = addLabelTextfield(main, "Betreuer", y, betreuer);
        y += 50;

        // Datei-Buttons
        addFileButton(main, "NDA", y);
        y += 40;
        addFileButton(main, "Anmeldeformular", y);
        y += 60;

        // Genehmigen / Ablehnen
        approve = new JCheckBox("Thema genehmigen");
        approve.setBounds(20, y, 200, 30);
        decline = new JCheckBox("Thema ablehnen");
        decline.setBounds(250, y, 200, 30);
        approve.addActionListener(e -> { if (approve.isSelected()) decline.setSelected(false); });
        decline.addActionListener(e -> { if (decline.isSelected()) approve.setSelected(false); });
        main.add(approve);
        main.add(decline);

        y += 40;

        JLabel begr = new JLabel("Begründung:");
        begr.setBounds(20, y, 150, 20);
        main.add(begr);

        begrField = new JTextArea();
        JScrollPane scroll = new JScrollPane(begrField);
        scroll.setBounds(20, y + 25, 630, 100);
        main.add(scroll);

        // Buttons unten
        JButton zurueckBtn = new JButton("Zurück");
        zurueckBtn.setBounds(500, 500, 140, 40);
        styleButton(zurueckBtn);
        main.add(zurueckBtn);

        zurueckBtn.addActionListener(e -> {
            this.dispose();
            dashboard.setVisible(true);
        });

        JButton speichernBtn = new JButton("Speichern");
        speichernBtn.setBounds(340, 500, 140, 40);
        styleButton(speichernBtn);
        main.add(speichernBtn);
        speichernBtn.addActionListener(e -> speichern());

        setVisible(true);
    }

    private JTextField addLabelTextfield(JPanel panel, String label, int y, String value) {
        JLabel l = new JLabel(label + ":");
        l.setBounds(20, y, 150, 25);
        panel.add(l);

        JTextField tf = new JTextField(value);
        tf.setBounds(170, y, 480, 25);
        panel.add(tf);

        return tf;
    }

    private void addFileButton(JPanel panel, String label, int y) {
        JLabel l = new JLabel(label + ":");
        l.setBounds(20, y, 150, 25);
        panel.add(l);

        JButton openBtn = new JButton("Öffnen");
        openBtn.setBounds(170, y, 120, 25);
        openBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.home") + "/Desktop"));
            chooser.showOpenDialog(panel);
        });
        panel.add(openBtn);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 45, 150));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBorderPainted(false);
        button.setOpaque(true);
    }

    private void speichern() {
        try (Connection conn = DBConnection.getConnection()) {
            // Genehmigung + Begründung + Änderungen speichern
            String sql = "INSERT INTO genehmigungen (mnr, genehmigt, begruendung, thema, studiengang, betreuer) VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE genehmigt=VALUES(genehmigt), begruendung=VALUES(begruendung), thema=VALUES(thema), studiengang=VALUES(studiengang), betreuer=VALUES(betreuer)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, student.mnr);
            ps.setBoolean(2, approve.isSelected());
            ps.setString(3, begrField.getText());
            ps.setString(4, themaField.getText());
            ps.setString(5, studiengangField.getText());
            ps.setString(6, betreuerField.getText());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Änderungen erfolgreich gespeichert!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fehler beim Speichern!");
        }
    }
}
