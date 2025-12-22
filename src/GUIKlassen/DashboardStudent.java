package GUIKlassen;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import Datenbank.DBConnection;

/**
 * Dashboard für Studenten. Zeigt die Funktionen für Studierende an, z.B.
 * Anmeldung, Abgabe und allgemeine Informationen.
 */
public class DashboardStudent extends JFrame {

	private int mnr;

	/**
	 * Konstruktor für das Studentendashboard.
	 *
	 * @param mnr Matrikelnummer des Studenten
	 */
	public DashboardStudent(int mnr) {
		this.mnr = mnr;

		setTitle("Ansicht Student");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1100, 650);
		setLocationRelativeTo(null);
		setLayout(null);

		JButton logoutBtn = new JButton("Ausloggen");
		logoutBtn.setBounds(900, 20, 140, 40);
		logoutBtn.setBackground(new Color(180, 30, 50));
		logoutBtn.setForeground(Color.WHITE);
		logoutBtn.setFocusPainted(false);
		logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
		logoutBtn.setOpaque(true);
		logoutBtn.setBorderPainted(false);
		add(logoutBtn);

		JPanel leftPanel = new JPanel(null);
		leftPanel.setBounds(60, 100, 380, 450);
		leftPanel.setBackground(Color.WHITE);
		leftPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		add(leftPanel);

		JLabel leftTitle = new JLabel("Meine Funktionen", SwingConstants.CENTER);
		leftTitle.setOpaque(true);
		leftTitle.setBackground(new Color(220, 53, 69));
		leftTitle.setForeground(Color.WHITE);
		leftTitle.setFont(new Font("Arial", Font.BOLD, 16));
		leftTitle.setBounds(0, 0, 380, 50);
		leftPanel.add(leftTitle);

		JButton btnInfo = createBlueButton(
				"Allgemeine Informationen",
				"Thema, Firma, Zeitraum, Betreuer Vorschlag und NDA Status"
		);
		btnInfo.setBounds(50, 70, 280, 75);
		leftPanel.add(btnInfo);

		JButton btnAnmeldung = createBlueButton(
				"Anmeldung zur Bachelor-Arbeit",
				"Offizielles Formular (IDP bestanden)"
		);
		btnAnmeldung.setBounds(50, 170, 280, 75);
		leftPanel.add(btnAnmeldung);

		JButton btnAbgabe = createBlueButton("Abgabe Bachelorarbeit", "");
		btnAbgabe.setBounds(50, 270, 280, 75);
		leftPanel.add(btnAbgabe);

		JPanel rightPanel = new JPanel(null);
		rightPanel.setBounds(480, 100, 500, 450);
		rightPanel.setBackground(Color.WHITE);
		rightPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		add(rightPanel);

		JLabel rightTitle = new JLabel("Portal - Benachrichtigung", SwingConstants.CENTER);
		rightTitle.setOpaque(true);
		rightTitle.setBackground(new Color(220, 53, 69));
		rightTitle.setForeground(Color.WHITE);
		rightTitle.setFont(new Font("Arial", Font.BOLD, 16));
		rightTitle.setBounds(0, 0, 500, 50);
		rightPanel.add(rightTitle);

		// >>> NEU: Benachrichtigungen laden
		ladeBenachrichtigungen(rightPanel);
		logoutBtn.addActionListener(e -> {
            new LoginFenster();
            dispose();
        });

        ladeStudentDaten();
        setVisible(true);

		// ---------- BUTTON ACTIONS ----------

		// Allgemeine Informationen
		btnInfo.addActionListener(e -> {
			JFrame frame = new JFrame("Allgemeine Informationen");
			frame.setSize(600, 600);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.add(new AllgemeineInformationenStudent(mnr));
			frame.setVisible(true);
		});

		// Anmeldung zur Bachelorarbeit
		btnAnmeldung.addActionListener(e -> new AnmeldungZurBachelorarbeitStudent());

		// Abgabe Bachelorarbeit
		btnAbgabe.addActionListener(e -> {
			JFrame frame = new JFrame("Abgabe Bachelorarbeit");
			frame.setSize(560, 600);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.add(new AbgabeBachelorarbeit());
			frame.setVisible(true);
		});

		logoutBtn.addActionListener(e -> {
			new LoginFenster();
			dispose();
		});

		ladeStudentDaten();
		setVisible(true);
	}

	/**
	 * Erstellt einen blauen JButton mit einem Titel und optional einem Untertitel.
	 * Wenn der Untertitel leer ist, wird nur der Titel zentriert und fett dargestellt.
	 * Andernfalls wird der Titel fett und zentriert sowie der Untertitel darunter in
	 * kleinerer Schrift angezeigt.
	 *
	 * @param title    Der Haupttext des Buttons.
	 * @param subtitle Der optionale Untertitel des Buttons.
	 * @return JButton Ein JButton mit blauem Dashboard-Design.
	 */
	private JButton createBlueButton(String title, String subtitle) {
		String text = subtitle.isEmpty()
				? "<html><center><b>" + title + "</b></center></html>"
				: "<html><center><b>" + title + "</b><br><font size='2'>" + subtitle + "</font></center></html>";

		JButton btn = new JButton(text);
		btn.setBackground(new Color(0, 45, 150));
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
		btn.setFont(new Font("Arial", Font.PLAIN, 14));
		btn.setBorderPainted(false);
		btn.setOpaque(true);
		btn.setVerticalAlignment(SwingConstants.TOP);
		return btn;
	}

	/**
	 * Lädt die Daten eines Studenten aus der Datenbank anhand der Matrikelnummer (MNR).
	 */
	private void ladeStudentDaten() {
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"SELECT * FROM studentendb WHERE MNR = ?"
			);
			ps.setInt(1, mnr);
			ps.executeQuery();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Fehler beim Laden der Studentendaten!");
		}
	}

	/**
	 * Lädt alle Benachrichtigungen des Studenten aus der Datenbank
	 * und zeigt sie im rechten Panel an.
	 *
	 * @param panel Panel, in dem die Benachrichtigungen angezeigt werden
	 */
	private void ladeBenachrichtigungen(JPanel panel) {
		try (Connection conn = DBConnection.getConnection()) {
			PreparedStatement ps = conn.prepareStatement(
	                "SELECT titel, text, erstellt_am FROM benachrichtigung " +
	                "WHERE empfaenger_mnr = ? AND empfaenger_rolle = 'STUDENT' " +
	                "ORDER BY erstellt_am DESC"
	            ); // <<< FIX: richtige Tabelle & Spalten

	            ps.setInt(1, mnr);
	            ResultSet rs = ps.executeQuery();

	            int y = 70;

	            while (rs.next()) {
	                JLabel msg = new JLabel(
	                    "<html><b>" + rs.getString("titel") + "</b><br>"
	                    + rs.getString("text") + "<br>"
	                    + "<i>" + rs.getTimestamp("erstellt_am") + "</i></html>"
	                ); // <<< FIX: kein datum mehr

	                msg.setBounds(20, y, 460, 70);
	                panel.add(msg);
	                y += 80;
	            }

	            panel.revalidate();
	            panel.repaint();

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}

	/**
	 * Main-Methode zum Testen des Dashboards.
	 *
	 * @param args Kommandozeilenargumente
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new DashboardStudent(4711));
	}
}
