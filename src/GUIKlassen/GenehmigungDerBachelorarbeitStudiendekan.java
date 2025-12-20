package GUIKlassen;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class GenehmigungDerBachelorarbeitStudiendekan extends JFrame {

    public GenehmigungDerBachelorarbeitStudiendekan() {
        setTitle("Genehmigung der Bachelorarbeit");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);

        JPanel main = new JPanel();
        main.setLayout(null);
        add(main);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(44, 120, 206));
        titlePanel.setBounds(10, 10, 660, 35);

        JLabel titleLabel = new JLabel("Genehmigung der Bachelorarbeit");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titlePanel.add(titleLabel);

        main.add(titlePanel);

        JTextArea info = new JTextArea(
                "Bitte prüfen Sie die eingereichte Anmeldung und erteilen Sie Ihre Genehmigung.\n"
                        + "Nach der Genehmigung wird der Studen automatisch freigeschaltet, um mit der Bearbeitung zu beginnen.");
        info.setEditable(false);
        info.setOpaque(false);
        info.setBounds(20, 55, 640, 40);
        info.setFont(new Font("Arial", Font.PLAIN, 12));
        main.add(info);

        int y = 110;

        y = addLabelTextfield(main, "Name", y);
        y = addLabelTextfield(main, "Matrikelnummer", y);
        y = addLabelTextfield(main, "Studiengang", y);
        y = addLabelTextfield(main, "Thema", y);
        y = addLabelTextfield(main, "Betreuer", y);

        addFileButton(main, "NDA", y);
        y += 40;

        addFileButton(main, "Anmeldeformular", y);
        y += 60;

        JCheckBox approve = new JCheckBox("Thema genehmigen");
        approve.setBounds(20, y, 200, 30);

        JCheckBox decline = new JCheckBox("Thema ablehnen");
        decline.setBounds(250, y, 200, 30);

        approve.addActionListener(e -> { if (approve.isSelected()) decline.setSelected(false); });
        decline.addActionListener(e -> { if (decline.isSelected()) approve.setSelected(false); });

        main.add(approve);
        main.add(decline);

        JLabel begr = new JLabel("Begründung:");
        begr.setBounds(20, y + 40, 150, 20);
        main.add(begr);

        JTextArea begrField = new JTextArea();
        JScrollPane scroll = new JScrollPane(begrField);
        scroll.setBounds(20, y + 65, 630, 100);
        main.add(scroll);
        
        
        JButton btnZurueck = new JButton("Zurück");
        btnZurueck.setBounds(20, 500, 120, 30);
        btnZurueck.addActionListener(e -> dispose()); // schließt das Dialogfenster
        main.add(btnZurueck);

    }

    private int addLabelTextfield(JPanel panel, String label, int y) {
        JLabel l = new JLabel(label);
        l.setBounds(20, y, 150, 25);
        panel.add(l);

        JTextField tf = new JTextField();
        tf.setBounds(170, y, 480, 25);
        panel.add(tf);

        return y + 35;
    }

    private void addFileButton(JPanel panel, String label, int y) {
        JLabel l = new JLabel(label);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GenehmigungDerBachelorarbeitStudiendekan().setVisible(true);
        });
    }
}