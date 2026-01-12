package GUIKlassen;

import javax.swing.*;
import java.awt.*;

/**
 * Dialogfenster zur Anzeige einer Erfolgsmeldung.
 *
 * Der Dialog informiert den Nutzer darüber, dass eine Aktion erfolgreich
 * abgeschlossen wurde und bietet die Möglichkeit, zum vorherigen Bereich
 * zurückzukehren.
 */
public class SuccessDialog extends JDialog {

    /**
     * Erstellt ein modales Erfolgsdialogfenster.
     *
     * Der Dialog wird relativ zum übergebenen Hauptfenster positioniert
     * und blockiert die Eingabe, bis er geschlossen wird.
     *
     * @param parent übergeordnetes Hauptfenster
     */
    public SuccessDialog(JFrame parent) {
        super(parent, "Erfolg", true);
        setSize(500, 350);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel iconLabel = new JLabel("✔");
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 60));
        iconLabel.setForeground(new Color(34, 177, 76));
        mainPanel.add(iconLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel titleLabel = new JLabel("Deine Daten wurden weiter verschickt!");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(44, 62, 80));
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel subtitleLabel = new JLabel("Ihre Informationen wurden erfolgreich gesendet.");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        mainPanel.add(subtitleLabel);

        mainPanel.add(Box.createVerticalGlue());

        JButton dashboardButton = new JButton("Zurück zum Dashboard");
        dashboardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashboardButton.setFocusPainted(false);
        dashboardButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        dashboardButton.setForeground(Color.WHITE);
        dashboardButton.setBackground(new Color(30, 136, 229));
        dashboardButton.setPreferredSize(new Dimension(220, 40));
        dashboardButton.setMaximumSize(new Dimension(220, 40));
        dashboardButton.addActionListener(e -> dispose());

        mainPanel.add(dashboardButton);
        add(mainPanel);
    }
}
