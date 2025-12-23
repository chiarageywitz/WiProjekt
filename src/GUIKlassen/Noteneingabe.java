package GUIKlassen;

import Datenbank.DBConnection;
import Datenbank.StudentDAO.StudentInfo;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Frame-Klasse für die Noteneingabe von Betreuern oder Studiendekan. Ermöglicht
 * die Eingabe von Noten, Speicherung in der Datenbank und Benachrichtigung des
 * Studenten. Berechnet automatisch die Endnote.
 */
public class Noteneingabe extends JFrame {

	private StudentInfo student;
	private String rolle;
	private int mnr;
	private JFrame parent;

	private final Color dashboardBlue = new Color(0, 45, 150);

	/**
	 * Konstruktor für die Noteneingabe.
	 * 
	 * @param student StudentInfo-Objekt, für den die Note eingetragen wird.
	 * @param rolle   Rolle des Benutzers ("betreuer" oder "studiendekan").
	 * @param parent  Parent-Frame, um nach dem Schließen wieder sichtbar zu werden.
	 */
	public Noteneingabe(StudentInfo student, String rolle, JFrame parent) {

		// Sicherheitscheck
		if (student == null) {
			JOptionPane.showMessageDialog(null, "Kein Student übergeben!");
			dispose();
			return;
		}

		this.student = student;
		this.rolle = rolle.toLowerCase();
		this.mnr = student.mnr;
		this.parent = parent;

		setTitle("Noteneingabe");
		setSize(700, 500); // etwas größer für Hinweis + Bemerkung
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel main = new JPanel(null);
		main.setBackground(Color.WHITE);
		add(main);

		int y = 15;

		// Header
		JPanel header = new JPanel();
		header.setBackground(dashboardBlue);
		header.setBounds(20, y, 260, 35);
		JLabel headerLabel = new JLabel(
				rolle.equals("betreuer") ? "Noteneingabe (Betreuer)" : "Noteneingabe (Studiendekan)");
		headerLabel.setForeground(Color.WHITE);
		headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
		header.add(headerLabel);
		main.add(header);

		y += 60;

		// Thema
		JLabel themaLabel = new JLabel("Thema:");
		themaLabel.setBounds(20, y, 200, 25);
		main.add(themaLabel);

		JTextField themaField = new JTextField(student.thema);
		themaField.setBounds(20, y + 25, 640, 30);
		themaField.setEditable(false); // Thema nicht editierbar
		main.add(themaField);

		y += 70;

		// Note
		JLabel noteLabel = new JLabel(rolle.equals("betreuer") ? "Note (Betreuer):" : "Note (Studiendekan):");
		noteLabel.setBounds(20, y, 250, 25);
		main.add(noteLabel);

		JTextField noteField = new JTextField();
		noteField.setBounds(20, y + 25, 200, 30);
		Double vorhandeneNote = ladeVorhandeneNote();
		if (vorhandeneNote != null) {
			noteField.setText(vorhandeneNote.toString().replace(".", ","));
		}

		// DocumentFilter: nur Zahlen + Komma
		((AbstractDocument) noteField.getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
					throws BadLocationException {
				if (isValidInput(string))
					super.insertString(fb, offset, string, attr);
			}

			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
					throws BadLocationException {
				if (isValidInput(text))
					super.replace(fb, offset, length, text, attrs);
			}

			private boolean isValidInput(String text) {
				return text.matches("[0-9,]*"); // nur Ziffern + Komma
			}
		});

		main.add(noteField);
		y += 70;

		// Hinweis
		JLabel hinweisLabel = new JLabel(
				"Hinweis: Benotung wie folgt: 12:3 (Betreuer : Studiendekan).");
		hinweisLabel.setBounds(20, y, 640, 25);
		hinweisLabel.setForeground(Color.DARK_GRAY);
		hinweisLabel.setFont(new Font("Arial", Font.ITALIC, 12));
		main.add(hinweisLabel);

		y += 50;

		// Bemerkung (für Dekan)
		JLabel bemerkungLabel = new JLabel("Bemerkung:");
		bemerkungLabel.setBounds(20, y, 200, 25);
		main.add(bemerkungLabel);

		JTextField bemerkungField = new JTextField();
		bemerkungField.setBounds(20, y + 25, 640, 30);
		main.add(bemerkungField);

		y += 70;

		// Absenden
		JButton speichernBtn = new JButton("Absenden");
		speichernBtn.setBounds(20, y, 140, 35);
		styleButton(speichernBtn);
		speichernBtn.addActionListener(e -> speichern(noteField, bemerkungField));
		main.add(speichernBtn);

