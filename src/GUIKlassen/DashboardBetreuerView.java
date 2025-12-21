package GUIKlassen;

import Datenbank.StudentDAO.StudentInfo;

import javax.swing.*;
import java.awt.*;

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
		setSize(1000, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Color hftRed = new Color(226, 0, 26);
		Color hftBlue = new Color(0, 45, 150);
		Color panelBg = new Color(240, 240, 240);

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
		backBtn.setPreferredSize(logoutBtn.getPreferredSize()); // gleiche Größe
		backBtn.setFocusPainted(false);
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
		JButton freigabeBtn = createBlueButton("Freigabe der Arbeit", hftBlue);

		noteneingabeBtn.addActionListener(e -> {
			setVisible(false); // 🔹 Dashboard ausblenden
			new Noteneingabe(student, "betreuer", this);
		});

		funktionenBox.add(Box.createVerticalGlue());
		funktionenBox.add(noteneingabeBtn);
		funktionenBox.add(Box.createVerticalStrut(20));
		funktionenBox.add(freigabeBtn);
		funktionenBox.add(Box.createVerticalGlue());

		// ===== Portal =====
		JPanel portalBox = createBoxPanel("Portal-Benachrichtigungen", hftRed);
		JTextArea portalArea = new JTextArea();
		portalArea.setEditable(false);
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
}
