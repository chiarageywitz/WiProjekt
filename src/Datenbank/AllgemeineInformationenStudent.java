package Datenbank;
import javax.swing.*; 
import java.awt.*;   
import java.io.File;
import Datenbank.DashboardStudent;

public class AllgemeineInformationenStudent extends JPanel {
	 private static final long serialVersionUID = 1L;

    public AllgemeineInformationenStudent() { 

        setLayout(null);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));

        // ===== Titel =====
        JLabel title = new JLabel("Allgemeine Informationen");
        title.setOpaque(true);
        title.setBackground(new Color(0, 102, 204));
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(10, 0, 280, 35);   // Nicht mehr abgeschnitten
        add(title);

        int y = 50;

        addField("Thema:", y);                         y += 60;
        addField("Unternehmen, Ort:", y);              y += 60;
        addField("Zeitraum:", y);                      y += 60;
        addField("Betreuer an der HFT:", y);           y += 60;
        addField("Betreuer im Unternehmen:", y);       y += 60;

        // ===== NDA nötig =====
        JLabel ndaLabel = new JLabel("NDA nötig?:");
        ndaLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        ndaLabel.setBounds(20, y, 150, 30);
        add(ndaLabel);

        JCheckBox ja = new JCheckBox("Ja");
        ja.setBounds(140, y, 50, 30);

        JCheckBox nein = new JCheckBox("Nein");
        nein.setBounds(200, y, 80, 30);

        ja.addActionListener(e -> { if (ja.isSelected()) nein.setSelected(false); });
        nein.addActionListener(e -> { if (nein.isSelected()) ja.setSelected(false); });

        add(ja);
        add(nein);

        y += 60;

        // ========= Upload ==========
        JLabel uploadLabel = new JLabel("NDA Upload:");
        uploadLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        uploadLabel.setBounds(20, y, 200, 20);
        add(uploadLabel);

        JTextField uploadField = new JTextField();
        uploadField.setBounds(20, y + 25, 330, 35);
        uploadField.setEditable(false);
        add(uploadField);

        // EXTRA GROẞER BUTTON – NICHTS ABGESCHNITTEN
        JButton uploadButton = new JButton("Datei auswählen (Upload)");
        uploadButton.setBounds(360, y + 25, 220, 40); // großer Button
        uploadButton.setBackground(new Color(0, 102, 204));
        uploadButton.setForeground(Color.WHITE);
        uploadButton.setFont(new Font("Arial", Font.BOLD, 12));
        uploadButton.setBorderPainted(false);
        uploadButton.setOpaque(true);
        add(uploadButton);

        uploadButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("NDA auswählen");

            int result = chooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                uploadField.setText(file.getAbsolutePath());
            }
        });

        y += 100;

        // ===== Absenden =====
        JButton submitButton = new JButton("Absenden");
        submitButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Ihre Angaben wurden erfolgreich gespeichert!",
                "Erfolg",
                JOptionPane.INFORMATION_MESSAGE
            );

            new DashboardStudent(4711);   // ✅ Zurück zum Dashboard
            SwingUtilities.getWindowAncestor(this).dispose(); // ✅ Aktuelles Fenster schließen
        });
        
        
        submitButton.setBounds(20, y, 150, 40);
        submitButton.setBackground(new Color(0, 70, 160));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorderPainted(false);
        submitButton.setOpaque(true);
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        add(submitButton);
        
        // ===== ZURÜCK BUTTON =====
        JButton backBtn = new JButton("Zurück");
        backBtn.setBounds(20, 650, 120, 35);
        backBtn.setBackground(new Color(0, 102, 204));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBorderPainted(false);
        backBtn.setOpaque(true);
        add(backBtn);

        // Fenster schließen → zurück zum Dashboard
        
        backBtn.addActionListener(e -> {
            new DashboardStudent(4711);   // ✅ Dashboard NEU öffnen
            SwingUtilities.getWindowAncestor(this).dispose(); // ✅ dieses Fenster schließen
        }); 
    }

    // ===========================================
    // Textfeld + Bearbeiten-Button
    // ===========================================
    private void addField(String labelName, int y) {

        JLabel label = new JLabel(labelName);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setBounds(20, y, 200, 20);
        add(label);

        JTextField field = new JTextField();
        field.setBounds(20, y + 20, 330, 35);
        add(field);

        JButton editButton = new JButton("Bearbeiten");
        editButton.setBounds(360, y + 20, 220, 35);   // breiter → VOLL sichtbar
        editButton.setBackground(new Color(0, 102, 204));
        editButton.setForeground(Color.WHITE);
        editButton.setBorderPainted(false);
        editButton.setOpaque(true);
        add(editButton);
    }

    // ===== Test =====
    public static void main(String[] args) {
        JFrame frame = new JFrame("Formular");
        frame.setSize(620, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new AllgemeineInformationenStudent());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
    
    }
   
}
