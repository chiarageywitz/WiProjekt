package GUIKlassen;

import Datenbank.DBConnection;
import Util.PasswortUtil;
import Util.UIColors;
import Util.UIImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * GUI-Fenster zur Erstellung eines neuen Benutzers.
 * 
 * Ermöglicht die Eingabe von Benutzername, Passwort, E-Mail und Rolle. Führt
 * Validierungen durch, speichert die Daten in der Datenbank und speichert
 * zuletzt erstellte Benutzerdaten für automatisches Login.
 */
public class NeuerBenutzerFenster extends BaseFrame {

	// Letzte erstellte Benutzerdaten für automatisches Login
	private static String lastCreatedEmail = "";
	private static String lastCreatedPassword = "";

	public static String getLastCreatedEmail() {
		return lastCreatedEmail;
	}

	public static String getLastCreatedPassword() {
		return lastCreatedPassword;
	}

	public static void clearLastCreatedData() {
		lastCreatedEmail = "";
		lastCreatedPassword = "";
	}

	/**
	 * Konstruktor. Erstellt das GUI-Formular und bindet Validierungen sowie
	 * Datenbankoperationen an den "Benutzer erstellen"-Button.
	 */
	public NeuerBenutzerFenster() {
		super("Neuer Benutzer - Hochschule");

		getContentPane().setBackground(UIColors.BACKGROUND);

		// =========================
		// Logo und Header
		// =========================
		int logoWidth = 200;
		int logoX = (BaseFrame.WIDTH - logoWidth) / 2;
		add(UIImageLoader.createLogoLabel(logoX, 20, logoWidth, 100));

		JLabel header = new JLabel("Neuen Benutzer erstellen", SwingConstants.CENTER);
		header.setOpaque(true);
		header.setBackground(UIColors.HFT_RED);
		header.setForeground(UIColors.TEXT_WHITE);
		header.setFont(new Font("Arial", Font.BOLD, 16));
		header.setBounds(0, 130, BaseFrame.WIDTH, 40);
		add(header);

		// =========================
		// Formular-Felder
		// =========================
		int formWidth = 320;
		int labelWidth = 120;
		int fieldWidth = 180;
		int startX = (BaseFrame.WIDTH - formWidth) / 2;
		int labelX = startX;
		int fieldX = startX + labelWidth + 10;
		int y = 230;

		// Benutzername
		JLabel nameLabel = new JLabel("Benutzername:");
		nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
		nameLabel.setBounds(labelX, y, labelWidth, 25);
		add(nameLabel);

		JTextField nameField = new JTextField();
		nameField.setBounds(fieldX, y, fieldWidth, 25);
		add(nameField);

		y += 40;

		// Passwort
		JLabel passLabel = new JLabel("Passwort:");
		passLabel.setFont(new Font("Arial", Font.BOLD, 14));
		passLabel.setBounds(labelX, y, labelWidth, 25);
		add(passLabel);

		JPasswordField passField = new JPasswordField();
		passField.setBounds(fieldX, y, fieldWidth, 25);
		add(passField);

		JLabel passHint = new JLabel("Mindestens 6 Zeichen");
		passHint.setFont(new Font("Arial", Font.ITALIC, 11));
		passHint.setForeground(Color.GRAY);
		passHint.setBounds(fieldX, y + 28, fieldWidth, 15);
		add(passHint);

		y += 60;

		// E-Mail
		JLabel emailLabel = new JLabel("E-Mail:");
		emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
		emailLabel.setBounds(labelX, y, labelWidth, 25);
		add(emailLabel);

		JTextField emailField = new JTextField();
		emailField.setBounds(fieldX, y, fieldWidth, 25);
		add(emailField);

		y += 55;

		// Rolle
		JLabel rolleLabel = new JLabel("Rolle:");
		rolleLabel.setFont(new Font("Arial", Font.BOLD, 14));
		rolleLabel.setBounds(labelX, y, labelWidth, 25);
		add(rolleLabel);

		String[] rollen = { "Student", "Betreuer", "Dekan" };
		JComboBox<String> rolleBox = new JComboBox<>(rollen);
		rolleBox.setBounds(fieldX, y, fieldWidth, 25);
		add(rolleBox);

		y += 60;

		// =========================
		// Benutzer erstellen Button
		// =========================
		JButton createButton = new JButton("Benutzer erstellen");
		createButton.setBounds(startX + (formWidth - 180) / 2, y + 10, 180, 35);
		createButton.setBackground(UIColors.PRIMARY_BLUE);
		createButton.setForeground(UIColors.TEXT_WHITE);
		createButton.setFont(new Font("Arial", Font.BOLD, 14));
		createButton.setFocusPainted(false);
		createButton.setOpaque(true);
		createButton.setBorderPainted(false);
		add(createButton);

		// ActionListener für Enter-Taste
		nameField.addActionListener(e -> createButton.doClick());
		passField.addActionListener(e -> createButton.doClick());
		emailField.addActionListener(e -> createButton.doClick());

		// =========================
		// Button-Logik: Validierung + DB
		// =========================
		createButton.addActionListener(e -> {
			String user = nameField.getText().trim();
			String pass = new String(passField.getPassword());
			String email = emailField.getText().trim();
			String rolle = (String) rolleBox.getSelectedItem();

			// Pflichtfelder prüfen
			if (user.isEmpty() || pass.isEmpty() || email.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen!");
				return;
			}

			// E-Mail-Format prüfen
			if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
				JOptionPane.showMessageDialog(this, "Bitte eine gültige E-Mail-Adresse eingeben!");
				return;
			}

			// Passwortlänge prüfen
			if (pass.length() < 6) {
				JOptionPane.showMessageDialog(this, "Passwort muss mindestens 6 Zeichen lang sein!");
				return;
			}

			// Datenbankeintrag
			try (Connection conn = DBConnection.getConnection()) {
				// Prüfen, ob E-Mail bereits existiert
				PreparedStatement checkPs = conn.prepareStatement("SELECT COUNT(*) FROM studentendb WHERE email = ?");
				checkPs.setString(1, email);
				ResultSet rs = checkPs.executeQuery();
				rs.next();
				if (rs.getInt(1) > 0) {
					JOptionPane.showMessageDialog(this, "Diese E-Mail existiert bereits!");
					return;
				}

				// Benutzer einfügen
				PreparedStatement ps = conn
						.prepareStatement("INSERT INTO studentendb (Nachname, Vorname, email, rolle, passwort) "
								+ "VALUES (?, ?, ?, ?, ?)");
				ps.setString(1, user);
				ps.setString(2, user);
				ps.setString(3, email);
				ps.setString(4, rolle);
				ps.setString(5, PasswortUtil.hash(pass));
				ps.executeUpdate();

				// Erfolgs-Meldung
				JOptionPane.showMessageDialog(this, "Benutzer erfolgreich erstellt!\nSie können sich jetzt einloggen.");

				// Speichern für automatisches Login
				lastCreatedEmail = email;
				lastCreatedPassword = pass;

				// Zum Login-Fenster wechseln
				dispose();
				new LoginFenster();

			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Benutzer konnte nicht erstellt werden: " + ex.getMessage());
			}
		});

		// =========================
		// Link zurück zum Login
		// =========================
		JLabel backToLoginLabel = new JLabel("<HTML><U>Bereits ein Konto? Zum Login</U></HTML>");
		backToLoginLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		backToLoginLabel.setForeground(UIColors.PRIMARY_BLUE);
		backToLoginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		int linkWidth = backToLoginLabel.getPreferredSize().width;
		backToLoginLabel.setBounds(startX + (formWidth - linkWidth) / 2, y + 60, linkWidth, 25);
		add(backToLoginLabel);

		backToLoginLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clearLastCreatedData();
				dispose();
				new LoginFenster();
			}
		});

		setVisible(true);
		setAlwaysOnTop(true);
	}
}
