package GUIKlassen;

import javax.swing.*;
import java.awt.*;

/**
 * Fenster zur Anzeige einer Erfolgsmeldung nach der Abgabe einer
 * Bachelorarbeit.
 */
public class AbgabeErfolgreichFenster extends JDialog {

	/**
	 * onstruktor für das Erfolgsfenster.
	 * 
	 * @param parent Eltern-Fenster
	 * @param message Erfolgsmeldungstext
	 */
	public AbgabeErfolgreichFenster(Window parent, String message) {
		super(parent);
		setUndecorated(true);
		setSize(400, 100);
		setLocationRelativeTo(parent);

		JPanel panel = new JPanel();
		panel.setBackground(new Color(0, 102, 204));
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		panel.setLayout(new BorderLayout());

		JLabel label = new JLabel(message, SwingConstants.CENTER);
		label.setForeground(Color.WHITE);
		label.setFont(new Font("Arial", Font.BOLD, 14));
		panel.add(label, BorderLayout.CENTER);

		add(panel);
		setVisible(true);

		// Timer: Fenster nach 2 Sekunden automatisch schließen
		new Timer(2000, e -> dispose()).start();
	}
}
