package GUIKlassen;

import javax.swing.*;
import java.awt.*;

public class AllgemeineInformationen extends JFrame {

    public AllgemeineInformationen() {
        setTitle("Allgemeine Informationen");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        JPanel formPanel = new JPanel(null);
        formPanel.setBounds(50, 30, 800, 400);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        add(formPanel);

        JLabel titleLabel = new JLabel("Allgemeine Informationen", SwingConstants.CENTER);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(0, 45, 150));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(0, 0, 800, 50);
        formPanel.add(titleLabel);

        // Felder mit Rahmen
        createField(formPanel, "Thema:", 70);
        createField(formPanel, "Unternehmen / Ort:", 120);
        createField(formPanel, "Zeitraum:", 170);
        createField(formPanel, "Betreuer Hochschule:", 220);
        createField(formPanel, "Betreuer Unternehmen:", 270);

        JLabel ndaLabel = new JLabel("NDA nötig?");
        ndaLabel.setBounds(20, 320, 120, 25);
        ndaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(ndaLabel);

        JCheckBox ndaCheckbox = new JCheckBox();
        ndaCheckbox.setBounds(140, 320, 20, 25);
        formPanel.add(ndaCheckbox);

        JButton uploadBtn = new JButton("Upload NDA");
        uploadBtn.setBounds(180, 320, 120, 25);
        uploadBtn.setBackground(new Color(0, 45, 150));
        uploadBtn.setForeground(Color.WHITE);
        uploadBtn.setFocusPainted(false);
        uploadBtn.setOpaque(true);
        uploadBtn.setBorderPainted(false);
        formPanel.add(uploadBtn);

        JButton submitBtn = new JButton("Absenden");
        submitBtn.setBounds(650, 320, 120, 35);
        submitBtn.setBackground(new Color(0, 45, 150));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Arial", Font.BOLD, 14));
        submitBtn.setFocusPainted(false);
        submitBtn.setOpaque(true);
        submitBtn.setBorderPainted(false);
        formPanel.add(submitBtn);

        submitBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Informationen gespeichert!"));

        setVisible(true);
    }

    private void createField(JPanel panel, String labelText, int yPosition) {
        // Container für Feld + Stift
        JPanel fieldPanel = new JPanel(null);
        fieldPanel.setBounds(20, yPosition, 760, 35);
        fieldPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // dünner schwarzer Rand
        panel.add(fieldPanel);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setBounds(5, 5, 150, 25);
        fieldPanel.add(label);

        JTextField textField = new JTextField();
        textField.setBounds(160, 5, 580, 25);
        textField.setEditable(false);
        fieldPanel.add(textField);

        ImageIcon icon = new ImageIcon("stift.png");
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        JButton editBtn = new JButton(icon);
        editBtn.setBounds(600, 5, 25, 25);
        editBtn.setContentAreaFilled(false);
        editBtn.setBorderPainted(false);
        fieldPanel.add(editBtn);

        editBtn.addActionListener(e -> textField.setEditable(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AllgemeineInformationen::new);
    }
}