		// Zurück
		JButton zurueckBtn = new JButton("Zurück");
		zurueckBtn.setBounds(180, y, 140, 35);
		styleButton(zurueckBtn);
		zurueckBtn.addActionListener(e -> {
			parent.setVisible(true);
			dispose();
		});
		main.add(zurueckBtn);

		setVisible(true);
	}

	/**
	 * Speichert die eingegebene Note, aktualisiert die Endnote und sendet
	 * Benachrichtigung.
	 * 
	 * @param noteField      JTextField mit der eingegebenen Note.
	 * @param bemerkungField JTextField für optionale Bemerkungen.
	 */
	private void speichern(JTextField noteField, JTextField bemerkungField) {
		String noteText = noteField.getText().trim();
		String bemerkung = bemerkungField.getText().trim();
		// 🔒 Warnung bei vorhandener Note
		Double vorhandeneNote = ladeVorhandeneNote();
		if (vorhandeneNote != null) {
			int antwort = JOptionPane.showConfirmDialog(this,
					"Es existiert bereits eine Note (" + vorhandeneNote + "). Möchten Sie diese überschreiben?",
					"Warnung", JOptionPane.YES_NO_OPTION);
			if (antwort != JOptionPane.YES_OPTION) {
				parent.setVisible(true);
				dispose();
				return;
			}
		}
		if (noteText.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Bitte Note eingeben!");
			return;
		}
		// Komma → Punkt (für Double)
		double note = Double.parseDouble(noteText.replace(",", "."));
		try (Connection conn = DBConnection.getConnection()) {
			// Note speichern
			String sqlNote;
			if (rolle.equals("betreuer")) {
				sqlNote = """
						    INSERT INTO noten (mnr, note_betreuer)
						    VALUES (?, ?)
						    ON DUPLICATE KEY UPDATE note_betreuer = ?
						""";
			} else {
				sqlNote = """
						    INSERT INTO noten (mnr, note_studiendekan)
						    VALUES (?, ?)
						    ON DUPLICATE KEY UPDATE note_studiendekan = ?
						""";
			}
			PreparedStatement psNote = conn.prepareStatement(sqlNote);
			psNote.setInt(1, mnr);
			psNote.setDouble(2, note);
			psNote.setDouble(3, note);
			psNote.executeUpdate();
			// Note berechnen
			String sqlEndnote = """
					    UPDATE noten
					    SET endnote = (
					        (3 * note_studiendekan + 12 * note_betreuer) / 15
					    )
					    WHERE mnr = ?
					      AND note_studiendekan IS NOT NULL
					      AND note_betreuer IS NOT NULL
					""";

			PreparedStatement psEnd = conn.prepareStatement(sqlEndnote);
			psEnd.setInt(1, mnr);
			psEnd.executeUpdate();

			// Benachrichtigung
			PreparedStatement psMsg = conn
					.prepareStatement("INSERT INTO benachrichtigungen (mnr, text, datum) VALUES (?, ?, CURRENT_DATE)");
			String text = "Neue Note vom " + rolle + ": " + noteText;
			if (!bemerkung.isEmpty()) {
				text += " | Bemerkung: " + bemerkung;
			}
			psMsg.setInt(1, mnr);
			psMsg.setString(2, text);
			psMsg.executeUpdate();
			JOptionPane.showMessageDialog(this, "Note erfolgreich gespeichert!");
			parent.setVisible(true);
			dispose();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Fehler beim Speichern der Note!\n" + ex.getMessage());
		}
	}

	/**
	 * Formatiert einen Button nach dem Dashboard-Design.
	 * 
	 * @param button JButton, der formatiert wird.
	 */
	private void styleButton(JButton button) {
		button.setBackground(dashboardBlue);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setFont(new Font("Arial", Font.PLAIN, 14));
		button.setBorderPainted(false);
		button.setOpaque(true);
	}

	/**
	 * Lädt die vorhandene Note für den Studenten und die Rolle aus der Datenbank.
	 * 
	 * @return Double-Wert der Note oder null, falls keine Note vorhanden.
	 */
	private Double ladeVorhandeneNote() {
		try (Connection conn = DBConnection.getConnection()) {
			String sql;
			if (rolle.equals("betreuer")) {
				sql = "SELECT note_betreuer FROM noten WHERE mnr = ?";
			} else {
				sql = "SELECT note_studiendekan FROM noten WHERE mnr = ?";
			}
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, mnr);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble(1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
