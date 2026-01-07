package GUIKlassen;

import Datenbank.StudentDAO;
import Datenbank.StudentDAO.StudentInfo;
import Util.UIColors;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * JFrame-Klasse für die Studenten-Suche.
 */
public class StudentenSucheView extends JFrame {

    private DefaultListModel<StudentInfo> studentModel;
    private JList<StudentInfo> studentList;

    public StudentenSucheView() {
        setTitle("Studenten-Suche");
        setSize(BaseFrame.WIDTH, BaseFrame.HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color hftRed = UIColors.HFT_RED;
        Color panelBg = UIColors.BACKGROUND;

        // ================= HEADER =================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Studenten-Suche");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JButton logoutBtn = new JButton("Ausloggen");
        logoutBtn.setBackground(hftRed);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            new LoginFenster();
            dispose();
        });

        header.add(title, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ================= MAIN =================
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBackground(panelBg);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== LINKE SEITE: STUDENTENSUCHE =====
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(panelBg);

        JTextField searchField = new JTextField();
        JButton suchenBtn = new JButton("Suchen");
        suchenBtn.setPreferredSize(new Dimension(120, 30));
        suchenBtn.setBackground(UIColors.PRIMARY_BLUE);
        suchenBtn.setForeground(Color.WHITE);
        suchenBtn.setFocusPainted(false);
        suchenBtn.setBorderPainted(false);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(panelBg);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(suchenBtn, BorderLayout.EAST);

        studentModel = new DefaultListModel<>();
        studentList = new JList<>(studentModel);
        JScrollPane scrollPane = new JScrollPane(studentList);

        JButton weiterBtn = new JButton("Weiter");
        weiterBtn.setBackground(hftRed);
        weiterBtn.setForeground(Color.WHITE);
        weiterBtn.setFocusPainted(false);
        weiterBtn.setPreferredSize(new Dimension(120, 40));

        suchenBtn.addActionListener(e -> sucheStudenten(searchField.getText()));
        weiterBtn.addActionListener(e -> öffneDashboard());

        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(weiterBtn, BorderLayout.SOUTH);

        // ===== RECHTE SEITE: PORTAL-BENACHRICHTIGUNGEN =====
        JPanel portalBox = createBoxPanel("Portal-Benachrichtigungen", hftRed);
        JTextArea portalArea = new JTextArea();
        portalArea.setEditable(false);
        portalBox.add(new JScrollPane(portalArea));

        // ===== ZUSAMMENBAU =====
        mainPanel.add(leftPanel);
        mainPanel.add(portalBox);

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

    private void öffneDashboard() {
        StudentInfo student = studentList.getSelectedValue();
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Bitte zuerst einen Studenten auswählen!");
            return;
        }
        new DashboardBetreuerView(student);
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
        return box;
    }

    public static void main(String[] args) {
        new StudentenSucheView();
    }
}
