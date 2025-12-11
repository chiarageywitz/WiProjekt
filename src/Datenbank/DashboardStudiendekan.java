package Datenbank;

import javax.swing.*;
import java.awt.*;

public class DashboardStudiendekan extends JFrame {

    public DashboardStudiendekan() {
        setTitle("Übersicht vom Studiendekan");
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        Color hftRed = new Color(204, 0, 0);
        Color hftBlue = new Color(0, 102, 204);

        // =======================
        // TOP TITEL
        // =======================
        JLabel title = new JLabel("Übersicht vom Studiendekan");
        title.setBounds(50, 20, 600, 40);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(hftRed);
        add(title);

        // =======================
        // AUSLOGGEN BUTTON
        // =======================
        JButton logoutBtn = new JButton("Ausloggen");
        logoutBtn.setBounds(1000, 20, 130, 40);
        logoutBtn.setBackground(hftRed);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setOpaque(true);
        add(logoutBtn);

        logoutBtn.addActionListener(e -> {
            new LoginFenster();
            dispose();
        });

        // =======================
        // STUDENTEN SUCHE
        // =======================
        JPanel searchPanel = createBox("Studenten - Suche", 50, 100);
        JTextField searchField = new JTextField("Search");
        searchPanel.add(searchField);
        add(searchPanel);

        // =======================
        // NOTENLISTE
        // =======================
        JPanel notenPanel = createBox("Notenliste", 350, 100);

        JButton openNotenBtn = new JButton("Hier klicken um die Notenliste zu öffnen");
        notenPanel.add(openNotenBtn);
        add(notenPanel);

        // =======================
        // MEINE FUNKTIONEN
        // =======================
        JPanel funktionenPanel = createBox("Meine Funktionen", 650, 100);

        JButton genehmigungBtn = new JButton("Genehmigung erstellen");
        genehmigungBtn.setBackground(hftBlue);
        genehmigungBtn.setForeground(Color.WHITE);
        genehmigungBtn.setBorderPainted(false);
        genehmigungBtn.setOpaque(true);

        JButton notenBtn = new JButton("Noteneingabe");
        notenBtn.setBackground(hftBlue);
        notenBtn.setForeground(Color.WHITE);
        notenBtn.setBorderPainted(false);
        notenBtn.setOpaque(true);

        funktionenPanel.add(genehmigungBtn);
        funktionenPanel.add(Box.createVerticalStrut(20));
        funktionenPanel.add(notenBtn);

        add(funktionenPanel);
        
        

        // =======================
        // PORTAL BENACHRICHTIGUNGEN
        // =======================
        JPanel portalPanel = createBox("Portal - Benachrichtigungen",880, 100);

        JTextArea portalArea = new JTextArea();
        portalArea.setEditable(false);
        portalPanel.add(new JScrollPane(portalArea));

        add(portalPanel);

        setVisible(true);
    }

    // =======================
    // BOX HELFERMETHODE
    // =======================
    private JPanel createBox(String title, int x, int y) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBounds(x, y, 250, 300);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(new Color(204, 0, 0));
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setMaximumSize(new Dimension(250, 40));

        panel.add(label);
        panel.add(Box.createVerticalStrut(20));

        return panel;
    }

    public static void main(String[] args) {
    	new DashboardStudiendekan();
    }
}
