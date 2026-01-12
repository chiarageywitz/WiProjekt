package GUIKlassen;

import Datenbank.DBConnection;
import Datenbank.StudentDAO.StudentInfo;
import Util.UIColors;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * GUI-Fenster für die Freigabe oder Ablehnung der Bachelorarbeit durch das
 * Unternehmen. Nach Freigabe wird automatisch der Studiendekan zur Genehmigung
 * informiert.
 */
public class FreigabeDerBachelorarbeit extends JFrame {

	private DashboardBetreuerView dashboard;

	private JTextField nameField;
	private JTextField themaField;
	private JTextField betreuerUnternehmenField;
	private JTextField zeitraumField;

	private JCheckBox approve;
	private JCheckBox decline;
	private JTextArea begruendungField;

	/**
	 * Konstruktor. Initialisiert das GUI-Fenster, lädt allgemeine Informationen und
	 * setzt Buttons, Textfelder und Checkboxen.
	 *
	 * @param dashboard Referenz auf das Dashboard des Betreuers
	 */
	public FreigabeDerBachelorarbeit(DashboardBetreuerView dashboard) {
		this.dashboard = dashboard;

		setTitle("Freigabe der Bachelorarbeit");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(700, 700);
		setLocationRelativeTo(null);

		JPanel main = new JPanel(null);
		add(main);

		/* ===== HEADER ===== */
		JPanel headerPanel = new JPanel(null);
		headerPanel.setBounds(10, 10, 660, 35);
		headerPanel.setBackground(Color.WHITE);
		main.add(headerPanel);

		JPanel titlePanel = new JPanel();
		titlePanel.setBackground(UIColors.PRIMARY_BLUE);
		titlePanel.setBounds(0, 0, 360, 35);
		JLabel titleLabel = new JLabel("Freigabe der Bachelorarbeit");
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
		titlePanel.add(titleLabel);
		headerPanel.add(titlePanel);

		/* ===== INFO ===== */
		JTextArea info = new JTextArea("Bitte prüfen Sie und erteilen Sie die Freigabe.\n"
				+ "Nach Ihrer Freigabe wird automatisch der Studiendekan zur Genehmigung gefordert.");
		info.setEditable(false);
		info.setOpaque(false);
		info.setBounds(20, 55, 640, 40);
		main.add(info);

		int y = 110;

		nameField = addLabelTextfield(main, "Name", y);
		y += 35;
		themaField = addLabelTextfield(main, "Thema", y);
		y += 35;
		betreuerUnternehmenField = addLabelTextfield(main, "Betreuer im Unternehmen", y);
		y += 35;
		zeitraumField = addLabelTextfield(main, "Zeitraum", y);
		y += 50;

		/* ===== FREIGABE CHECKBOXEN ===== */
		approve = new JCheckBox("Thema freigeben");
		approve.setBounds(20, y, 200, 30);

		decline = new JCheckBox("Thema ablehnen");
		decline.setBounds(250, y, 200, 30);

		approve.addActionListener(e -> decline.setSelected(false));
		decline.addActionListener(e -> approve.setSelected(false));

		main.add(approve);
		main.add(decline);
		y += 40;

		/* ===== BEGRÜNDUNG ===== */
		JLabel begrLabel = new JLabel("Begründung:");
		begrLabel.setBounds(20, y, 150, 20);
		main.add(begrLabel);

		begruendungField = new JTextArea();
		JScrollPane scroll = new JScrollPane(begruendungField);
		scroll.setBounds(20, y + 25, 640, 150);
		main.add(scroll);

		/* ===== BUTTONS ===== */
		JButton backBottomBtn = new JButton("Zurück");
		backBottomBtn.setBounds(400, 600, 120, 40);
		styleButton(backBottomBtn);
		backBottomBtn.addActionListener(e -> {
			dispose();
			dashboard.setVisible(true);
		});
		main.add(backBottomBtn);

		JButton freigabeBtn = new JButton("Absenden");
		freigabeBtn.setBounds(540, 600, 120, 40);
		styleButton(freigabeBtn);
		freigabeBtn.addActionListener(e -> verarbeiten());
		main.add(freigabeBtn);

		ladeAllgemeineInformationen();
	}

