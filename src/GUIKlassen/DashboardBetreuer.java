package GUIKlassen;

import Datenbank.StudentDAO;
import Datenbank.StudentDAO.StudentInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

public class DashboardBetreuer extends JFrame {

    private DefaultListModel<StudentInfo> studentModel;
    private JList<StudentInfo> studentList;

    public DashboardBetreuer() {
        setTitle("Betreuer-Übersicht");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color hftRed = new Color(204, 0, 0);
        Color hftBlue = new Color(0, 102, 204);
        Color panelBg = new Color(245, 245, 245);

        // ================= TOP PANEL =================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton logoutBtn = new JButton("Ausloggen");
        logoutBtn.setBackground(hftRed);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setPreferredSize(new Dimension(120, 40));

        logoutBtn.addActionListener(e -> {
            new LoginFenster();
            dispose();
        });

        JLabel topLabel = new JLabel("Betreuer-Portal");
        topLabel.setFont(new Font("Arial", Font.BOLD, 22));

        topPanel.add(topLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ================= MAIN PANEL =================
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(panelBg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // ================= STUDENTENSUCHE =================
        JPanel studentBox = createBoxPanel("Studentensuche", hftRed);

        JTextField searchField = new JTextField("Suche");
        searchField.setForeground(Color.GRAY);
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Suche")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Suche");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        JButton suchenBtn = new JButton("Suchen");

        studentModel = new DefaultListModel<>();
        studentList = new JList<>(studentModel);
        JScrollPane listScroll = new JScrollPane(studentList);

        suchenBtn.addActionListener(e -> sucheStudenten(searchField.getText()));

        studentBox.add(searchField);
        studentBox.add(Box.createVerticalStrut(10));
        studentBox.add(suchenBtn);
        studentBox.add(Box.createVerticalStrut(10));
        studentBox.add(listScroll);

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(studentBox, gbc);

        // ================= MEINE FUNKTIONEN =================
        JPanel funktionenBox = createBoxPanel("Meine Funktionen", hftRed);

        JButton noteneingabeBtn = createBlueButton("Noteneingabe", hftBlue);
        JButton freigabeBtn = createBlueButton("Freigabe der Arbeit", hftBlue);

        noteneingabeBtn.setMaximumSize(new Dimension(240, 70));
        freigabeBtn.setMaximumSize(new Dimension(240, 70));

        noteneingabeBtn.addActionListener(e -> öffneNoteneingabe());

        funktionenBox.add(Box.createVerticalGlue());
        funktionenBox.add(noteneingabeBtn);
        funktionenBox.add(Box.createVerticalStrut(20));
        funktionenBox.add(freigabeBtn);
        funktionenBox.add(Box.createVerticalGlue());

        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(funktionenBox, gbc);

        // ================= PORTAL =================
        JPanel portalBox = createBoxPanel("Portal-Benachrichtigungen", hftRed);
        JTextArea portalArea = new JTextArea();
        portalArea.setEditable(false);
        portalBox.add(new JScrollPane(portalArea));

        gbc.gridx = 2;
        gbc.gridy = 0;
        mainPanel.add(portalBox, gbc);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    // ================= LOGIK =================
    private void sucheStudenten(String name) {
        try {
            studentModel.clear();
            List<StudentInfo> studenten = StudentDAO.sucheStudenten(name);
            for (StudentInfo s : studenten) {
                studentModel.addElement(s);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Fehler bei der Suche");
        }
    }

    private void öffneNoteneingabe() {
        StudentInfo student = studentList.getSelectedValue();
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Bitte zuerst einen Studenten auswählen!");
            return;
        }
        new Noteneingabe(student.mnr, "betreuer");
        dispose();
    }

    // ================= HILFSMETHODEN =================
    private JPanel createBoxPanel(String title, Color headerColor) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        box.setBackground(Color.WHITE);

        JLabel label = new JLabel(title);
        label.setOpaque(true);
        label.setBackground(headerColor);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(label);
        box.add(Box.createVerticalStrut(10));

        Dimension size = new Dimension(280, 400);
        box.setPreferredSize(size);
        box.setMaximumSize(size);

        return box;
    }

    private JButton createBlueButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    public static void main(String[] args) {
        new DashboardBetreuer();
    }
}



