package GUIKlassen;

import javax.swing.*;
import java.awt.*;
import javax.swing.text.MaskFormatter;
import Datenbank.DBConnection;
import Datenbank.BenachrichtigungDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * GUI für die Anmeldung zur Bachelorarbeit durch den Studenten. Enthält
 * Pflichtfelder, Checkbox für Bedingungen, Datumsauswahl und Buttons zum
 * Absenden.
 */
public class AnmeldungZurBachelorarbeitStudent extends JFrame {

	private int mnr;
	
	/**
	 * Konstruktor für die Anmeldung der Bachelorarbeit.
	 */
	public AnmeldungZurBachelorarbeitStudent(int mnr) {
		this.mnr = mnr;

		setTitle("Anmeldung der Bachelorarbeit");
		setSize(750, 850);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		Color hftBlue = new Color(0, 102, 204);

		// ---------- TITEL ----------
		JLabel title = new JLabel("Anmeldung der Bachelorarbeit");
		title.setFont(new Font("Arial", Font.BOLD, 26));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setOpaque(true);
		title.setBackground(hftBlue);
		title.setForeground(Color.WHITE);
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
		add(title, BorderLayout.NORTH);

		// ---------- MITTE PANEL ----------
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
		centerPanel.setBackground(Color.WHITE);

		// ---------- FRAGE JA/NEIN ----------
		JLabel frage = new JLabel("Darf die HFT den Titel deiner Bachelorarbeit und deinen Namen veröffentlichen?");
		frage.setFont(new Font("Arial", Font.PLAIN, 16));
		frage.setAlignmentX(Component.LEFT_ALIGNMENT);

		JRadioButton jaBtn = new JRadioButton("Ja");
		JRadioButton neinBtn = new JRadioButton("Nein");
		jaBtn.setBackground(Color.WHITE);
		neinBtn.setBackground(Color.WHITE);

		ButtonGroup gruppe = new ButtonGroup();
		gruppe.add(jaBtn);
		gruppe.add(neinBtn);

		JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		radioPanel.setBackground(Color.WHITE);
		radioPanel.add(jaBtn);
		radioPanel.add(neinBtn);
		radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// ---------- BEDINGUNGEN ----------
		JLabel bedTitel = new JLabel("Bedingungen der Bachelorarbeit:");
		bedTitel.setFont(new Font("Arial", Font.BOLD, 16));
		bedTitel.setOpaque(true);
		bedTitel.setBackground(hftBlue);
		bedTitel.setForeground(Color.WHITE);
		bedTitel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		bedTitel.setAlignmentX(Component.LEFT_ALIGNMENT);

		String bedingungenText = "1. Der erlaubte Zeitraum für die Bearbeitung beträgt drei Monate. Der Vorsitzende des "
				+ "Prüfungsausschusses legt den Ausgabetermin und den Abgabetermin fest.\n\n"
				+ "2. Die Bachelorarbeit muss mit meinen eigenen Worten ohne die Hilfe Dritter angefertigt werden.\n\n"
				+ "3. Ich bin verpflichtet, alle verwendeten, relevanten Hilfen und Quellen anzugeben.\n\n"
				+ "4. Ich muss meine Bachelorarbeit in meinem Abschlussvortrag präsentieren.\n\n"
				+ "5. Die Bachelorarbeit muss zu dem vom Vorsitzenden des Prüfungsausschusses festgelegten Abgabetermin eingereicht werden.\n\n"
				+ "6. Es müssen drei Fassungen der Bachelorarbeit in gebundener Papierform, keine Spiralbindung, abgegeben werden.\n\n"
				+ "7. Ich bin damit einverstanden, dass die HFT Stuttgart die Ergebnisse dieser Bachelorarbeit "
				+ "unentgeltlich benutzt und die Forschung und Lehre weiterentwickeln darf.";

		JTextArea textArea = new JTextArea(bedingungenText);
		textArea.setEditable(false);
		textArea.setFont(new Font("Arial", Font.PLAIN, 14));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setPreferredSize(new Dimension(650, 220));
		scroll.setAlignmentX(Component.LEFT_ALIGNMENT);

		// ---------- CHECKBOX ----------
		JCheckBox akzeptiert = new JCheckBox(
				"Ich erkläre, dass ich die Bedingungen zur Bachelorarbeit gelesen und akzeptiert habe.");
		akzeptiert.setBackground(Color.WHITE);
		akzeptiert.setAlignmentX(Component.LEFT_ALIGNMENT);

		// ---------- DATUM ----------
		JLabel datumLabel = new JLabel("Datum der Anmeldung:");
		datumLabel.setFont(new Font("Arial", Font.PLAIN, 15));
		datumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JFormattedTextField datumFeld = null;
		try {
			MaskFormatter datumFormatter = new MaskFormatter("##.##.####");
			datumFormatter.setPlaceholderCharacter('_');
			datumFeld = new JFormattedTextField(datumFormatter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		datumFeld.setPreferredSize(new Dimension(200, 30));

		JPanel datumPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		datumPanel.setBackground(Color.WHITE);
		datumPanel.add(datumLabel);
		datumPanel.add(datumFeld);
		datumPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// ---------- HINWEIS ----------
		JLabel hinweis = new JLabel("Nach dem Absenden wird Ihr Betreuer automatisch benachrichtigt.");
		hinweis.setFont(new Font("Arial", Font.ITALIC, 14));
		hinweis.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		hinweis.setAlignmentX(Component.LEFT_ALIGNMENT);

		// ---------- BOTTOM BUTTONS ----------
		JButton zurueckBtn = new JButton("Zurück zur Übersicht");
		zurueckBtn.setBackground(hftBlue);
		zurueckBtn.setForeground(Color.WHITE);
		zurueckBtn.setOpaque(true);
		zurueckBtn.setBorderPainted(false);
		zurueckBtn.setFocusPainted(false);

		JButton absendenBtn = new JButton("Anmeldeformular absenden");
		absendenBtn.setBackground(hftBlue);
		absendenBtn.setForeground(Color.WHITE);
		absendenBtn.setOpaque(true);
		absendenBtn.setBorderPainted(false);
		absendenBtn.setFocusPainted(false);

		zurueckBtn.setPreferredSize(new Dimension(220, 45));
		absendenBtn.setPreferredSize(new Dimension(280, 45));

		JPanel bottomBtnPanel = new JPanel(new BorderLayout());
		bottomBtnPanel.setBackground(Color.WHITE);
		bottomBtnPanel.add(zurueckBtn, BorderLayout.WEST);
		bottomBtnPanel.add(absendenBtn, BorderLayout.EAST);
		bottomBtnPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

		// ---------- ADD PANELS ----------
		centerPanel.add(frage);
		centerPanel.add(radioPanel);
		centerPanel.add(Box.createVerticalStrut(15));
		centerPanel.add(bedTitel);
		centerPanel.add(scroll);
		centerPanel.add(Box.createVerticalStrut(10));
		centerPanel.add(akzeptiert);
		centerPanel.add(Box.createVerticalStrut(15));
		centerPanel.add(datumPanel);
		centerPanel.add(Box.createVerticalStrut(15));
		centerPanel.add(hinweis);

		add(centerPanel, BorderLayout.CENTER);
		add(bottomBtnPanel, BorderLayout.SOUTH);

		// ---------- BUTTON ACTIONS ----------
		zurueckBtn.addActionListener(e -> {
			new DashboardStudent(mnr);
			dispose();
		});

		final JFormattedTextField finalDatumFeld = datumFeld;
		absendenBtn.addActionListener(e -> {
			// Validierung
			if (!akzeptiert.isSelected()) {
				JOptionPane.showMessageDialog(this,
					"Bitte akzeptieren Sie die Bedingungen.",
					"Bedingungen nicht akzeptiert", JOptionPane.WARNING_MESSAGE);
				return;
			}

			if (!jaBtn.isSelected() && !neinBtn.isSelected()) {
				JOptionPane.showMessageDialog(this,
					"Bitte beantworten Sie die Frage zur Veröffentlichung.",
					"Frage nicht beantwortet", JOptionPane.WARNING_MESSAGE);
				return;
			}

			String datum = finalDatumFeld.getText();
			if (datum == null || datum.contains("_")) {
				JOptionPane.showMessageDialog(this,
					"Bitte geben Sie ein gültiges Datum ein.",
					"Ungültiges Datum", JOptionPane.WARNING_MESSAGE);
				return;
			}

			try {
				// Anmeldung in DB speichern
				Connection conn = DBConnection.getConnection();
				
				// Betreuer-MNR holen
				PreparedStatement ps = conn.prepareStatement(
					"SELECT betreuer_mnr FROM antraege WHERE student_mnr = ? AND status = 'dekan_genehmigt' LIMIT 1"
				);
				ps.setInt(1, mnr);
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					int betreuerMnr = rs.getInt("betreuer_mnr");
					
					// Anmeldung speichern
					PreparedStatement psAnmeldung = conn.prepareStatement(
						"INSERT INTO anmeldungen (student_mnr, betreuer_mnr, datum, veroeffentlichung_erlaubt) " +
						"VALUES (?, ?, STR_TO_DATE(?, '%d.%m.%Y'), ?)"
					);
					psAnmeldung.setInt(1, mnr);
					psAnmeldung.setInt(2, betreuerMnr);
					psAnmeldung.setString(3, datum);
					psAnmeldung.setBoolean(4, jaBtn.isSelected());
					psAnmeldung.executeUpdate();

					// Benachrichtigungen erstellen
					BenachrichtigungDAO.erstellen(
						betreuerMnr,
						"Student (MNR: " + mnr + ") hat sich für die Bachelorarbeit angemeldet.",
						"betreuer",
						mnr,
						betreuerMnr
					);

					BenachrichtigungDAO.erstellen(
						mnr,
						"Ihre Anmeldung zur Bachelorarbeit wurde erfolgreich übermittelt.",
						"student",
						null,
						mnr
					);

					conn.close();

					JOptionPane.showMessageDialog(this,
						"<html><center>Ihre Anmeldung zur Bachelorarbeit wurde<br>erfolgreich übermittelt!<br>" +
						"Ihr Betreuer wurde benachrichtigt.</center></html>",
						"Erfolg", JOptionPane.INFORMATION_MESSAGE);

					new DashboardStudent(mnr);
					dispose();
				} else {
					JOptionPane.showMessageDialog(this,
						"Kein Betreuer gefunden. Bitte kontaktieren Sie das Prüfungsamt.",
						"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this,
					"Fehler beim Speichern: " + ex.getMessage(),
					"Fehler", JOptionPane.ERROR_MESSAGE);
			}
		});

		setVisible(true);
	}

	/**
     * Main-Methode zum Testen.
     *
     * @param args Kommandozeilenargumente
     */
	public static void main(String[] args) {
		new AnmeldungZurBachelorarbeitStudent(4711);
	}
}
