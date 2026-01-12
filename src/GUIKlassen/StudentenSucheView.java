package GUIKlassen;

import Datenbank.StudentDAO;
import Datenbank.StudentDAO.StudentInfo;
import Util.UIColors;
import Datenbank.DBConnection;
import Util.LoginSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * GUI für die Suche und Anzeige von Studenten.
 *
 * Betreuer können nach Studenten suchen, diese auswählen, zum Dashboard
 * wechseln und Benachrichtigungen einsehen.
 */
public class StudentenSucheView extends JFrame {

	/** Modell für die Studentenauswahl */
	private DefaultListModel<StudentInfo> studentModel;

	/** Liste der Studenten */
	private JList<StudentInfo> studentList;

	/** Textbereich für Portal-Benachrichtigungen */
	private JTextArea portalArea;

	/** Matrikelnummer des aktuell angemeldeten Betreuers */
	private int currentBetreuerMnr;

	/**
	 * Erstellt die Studenten-Suche GUI.
	 *
	 * Lädt alle Studenten, zeigt Portal-Benachrichtigungen an und setzt die
	 * aktuellen Sessiondaten.
	 */
	public StudentenSucheView() {
		this.currentBetreuerMnr = LoginSession.getCurrentMnr();

		setTitle("Studenten-Suche");
		setSize(BaseFrame.WIDTH, BaseFrame.HEIGHT);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// GUI Aufbau Header, Main Panel und Buttons
		// ...

		// Initiale Suche und Benachrichtigungen laden
		sucheStudenten("");
		ladeBenachrichtigungenFuerBetreuer();

		setVisible(true);
	}

	/**
	 * Sucht Studenten anhand des Namens.
	 *
	 * @param name Suchbegriff für Studentenname Lädt alle Treffer in die Liste oder
	 *             zeigt Hinweis wenn keine gefunden wurden.
	 */
	private void sucheStudenten(String name) {
		try {
			studentModel.clear();
			List<StudentInfo> studenten = StudentDAO.sucheStudenten(name);

			if (studenten.isEmpty()) {
				studentModel.addElement(new StudentInfo(0, "Keine Studenten gefunden", ""));
			} else {
				for (StudentInfo s : studenten) {
					studentModel.addElement(s);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Fehler bei der Suche: " + e.getMessage(), "Fehler",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Öffnet das Dashboard des ausgewählten Studenten.
	 *
	 * Zeigt eine Informationsmeldung, falls kein Student ausgewählt wurde.
	 */
	private void oeffneDashboard() {
		StudentInfo student = studentList.getSelectedValue();
		if (student == null || student.mnr == 0) {
			JOptionPane.showMessageDialog(this, "Bitte zuerst einen Studenten auswählen!", "Information",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		new DashboardBetreuerView(student);
		dispose();
	}

	/**
	 * Lädt die neuesten Benachrichtigungen für den angemeldeten Betreuer.
	 *
	 * Zeigt die Benachrichtigungen im Portal-Bereich an und passt den Fenstertitel
	 * entsprechend an.
	 */
	private void ladeBenachrichtigungenFuerBetreuer() {
		try (Connection conn = DBConnection.getConnection()) {

			String sql = """
					    SELECT b.text, b.datum, s.Vorname, s.Nachname,
					           CASE
					               WHEN b.empfanger_mnr IS NULL THEN 'Allgemein'
					               WHEN b.empfanger_mnr = ? THEN 'Direkt an Sie'
					               ELSE 'Anderer Betreuer'
					           END as empfaenger_info
					    FROM benachrichtigungen b
					    LEFT JOIN studentendb s ON b.sender_mnr = s.MNR
					    WHERE b.empfaenger_rolle = 'betreuer'
					    AND (b.empfanger_mnr IS NULL OR b.empfanger_mnr = ?)
					    ORDER BY b.datum DESC, b.erstellt_am DESC
					    LIMIT 20
					""";

			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, currentBetreuerMnr);
				ps.setInt(2, currentBetreuerMnr);
				ResultSet rs = ps.executeQuery();

				StringBuilder sb = new StringBuilder();
				int counter = 0;

				while (rs.next()) {
					counter++;
					if (counter == 1) {
						String betreuerName = "Unbekannt";
						String sqlName = "SELECT Vorname, Nachname FROM studentendb WHERE MNR = ?";
						try (PreparedStatement psName = conn.prepareStatement(sqlName)) {
							psName.setInt(1, currentBetreuerMnr);
							ResultSet rsName = psName.executeQuery();
							if (rsName.next()) {
								String vorname = rsName.getString("Vorname");
								String nachname = rsName.getString("Nachname");
								if (vorname != null && nachname != null && !nachname.isEmpty()) {
									betreuerName = !vorname.equals(nachname) ? vorname + " " + nachname : vorname;
								} else if (vorname != null) {
									betreuerName = vorname;
								}
							}
						}
						sb.append("Benachrichtigungen für ").append(betreuerName).append("\n\n");
					}

					String text = rs.getString("text");
					String datum = rs.getDate("datum").toString();
					String vorname = rs.getString("Vorname");
					String nachname = rs.getString("Nachname");
					String empfaengerInfo = rs.getString("empfaenger_info");

					sb.append("[").append(counter).append("] [").append(datum).append("]\n");
					sb.append("   ").append(text).append("\n");
					if (vorname != null) {
						String senderName = (nachname != null && !nachname.isEmpty() && !vorname.equals(nachname))
								? vorname + " " + nachname
								: vorname;
						sb.append("   Von: ").append(senderName).append("\n");
					}
					sb.append("   Art: ").append(empfaengerInfo).append("\n");
					sb.append("   ────────────────────\n");
				}

				if (counter == 0) {
					sb.append("Keine neuen Benachrichtigungen.\n");
				}

				portalArea.setText(sb.toString());
				setTitle(counter > 0 ? "Studenten-Suche - Betreuer-Portal (" + counter + " neue Benachrichtigungen)"
						: "Studenten-Suche - Betreuer-Portal");

			}
		} catch (Exception e) {
			e.printStackTrace();
			portalArea.setText("Fehler beim Laden der Benachrichtigungen.\nFehler: " + e.getMessage());
		}
	}

	/**
	 * Hilfsmethode zur Erstellung eines Panels mit Titel.
	 *
	 * @param title       Überschrift des Panels
	 * @param headerColor Hintergrundfarbe der Überschrift
	 * @return JPanel mit Titelbereich
	 */
	private JPanel createBoxPanel(String title, Color headerColor) {
		JPanel box = new JPanel(new BorderLayout());
		box.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
		box.setBackground(Color.WHITE);

		JLabel label = new JLabel(title, SwingConstants.CENTER);
		label.setOpaque(true);
		label.setBackground(headerColor);
		label.setForeground(Color.WHITE);
		label.setFont(new Font("Arial", Font.BOLD, 16));
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		box.add(label, BorderLayout.NORTH);
		return box;
	}

	/**
	 * Startpunkt der Anwendung für Testzwecke.
	 *
	 * Setzt eine Beispiel-Session und öffnet die Studenten-Suche.
	 */
	public static void main(String[] args) {
		LoginSession.setCurrentUser(2, "betreuer");
		SwingUtilities.invokeLater(() -> new StudentenSucheView());
	}
}