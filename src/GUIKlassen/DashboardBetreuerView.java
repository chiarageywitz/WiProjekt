package GUIKlassen;

import Datenbank.StudentDAO.StudentInfo;
import Util.UIColors;
import Util.LoginSession;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Datenbank.DBConnection;

/**
 * Dashboard für Betreuer, zeigt Übersicht eines einzelnen Studenten. Enthält
 * Funktionen für Noteneingabe, Freigabe der Arbeit und
 * Portal-Benachrichtigungen.
 */
public class DashboardBetreuerView extends JFrame {

	private StudentInfo student;

	/**
	 * Konstruktor für das Betreuer-Dashboard.
	 * 
	 * @param student StudentInfo-Objekt des ausgewählten Studenten
	 */
	public DashboardBetreuerView(StudentInfo student) {
		this.student = student;

		setTitle("Betreuer-Übersicht");
		setSize(BaseFrame.WIDTH, BaseFrame.HEIGHT);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		Color hftRed = UIColors.HFT_RED;
		Color hftBlue = UIColors.PRIMARY_BLUE;
		Color panelBg = UIColors.BACKGROUND;


		// ================= TOP =================
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(Color.WHITE);
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel topLabel = new JLabel(
				"Student: " + student.name + " | Thema: " + (student.thema != null ? student.thema : "—"));
		topLabel.setFont(new Font("Arial", Font.BOLD, 18));

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttonPanel.setBackground(Color.WHITE);

		// Logout-Button bleibt rot
		JButton logoutBtn = new JButton("Ausloggen");
		logoutBtn.setBackground(hftRed);
		logoutBtn.setForeground(Color.WHITE);
		logoutBtn.setFocusPainted(false);
		logoutBtn.setPreferredSize(new Dimension(100, 30)); // Größe merken
		logoutBtn.addActionListener(e -> {
			new LoginFenster();
			dispose();
		});

		// Zurück-Button jetzt gleich groß wie Logout
		JButton backBtn = new JButton("Zurück");
		backBtn.setPreferredSize(logoutBtn.getPreferredSize());
		backBtn.setBackground(UIColors.PRIMARY_BLUE);
		backBtn.setForeground(Color.WHITE);
		backBtn.setFocusPainted(false);
		backBtn.setBorderPainted(false);
		backBtn.addActionListener(e -> {
		    new StudentenSucheView();
		    dispose();
		});

		buttonPanel.add(backBtn);
		buttonPanel.add(logoutBtn);

		topPanel.add(topLabel, BorderLayout.WEST);
		topPanel.add(buttonPanel, BorderLayout.EAST);
		add(topPanel, BorderLayout.NORTH);

		// ================= MAIN =================
		JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		mainPanel.setBackground(panelBg);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// ===== Funktionen =====
		JPanel funktionenBox = createBoxPanel("Meine Funktionen", hftRed);

		JButton noteneingabeBtn = createBlueButton("Noteneingabe", hftBlue);
		JButton antraegeBtn = createBlueButton("Anträge verwalten", hftBlue);
		JButton zwischenversionenBtn = createBlueButton("Zwischenversionen & Feedback", hftBlue);

		noteneingabeBtn.addActionListener(e -> {
			setVisible(false);
			new Noteneingabe(student, "betreuer", this);
		});

		antraegeBtn.addActionListener(e -> {
			try {
				int betreuerMnr = LoginSession.getLoggedInMnr();
				new AntragsverwaltungBetreuer(betreuerMnr);
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Fehler: " + ex.getMessage());
			}
		});

		zwischenversionenBtn.addActionListener(e -> {
			try {
				int betreuerMnr = LoginSession.getLoggedInMnr();
				new ZwischenversionenVerwaltung(student.mnr, betreuerMnr, "betreuer");
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Fehler: " + ex.getMessage());
			}
		});

		funktionenBox.add(Box.createVerticalGlue());
		funktionenBox.add(noteneingabeBtn);
		funktionenBox.add(Box.createVerticalStrut(15));
		funktionenBox.add(antraegeBtn);
		funktionenBox.add(Box.createVerticalStrut(15));
		funktionenBox.add(zwischenversionenBtn);
		funktionenBox.add(Box.createVerticalGlue());

		// ===== Portal =====
		JPanel portalBox = createBoxPanel("Portal-Benachrichtigungen", hftRed);
		JTextArea portalArea = new JTextArea();
		portalArea.setEditable(false);
		portalArea.setLineWrap(true);
		portalArea.setWrapStyleWord(true);
		
		// Benachrichtigungen laden
		try {
			int betreuerMnr = LoginSession.getLoggedInMnr();
			Connection conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement(
				"SELECT text, datum FROM benachrichtigungen " +
				"WHERE mnr = ? ORDER BY datum DESC LIMIT 10"
			);
			ps.setInt(1, betreuerMnr);
			ResultSet rs = ps.executeQuery();
			
			StringBuilder sb = new StringBuilder();
			while (rs.next()) {
				sb.append("• ")
				  .append(rs.getDate("datum"))
				  .append(": ")
				  .append(rs.getString("text"))
				  .append("\n\n");
			}
			portalArea.setText(sb.toString());
			conn.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			portalArea.setText("Fehler beim Laden der Benachrichtigungen");
		}
		
		portalBox.add(new JScrollPane(portalArea));

		mainPanel.add(funktionenBox);
		mainPanel.add(portalBox);

		add(mainPanel, BorderLayout.CENTER);
		setVisible(true);
	}

	// ================= HILFSMETHODEN =================
	/**
	 * Hilfsmethode zum Erstellen eines Panels mit farbigem Header.
	 * 
	 * @param title Titel des Panels
	 * @param headerColor Farbe des Headers
	 * @return JPanel mit Header
	 */
	private JPanel createBoxPanel(String title, Color headerColor) {
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
		box.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
		box.setBackground(Color.WHITE);

		JLabel label = new JLabel(title);
		label.setOpaque(true);
		label.setBackground(headerColor);
		label.setForeground(Color.WHITE);
		label.setFont(new Font("Arial", Font.BOLD, 16));
		label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);

		box.add(label);
		box.add(Box.createVerticalStrut(10));
		return box;
	}

	/**
	 * Hilfsmethode zum Erstellen eines blauen Buttons.
     *
     * @param text  Text des Buttons
     * @param color Hintergrundfarbe
     * @return JButton
	 */
	private JButton createBlueButton(String text, Color color) {
		JButton btn = new JButton(text);
		btn.setBackground(color);
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setAlignmentX(Component.CENTER_ALIGNMENT);
		btn.setMaximumSize(new Dimension(240, 60));
		return btn;
	}
	
	public StudentInfo getStudent() {
	    return student;
	}

}
