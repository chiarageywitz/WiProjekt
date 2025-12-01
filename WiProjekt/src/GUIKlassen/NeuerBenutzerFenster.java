package GUIKlassen;

import javax.swing.*;
import java.awt.*;

public class NeuerBenutzerFenster extends JFrame {

    public NeuerBenutzerFenster() {
        setTitle("Neuer Benutzer - Hochschule");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        // =======================
        // Roter Header ähnlich DashboardStudent
        // =======================
        JLabel header = new JLabel("Neuen Benutzer erstellen", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(220, 53, 69));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBounds(0, 20, 500, 40);
        add(header);

        // =======================
        // Formularfelder
        // =======================
        JTextField nameField = createLabeledField("Benutzername:", 80);
        JPasswordField passField = (JPasswordField) createLabeledField("Passwort:", 130, true);
        JTextField emailField = createLabeledField("E-Mail:", 180);
        JTextField matrikelField = createLabeledField("Matrikelnummer:", 230);

        // =======================
        // Erstellen Button
        // =======================
        JButton createButton = new JButton("Benutzer erstellen");
        createButton.setBounds(160, 300, 180, 35);
        createButton.setBackground(new Color(0, 45, 150));
        createButton.setForeground(Color.WHITE);
        createButton.setFont(new Font("Arial", Font.BOLD, 14));
        createButton.setFocusPainted(false);
        createButton.setOpaque(true);
        createButton.setBorderPainted(false);
        add(createButton);

        createButton.addActionListener(e -> {
            String user = nameField.getText();
            String pass = new String(passField.getPassword());
            String email = emailField.getText();
            String matrikel = matrikelField.getText();

            JOptionPane.showMessageDialog(null,
                    "Benutzer erstellt:\nName: " + user +
                            "\nE-Mail: " + email +
                            "\nMatrikelnummer: " + matrikel);
            dispose(); // Fenster schließen
        });

        setVisible(true);
    }

    // =======================
    // Hilfsmethode für Formularfelder
    // =======================
    private JTextField createLabeledField(String labelText, int yPos) {
        return createLabeledField(labelText, yPos, false);
    }

    private JTextField createLabeledField(String labelText, int yPos, boolean isPassword) {
        JPanel panel = new JPanel(null);
        panel.setBounds(100, yPos, 300, 30);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // dünner Rahmen
        add(panel);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setBounds(5, 5, 120, 20);
        panel.add(label);

        JTextField textField;
        if (isPassword) {
            textField = new JPasswordField();
        } else {
            textField = new JTextField();
        }
        textField.setBounds(130, 5, 160, 20);
        panel.add(textField);

        return textField;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NeuerBenutzerFenster::new);
    }
}
