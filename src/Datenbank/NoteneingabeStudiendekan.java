package Datenbank;

import javax.swing.*;
import java.awt.*;

public class NoteneingabeStudiendekan extends JFrame {

    public NoteneingabeStudiendekan() {

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Button.background", new Color(44, 120, 206));
            UIManager.put("Button.foreground", Color.WHITE);
        } catch (Exception e) { }

        setTitle("Noteneingabe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 300);
        setLocationRelativeTo(null);

        JPanel main = new JPanel();
        main.setLayout(null);
        add(main);

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(44, 120, 206));
        header.setBounds(10, 10, 150, 35);

        JLabel headerLabel = new JLabel("Noteneingabe");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        header.add(headerLabel);
        main.add(header);

        int y = 60;

        JLabel themaLabel = new JLabel("Thema:");
        themaLabel.setBounds(20, y, 200, 25);
        main.add(themaLabel);

        JTextField themaField = new JTextField();
        themaField.setBounds(20, y + 25, 640, 30);
        main.add(themaField);

        y += 70;

        JLabel noteLabel = new JLabel("Note Bachelorseminar:");
        noteLabel.setBounds(20, y, 250, 25);
        main.add(noteLabel);

        JTextField noteField = new JTextField();
        noteField.setBounds(20, y + 25, 640, 30);
        main.add(noteField);

        y += 80;

        // ABSENDEN Button – jetzt zu 100% blau
        JButton send = new JButton("Absenden");
        send.setBounds(20, y, 120, 35);
        send.setFont(new Font("Arial", Font.BOLD, 13));

        main.add(send);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new NoteneingabeStudiendekan().setVisible(true);
        });
    }
}