package GUIKlassen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import Datenbank.UserDAO;
import Datenbank.UserLoginResult;
import Util.UIColors;
import Util.UIImageLoader;

/**
 * GUI-Fenster für den Login der Benutzer. Benutzer können sich mit E-Mail und
 * Passwort einloggen. Abhängig von der Rolle (Student, Betreuer, Dekan) wird
 * das jeweilige Dashboard geöffnet.
 * 
 */
public class LoginFenster extends JFrame {

	/**
	 * Konstruktor für das Login-Fenster. rstellt das GUI mit Feldern für E-Mail und
	 * Passwort, Login-Button sowie einem Link für neue Benutzer. Enthält die
	 * Login-Logik.
	 */
	public LoginFenster() {
		setTitle("Login - Hochschule");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 400);
		setLocationRelativeTo(null);
		setLayout(null);

		getContentPane().setBackground(UIColors.BACKGROUND);

		// Logo
		add(UIImageLoader.createLogoLabel(150, 20, 200, 100));

		int windowWidth = 500;
		int formWidth = 300;
		int labelWidth = 100;
		int fieldWidth = 180;

		int startX = (windowWidth - formWidth) / 2;
		int labelX = startX;
		int fieldX = startX + labelWidth + 10;

		// E-Mail
		JLabel userLabel = new JLabel("E-Mail:");
		userLabel.setFont(new Font("Arial", Font.BOLD, 14));
		int y = 150;
		userLabel.setBounds(labelX, y, labelWidth, 25);
		add(userLabel);

		JTextField userField = new JTextField();
		userField.setBounds(fieldX, y, fieldWidth, 25);
		add(userField);

		// Passwort
		JLabel passLabel = new JLabel("Passwort:");
		passLabel.setFont(new Font("Arial", Font.BOLD, 14));
		y += 40;
		passLabel.setBounds(labelX, y, labelWidth, 25);
		add(passLabel);

		JPasswordField passField = new JPasswordField();
		passField.setBounds(fieldX, y, fieldWidth, 25);
		add(passField);

		// Login Button
		JButton loginButton = new JButton("Einloggen");
		y += 60;
		loginButton.setBounds(startX + (formWidth - 140) / 2, y, 140, 35);
		loginButton.setBackground(UIColors.PRIMARY_BLUE);
		loginButton.setForeground(UIColors.TEXT_WHITE);
		loginButton.setFont(new Font("Arial", Font.BOLD, 14));
		loginButton.setFocusPainted(false);
		loginButton.setBorderPainted(false);
		add(loginButton);

		// Neuer Benutzer
		JLabel newUserLabel = new JLabel("<HTML><U>Neuer Benutzer?</U></HTML>");
		newUserLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		newUserLabel.setForeground(UIColors.PRIMARY_BLUE);
		y += 45;
		newUserLabel.setBounds(startX + (formWidth - newUserLabel.getPreferredSize().width) / 2, y,
				newUserLabel.getPreferredSize().width, 25);
		newUserLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		add(newUserLabel);

		// LOGIN LOGIK
		loginButton.addActionListener(e -> {
			String email = userField.getText();
			String pass = new String(passField.getPassword());

			try {
				UserLoginResult result = UserDAO.login(email, pass);

				if (result == null) {
					JOptionPane.showMessageDialog(this, "E-Mail oder Passwort falsch!");
					return;
				}

				String rolle = result.getRolle().toLowerCase();
				int mnr = result.getMnr();

				// ❗ KLASSISCHES SWITCH (WICHTIG)
				switch (rolle) {
				case "student":
					new DashboardStudent(mnr);
					break;

				case "betreuer":
					new StudentenSucheView();
					break;

				case "dekan":
					new DashboardStudiendekan(null);
					break;

				default:
					JOptionPane.showMessageDialog(this, "Unbekannte Rolle!");
				}

				dispose();

			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Fehler beim Login!");
			}
		});

		newUserLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
				new NeuerBenutzerFenster();
			}
		});

		setVisible(true);
	}

	/**
	 * * Main-Methode zum Starten des Login-Fensters. Initialisiert die GUI im
	 * Event-Dispatch-Thread.
	 *
	 * @param args Kommandozeilenargumente (werden nicht verwendet)
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(LoginFenster::new);
	}
}
