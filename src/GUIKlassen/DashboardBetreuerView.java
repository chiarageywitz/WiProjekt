package GUIKlassen;

import Datenbank.StudentDAO.StudentInfo;

import javax.swing.*;
import java.awt.*;

public class DashboardBetreuerView extends JFrame {

    private StudentInfo student;

    public DashboardBetreuerView(StudentInfo student) {
        this.student = student;

        setTitle("Betreuer-Übersicht");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color hftRed = new Color(204, 0, 0);
        Color hftBlue = new Color(0, 102, 204);
        Color panelBg = new Color(245, 245, 245);

        // ================= TOP =================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel topLabel = new JLabel(
                "Student: " + student.name +
                " | Thema: " + (student.thema != null ? student.thema : "—")
        );
        topLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton logoutBtn = new JButton("Ausloggen");
        logoutBtn.setBackground(hftRed);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);

        logoutBtn.addActionListener(e -> {
            new StudentenSucheView();
            dispose();
        });

        topPanel.add(topLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ================= MAIN =================
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBackground(panelBg);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== Funktionen =====
        JPanel funktionenBox = createBoxPanel("Meine Funktionen", hftRed);

        JButton noteneingabeBtn = createBlueButton("Noteneingabe", hftBlue);
        JButton freigabeBtn = createBlueButton("Freigabe der Arbeit", hftBlue);

        noteneingabeBtn.addActionListener(e ->
                new Noteneingabe(student, "betreuer")
        );

        funktionenBox.add(Box.createVerticalGlue());
        funktionenBox.add(noteneingabeBtn);
        funktionenBox.add(Box.createVerticalStrut(20));
        funktionenBox.add(freigabeBtn);
        funktionenBox.add(Box.createVerticalGlue());

        // ===== Portal =====
        JPanel portalBox = createBoxPanel("Portal-Benachrichtigungen", hftRed);
        JTextArea portalArea = new JTextArea();
        portalArea.setEditable(false);
        portalBox.add(new JScrollPane(portalArea));

        mainPanel.add(funktionenBox);
        mainPanel.add(portalBox);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
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
        return box;
    }

    private JButton createBlueButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(240, 60));
        return btn;
    }
}
