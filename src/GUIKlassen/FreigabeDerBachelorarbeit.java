package GUIKlassen;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * GUI-Fenster für die Freigabe der Bachelorarbeit durch das Unternehmen.
 * Aufbau analog zur Genehmigung der Bachelorarbeit, ohne Tabelle.
 */
public class FreigabeDerBachelorarbeit extends JFrame {

    private JTextField nameField;
    private JTextField themaField;
    private JTextField betreuerUnternehmenField;
    private JTextField zeitraumField;

    private JCheckBox approve;
    private JCheckBox decline;
    private JTextArea begruendungField;

    public FreigabeDerBachelorarbeit() {

        setTitle("Freigabe der Bachelorarbeit");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setLocationRelativeTo(null);

        JPanel main = new JPanel();
        main.setLayout(null);
        add(main);

        /* ===== HEADER ===== */
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 45, 150));
        titlePanel.setBounds(10, 10, 660, 35);

        JLabel titleLabel = new JLabel("Freigabe der Bachelorarbeit");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titlePanel.add(titleLabel);

        main.add(titlePanel);

        /* ===== INFO TEXT ===== */
        JTextArea info = new JTextArea(
                "Bitte prüfen Sie die eingereichte Bachelorarbeit und erteilen Sie die Freigabe.\n"
                        + "Nach der Freigabe kann die Arbeit offiziell fortgeführt werden.");
        info.setEditable(false);
        info.setOpaque(false);
        info.setFont(new Font("Arial", Font.PLAIN, 12));
        info.setBounds(20, 55, 640, 40);
        main.add(info);

        int y = 110;

        /* ===== EINGABEFELDER ===== */
        nameField = addLabelTextfield(main, "Name", y, "");
        y += 35;

        themaField = addLabelTextfield(main, "Thema", y, "");
        y += 35;

        betreuerUnternehmenField = addLabelTextfield(main, "Betreuer im Unternehmen", y, "");
        y += 35;

        zeitraumField = addLabelTextfield(main, "Zeitraum", y, "");
        y += 50;

        /* ===== NDA BUTTON ===== */
        addFileButton(main, "NDA", y);
        y += 60;

        /* ===== GENEHMIGUNG / ABLEHNUNG ===== */
        approve = new JCheckBox("Thema freigeben");
        approve.setBounds(20, y, 200, 30);

        decline = new JCheckBox("Thema ablehnen");
        decline.setBounds(250, y, 200, 30);

        approve.addActionListener(e -> {
            if (approve.isSelected()) {
                decline.setSelected(false);
            }
        });

        decline.addActionListener(e -> {
            if (decline.isSelected()) {
                approve.setSelected(false);
            }
        });

        main.add(approve);
        main.add(decline);
        y += 40;

        /* ===== BEGRÜNDUNG ===== */
        JLabel begrLabel = new JLabel("Begründung:");
        begrLabel.setBounds(20, y, 150, 20);
        main.add(begrLabel);

        begruendungField = new JTextArea();
        JScrollPane scroll = new JScrollPane(begruendungField);
        scroll.setBounds(20, y + 25, 640, 150);
        main.add(scroll);

        /* ===== BUTTONS ===== */
        JButton rueckgabeBtn = new JButton("Rückgabe an Studierenden");
        rueckgabeBtn.setBounds(300, 600, 220, 40);
        styleButton(rueckgabeBtn);
        rueckgabeBtn.addActionListener(e -> speichern());
        main.add(rueckgabeBtn);

        JButton freigabeBtn = new JButton("Freigeben");
        freigabeBtn.setBounds(540, 600, 120, 40);
        styleButton(freigabeBtn);
        freigabeBtn.addActionListener(e -> {
            if (!approve.isSelected()) {
                JOptionPane.showMessageDialog(this,
                        "Bitte zuerst »Thema freigeben« auswählen!");
                return;
            }
            JOptionPane.showMessageDialog(this, "Bachelorarbeit wurde freigegeben.");
            dispose();
        });
        main.add(freigabeBtn);

        
    }

    /* ===== HILFSMETHODEN ===== */

    private JTextField addLabelTextfield(JPanel panel, String label, int y, String value) {
        JLabel l = new JLabel(label + ":");
        l.setBounds(20, y, 200, 25);
        panel.add(l);

        JTextField tf = new JTextField(value);
        tf.setBounds(220, y, 430, 25);
        panel.add(tf);

        return tf;
    }

    private void addFileButton(JPanel panel, String label, int y) {
        JLabel l = new JLabel(label + ":");
        l.setBounds(20, y, 200, 25);
        panel.add(l);

        JButton openBtn = new JButton("Öffnen");
        openBtn.setBounds(220, y, 120, 25);
        openBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.home")));
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
        if (!approve.isSelected() && !decline.isSelected()) {
            JOptionPane.showMessageDialog(this,
                    "Bitte Genehmigung oder Ablehnung auswählen!");
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Freigabe wurde gespeichert (ohne Datenbankanbindung).");
        dispose();
    }

    /* ===== TEST ===== */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FreigabeDerBachelorarbeit().setVisible(true));
    }
}
