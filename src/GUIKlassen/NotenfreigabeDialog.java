package GUIKlassen;

import Datenbank.NotenDAO;
import javax.swing.*;
import java.awt.*;

/**
 * GUI für die Freigabe von Noten nach Ablauf der Notenfrist.
 * Nur für Studiendekan verfügbar.
 */
public class NotenfreigabeDialog extends JFrame {

    public NotenfreigabeDialog() {
        setTitle("Notenfreigabe");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("Noten nach Notenfrist freigeben");
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(220, 53, 69));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel infoLabel = new JLabel("<html><center>" +
            "Durch die Freigabe werden alle eingetragenen Noten<br>" +
            "an die Studenten verschickt.<br><br>" +
            "Diese Aktion kann nicht rückgängig gemacht werden.<br><br>" +
            "Möchten Sie fortfahren?" +
            "</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(infoLabel);

        add(centerPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnFreigeben = new JButton("Noten freigeben");
        btnFreigeben.setBackground(new Color(40, 167, 69));
        btnFreigeben.setForeground(Color.WHITE);
        btnFreigeben.setFocusPainted(false);
        btnFreigeben.setPreferredSize(new Dimension(180, 45));

        JButton btnAbbrechen = new JButton("Abbrechen");
        btnAbbrechen.setBackground(Color.GRAY);
        btnAbbrechen.setForeground(Color.WHITE);
        btnAbbrechen.setFocusPainted(false);
        btnAbbrechen.setPreferredSize(new Dimension(140, 45));

        buttonPanel.add(btnFreigeben);
        buttonPanel.add(btnAbbrechen);
        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        btnFreigeben.addActionListener(e -> notenFreigeben());
        btnAbbrechen.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void notenFreigeben() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Sind Sie sicher, dass Sie die Noten jetzt freigeben möchten?\n" +
            "Alle Studenten werden per Benachrichtigung über ihre Note informiert.",
            "Bestätigung erforderlich",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                NotenDAO.notenFreigeben();
                
                JOptionPane.showMessageDialog(this,
                    "Die Noten wurden erfolgreich freigegeben.\n" +
                    "Alle betroffenen Studenten wurden benachrichtigt.",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Fehler beim Freigeben der Noten: " + ex.getMessage(),
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
