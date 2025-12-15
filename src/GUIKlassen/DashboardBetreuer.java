package GUIKlassen;

import javax.swing.*;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class DashboardBetreuer extends JFrame {

    public DashboardBetreuer() {
        setTitle("Betreuer-Übersicht");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color hftRed = new Color(204, 0, 0);
        Color hftBlue = new Color(0, 102, 204);
        Color panelBg = new Color(245, 245, 245);

        // ---------------- TOP PANEL ----------------
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton logoutBtn = new JButton("Ausloggen");
        logoutBtn.addActionListener(e -> {
            new LoginFenster();   // Login wieder öffnen
            dispose();            // Betreuer-Dashboard schließen
        });
        
        logoutBtn.setBackground(hftRed);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setOpaque(true);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setPreferredSize(new Dimension(120, 40));

        JLabel topLabel = new JLabel("Betreuer-Portal");
        topLabel.setFont(new Font("Arial", Font.BOLD, 22));
        topLabel.setForeground(Color.BLACK);

        topPanel.add(topLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ---------------- MAIN PANEL ----------------
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(panelBg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // ---------------- STUDENTENSUCHE ----------------
        JPanel studentBox = createBoxPanel("Studentensuche", hftRed);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Emoji Lupe
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        searchIcon.setPreferredSize(new Dimension(50, 50));
        searchIcon.setHorizontalAlignment(SwingConstants.CENTER);
        searchIcon.setVerticalAlignment(SwingConstants.CENTER);
        searchPanel.add(searchIcon, BorderLayout.WEST);

        JTextField searchField = new JTextField("Suche");
        searchField.setForeground(Color.GRAY);
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Suche")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Suche");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);

        searchPanel.setMaximumSize(new Dimension(240, 50));
        searchPanel.setPreferredSize(new Dimension(240, 50));

        studentBox.add(Box.createVerticalGlue());
        studentBox.add(searchPanel);
        studentBox.add(Box.createVerticalGlue());

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(studentBox, gbc);

        // ---------------- MEINE FUNKTIONEN ----------------
        JPanel funktionenBox = createBoxPanel("Meine Funktionen", hftRed);

        JButton noteneingabeBtn = createBlueButton("Noteneingabe", hftBlue);
        JButton freigabeBtn = createBlueButton("Freigabe der Arbeit", hftBlue);

        // Buttons größer machen
        noteneingabeBtn.setMaximumSize(new Dimension(240, 70));
        freigabeBtn.setMaximumSize(new Dimension(240, 70));

        funktionenBox.setLayout(new BoxLayout(funktionenBox, BoxLayout.Y_AXIS));
        funktionenBox.add(Box.createVerticalGlue());
        funktionenBox.add(noteneingabeBtn);
        funktionenBox.add(Box.createVerticalStrut(20));
        funktionenBox.add(freigabeBtn);
        funktionenBox.add(Box.createVerticalGlue());

        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(funktionenBox, gbc);

        // ---------------- PORTAL-BENACHRICHTIGUNGEN ----------------
        JPanel portalBox = createBoxPanel("Portal-Benachrichtigungen", hftRed);

        JTextArea portalArea = new JTextArea();
        portalArea.setEditable(false);
        portalArea.setLineWrap(true);
        portalArea.setWrapStyleWord(true);
        JScrollPane portalScroll = new JScrollPane(portalArea);
        portalScroll.setPreferredSize(new Dimension(200, 150));

        portalBox.add(portalScroll);

        gbc.gridx = 2;
        gbc.gridy = 0;
        mainPanel.add(portalBox, gbc);

        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createBoxPanel(String title, Color headerColor) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        box.setBackground(Color.WHITE);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setOpaque(true);
        label.setBackground(headerColor);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(label);
        box.add(Box.createVerticalStrut(10));

        Dimension boxSize = new Dimension(280, 400);
        box.setPreferredSize(boxSize);
        box.setMinimumSize(boxSize);
        box.setMaximumSize(boxSize);

        return box;
    }

    private JButton createBlueButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    public static void main(String[] args) {
        new DashboardBetreuer();
    }
}
