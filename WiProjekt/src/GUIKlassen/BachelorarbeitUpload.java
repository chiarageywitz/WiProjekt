package GUIKlassen;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class BachelorarbeitUpload extends JPanel {

    private static final long serialVersionUID = 1L;

    public BachelorarbeitUpload() {

        setLayout(null);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));

        // ===== Titel =====
        JLabel title = new JLabel("Bachelorarbeit");
        title.setOpaque(true);
        title.setBackground(new Color(0, 102, 204)); // Blau wie im Beispiel
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(10, 0, 150, 35);
        add(title);

        int y = 55;

        // ===== Datei auswählen Titel =====
        JLabel fileTitle = new JLabel("Datei auswählen (jpg, docx, pdf, usw)");
        fileTitle.setFont(new Font("Arial", Font.PLAIN, 13));
        fileTitle.setBounds(20, y, 300, 20);
        add(fileTitle);

        y += 35;

        // ===== Upload-Feld =====
        JPanel dropPanel = new JPanel(null);
        dropPanel.setBounds(20, y, 500, 120);
        dropPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        dropPanel.setBackground(Color.WHITE);
        add(dropPanel);

        // Icon + Text
        JLabel uploadLabel = new JLabel("<html><center>⬆️<br>Datei auswählen<br><span style='font-size:10px;'>Drag and drop files here</span></center></html>", SwingConstants.CENTER);
        uploadLabel.setBounds(0, 0, 500, 120);
        uploadLabel.setForeground(Color.GRAY);
        dropPanel.add(uploadLabel);

        // Unsichtbarer File Chooser Button
        JButton chooseBtn = new JButton();
        chooseBtn.setBounds(0, 0, 500, 120);
        chooseBtn.setOpaque(false);
        chooseBtn.setContentAreaFilled(false);
        chooseBtn.setBorderPainted(false);
        dropPanel.add(chooseBtn);

        // Datei auswählen
        chooseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Datei auswählen");

            int result = chooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                uploadLabel.setText("<html><center><b>" + file.getName() + "</b></center></html>");
            }
        });

        y += 150;

        // ===== Beschreibung =====
        JLabel beschrLabel = new JLabel("Beschreibung");
        beschrLabel.setFont(new Font("Arial", Font.PLAIN, 13)); 
        beschrLabel.setBounds(20, y, 200, 20);
        add(beschrLabel);

        y += 30;

        JTextArea beschreibung = new JTextArea();
        beschreibung.setLineWrap(true);
        beschreibung.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(beschreibung);
        scroll.setBounds(20, y, 500, 120);
        add(scroll);

        y += 150;

        // ===== Hochladen Button =====
        JButton uploadBtn = new JButton("Hochladen");
        uploadBtn.setBounds(200, y, 150, 40);
        uploadBtn.setBackground(new Color(0, 102, 204));
        uploadBtn.setForeground(Color.WHITE);
        uploadBtn.setFont(new Font("Arial", Font.BOLD, 14));
        uploadBtn.setBorderPainted(false);
        uploadBtn.setOpaque(true);
        add(uploadBtn);
    }

    // Test
    public static void main(String[] args) {
        JFrame frame = new JFrame("Bachelorarbeit Upload");
        frame.setSize(560, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new BachelorarbeitUpload());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
