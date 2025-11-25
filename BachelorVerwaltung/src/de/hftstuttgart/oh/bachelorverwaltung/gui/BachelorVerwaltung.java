package de.hftstuttgart.oh.bachelorverwaltung.gui;

import de.hftstuttgart.oh.bachelorverwaltung.dbaccess.DBAccess;
import de.hftstuttgart.oh.bachelorverwaltung.businessobjects.Student;

import javax.swing.*;           // JFrame, JButton, JTextField, JLabel, JOptionPane
import java.awt.*;              // Layouts
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BachelorVerwaltung extends JFrame {

    // ► GUI-Bausteine (Felder der Klasse)
    private JTextField tf_MNR;
    private JLabel l_Vorname;
    private JLabel l_Nachname;
    private JButton b_Suchen;

    // ► Konstruktor – hier bauen wir das Fenster zusammen
    public BachelorVerwaltung() {
        super("Bachelorverwaltung");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tf_MNR = new JTextField(10);
        l_Vorname = new JLabel("-");
        l_Nachname = new JLabel("-");
        b_Suchen = new JButton("Suchen");

        // ► Klick auf „Suchen“
        b_Suchen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int mnr = Integer.parseInt(tf_MNR.getText());
                    Student student = DBAccess.getStudentByMNR(mnr);

                    if (student != null) {
                        l_Vorname.setText(student.getVorname());
                        l_Nachname.setText(student.getNachname());
                    } else {
                        l_Vorname.setText("Not found!");
                        l_Nachname.setText("");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            BachelorVerwaltung.this,
                            "Bitte eine gültige Matrikelnummer eingeben."
                    );
                }
            }
        });

        // ► Layout für das Fenster
        JPanel center = new JPanel(new GridLayout(3, 2));
        center.add(new JLabel("Matrikelnummer:"));
        center.add(tf_MNR);
        center.add(new JLabel("Vorname:"));
        center.add(l_Vorname);
        center.add(new JLabel("Nachname:"));
        center.add(l_Nachname);

        JPanel bottom = new JPanel();
        bottom.add(b_Suchen);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(center, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null); // Fenster in die Mitte
    }

    // ► Startpunkt des Programms
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BachelorVerwaltung().setVisible(true);
        });
    }
}


