package GUIKlassen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import Datenbank.UserDAO;
import Datenbank.UserLoginResult;
import Util.UIColors;
import Util.UIImageLoader;
import Util.LoginSession;

/**
 * Login-Fenster für die Anwendung. Ermöglicht Benutzern, sich mit E-Mail und
 * Passwort anzumelden. Unterstützt automatisches Vorausfüllen für neu erstellte
 * Benutzer.
 */
public class LoginFenster extends BaseFrame {

	private JTextField userField;
	private JPasswordField passField;
	private JButton loginButton;

	/**
	 * Konstruktor für das Login-Fenster. Initialisiert GUI-Elemente, Logo,
	 * Eingabefelder, Buttons und Events.
	 */
	public LoginFenster() {
		super("Login - Hochschule");

		getContentPane().setBackground(UIColors.BACKGROUND);

		int logoWidth = 200;
		int logoX = (BaseFrame.WIDTH - logoWidth) / 2;
		add(UIImageLoader.createLogoLabel(logoX, 60, logoWidth, 100));

		int windowWidth = BaseFrame.WIDTH;
		int formWidth = 300;
		int labelWidth = 100;
		int fieldWidth = 180;
		int formHeight = 220;
		int startX = (windowWidth - formWidth) / 2;
		int labelX = startX;
		int fieldX = startX + labelWidth + 10;

		int y = (BaseFrame.HEIGHT - formHeight) / 2;

		JLabel userLabel = new JLabel("E-Mail:");
		userLabel.setFont(new Font("Arial", Font.BOLD, 14));
		userLabel.setBounds(labelX, y, labelWidth, 25);
		add(userLabel);

		userField = new JTextField();
		userField.setBounds(fieldX, y, fieldWidth, 25);
		add(userField);

		JLabel passLabel = new JLabel("Passwort:");
		passLabel.setFont(new Font("Arial", Font.BOLD, 14));
		y += 40;
		passLabel.setBounds(labelX, y, labelWidth, 25);
		add(passLabel);

		passField = new JPasswordField();
		passField.setBounds(fieldX, y, fieldWidth, 25);
		add(passField);

		loginButton = new JButton("Einloggen");
		y += 60;
		loginButton.setBounds(startX + (formWidth - 140) / 2, y, 140, 35);
		loginButton.setBackground(UIColors.PRIMARY_BLUE);
		loginButton.setForeground(UIColors.TEXT_WHITE);
		loginButton.setFont(new Font("Arial", Font.BOLD, 14));
		loginButton.setFocusPainted(false);
		loginButton.setOpaque(true);
		loginButton.setBorderPainted(false);
		add(loginButton);

		JLabel newUserLabel = new JLabel("<HTML><U>Neuer Benutzer?</U></HTML>");
		newUserLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		newUserLabel.setForeground(UIColors.PRIMARY_BLUE);
		y += 45;
		newUserLabel.setBounds(startX + (formWidth - newUserLabel.getPreferredSize().width) / 2, y,
				newUserLabel.getPreferredSize().width, 25);
		newUserLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		add(newUserLabel);

		loginButton.addActionListener(e -> performLogin());
		passField.addActionListener(e -> performLogin());
		autoFillLoginData();

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
	 * Füllt das Login-Formular automatisch mit den zuletzt erstellten Benutzerdaten
	 * aus. Führt optional automatisch den Login aus.
	 */
	private void autoFillLoginData() {
		String lastEmail = NeuerBenutzerFenster.getLastCreatedEmail();
		String lastPassword = NeuerBenutzerFenster.getLastCreatedPassword();

		if (!lastEmail.isEmpty() && !lastPassword.isEmpty()) {
			userField.setText(lastEmail);
			passField.setText(lastPassword);
			loginButton.requestFocus();

			SwingUtilities.invokeLater(() -> {
				try {
					Thread.sleep(100);
					performLogin();
				} catch (InterruptedException e) {
					// Ignorieren
				}
			});

			NeuerBenutzerFenster.clearLastCreatedData();
		}
	}

	/**
	 * Führt den Login-Vorgang aus. Validiert Eingaben, überprüft Zugangsdaten und
	 * leitet Benutzer basierend auf Rolle weiter.
	 */
	private void performLogin() {
		String email = userField.getText();
		String pass = new String(passField.getPassword());

		if (email.isEmpty() || pass.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Bitte E-Mail und Passwort eingeben!", "Eingabe fehlt",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {
			UserLoginResult result = UserDAO.login(email, pass);

			if (result == null) {
				JOptionPane.showMessageDialog(this, "E-Mail oder Passwort falsch!", "Login fehlgeschlagen",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			String rolle = result.getRolle();
			int mnr = result.getMnr();
			LoginSession.setCurrentUser(mnr, rolle, email);

			JOptionPane.showMessageDialog(this, "<html><center>Login erfolgreich!<br>" + "Willkommen, " + email + "<br>"
					+ "Rolle: " + rolle + "</center></html>", "Willkommen", JOptionPane.INFORMATION_MESSAGE);

			switch (rolle.toLowerCase()) {
			case "student" -> {
				new DashboardStudent(mnr);
				dispose();
			}
			case "betreuer" -> {
				new StudentenSucheView();
				dispose();
			}
			case "dekan" -> {
				new DashboardStudiendekan(null);
				dispose();
			}
			default -> {
				JOptionPane.showMessageDialog(this, "Unbekannte Rolle: " + rolle, "Fehler", JOptionPane.ERROR_MESSAGE);
				LoginSession.clear();
			}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Fehler beim Login: " + ex.getMessage(), "Login-Fehler",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Main-Methode zum Testen des Login-Fensters.
	 *
	 * @param args Kommandozeilenargumente
	 */
	public static void main(String[] args) {
		LoginSession.clear();
		SwingUtilities.invokeLater(() -> {
			LoginFenster login = new LoginFenster();
			login.setVisible(true);
		});
	}
}