	/**
	 * Verarbeitet die Auswahl des Unternehmens (Freigabe oder Ablehnung), erstellt
	 * Benachrichtigungen und gibt Feedback an den Benutzer.
	 */
	private void verarbeiten() {

		if (!approve.isSelected() && !decline.isSelected()) {
			JOptionPane.showMessageDialog(this, "Bitte wählen Sie »Thema freigeben« oder »Thema ablehnen«!");
			return;
		}

		StudentInfo s = dashboard.getStudent();

		try (Connection conn = DBConnection.getConnection()) {

			if (approve.isSelected()) {
				insertNotification(conn, s.mnr, "Bachelorarbeit wurde vom Betreuer freigegeben.");

				insertNotification(conn, null, "Genehmigung der Bachelorarbeit von " + s.name + " erforderlich.");

				JOptionPane.showMessageDialog(this, "Bachelorarbeit wurde freigegeben.");

			} else {
				String begruendung = begruendungField.getText().trim();
				if (begruendung.isEmpty()) {
					JOptionPane.showMessageDialog(this, "Bitte geben Sie eine Begründung für die Ablehnung an!");
					return;
				}

				insertNotification(conn, s.mnr, "Bachelorarbeit wurde abgelehnt: " + begruendung);

				JOptionPane.showMessageDialog(this, "Bachelorarbeit wurde abgelehnt.");
			}

			dispose();
			dashboard.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Aktion konnte nicht gespeichert werden!");
		}
	}

	/**
	 * Fügt eine Benachrichtigung in die Datenbank ein.
	 *
	 * @param conn Offene DB-Verbindung
	 * @param mnr  Matrikelnummer des Studenten oder NULL für Studiendekan
	 * @param text Nachrichtentext
	 * @throws Exception bei DB-Fehlern
	 */
	private void insertNotification(Connection conn, Integer mnr, String text) throws Exception {
		PreparedStatement ps = conn
				.prepareStatement("INSERT INTO benachrichtigungen (mnr, text, datum) VALUES (?, ?, CURRENT_DATE)");
		if (mnr == null)
			ps.setNull(1, java.sql.Types.INTEGER);
		else
			ps.setInt(1, mnr);

		ps.setString(2, text);
		ps.executeUpdate();
	}

	/**
	 * Lädt allgemeine Informationen zur Bachelorarbeit des Studenten aus der
	 * Datenbank und füllt die entsprechenden Felder.
	 */
	private void ladeAllgemeineInformationen() {
		try {
			StudentInfo s = dashboard.getStudent();
			nameField.setText(s.name);

			String sql = """
					    SELECT thema, zeitraum, betreuer_unternehmen
					    FROM allgemeine_informationen
					    WHERE mnr = ?
					""";

			try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

				ps.setInt(1, s.mnr);
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					themaField.setText(rs.getString("thema"));
					zeitraumField.setText(rs.getString("zeitraum"));
					betreuerUnternehmenField.setText(rs.getString("betreuer_unternehmen"));
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Fehler beim Laden der Daten");
		}
	}

	/**
	 * Hilfsmethode zum Hinzufügen eines Labels und eines Textfeldes zum Panel.
	 *
	 * @param panel Zielpanel
	 * @param label Text des Labels
	 * @param y     Vertikale Position
	 * @return JTextField das erzeugt wurde
	 */
	private JTextField addLabelTextfield(JPanel panel, String label, int y) {
		JLabel l = new JLabel(label + ":");
		l.setBounds(20, y, 200, 25);
		panel.add(l);

		JTextField tf = new JTextField();
		tf.setBounds(220, y, 430, 25);
		panel.add(tf);
		return tf;
	}

	/**
	 * Stilisiert einen JButton nach den Vorgaben des UI-Designs.
	 *
	 * @param button Button, der gestylt werden soll
	 */
	private void styleButton(JButton button) {
		button.setBackground(UIColors.PRIMARY_BLUE);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setOpaque(true);
	}
}
