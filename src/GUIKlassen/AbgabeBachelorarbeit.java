package GUIKlassen;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import Datenbank.VersionDAO;
import Datenbank.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Panel für den Upload der Bachelorarbeit. Enthält Upload-Feld, Beschreibung,
 * Hochladen-Button und Zurück-Button.
 */
public class AbgabeBachelorarbeit extends JPanel {

	private static final long serialVersionUID = 1L;
	private int mnr;
	private File selectedFile;

	/**
	 * Konstruktor für das Upload-Panel.
	 * @param mnr Matrikelnummer
	 */
	public AbgabeBachelorarbeit(int mnr) {
		this.mnr = mnr;

		setLayout(null);
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));

		// ===== Titel =====
		JLabel title = new JLabel("Bachelorarbeit");
		title.setOpaque(true);
		title.setBackground(new Color(0, 102, 204)); // Blau
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Arial", Font.BOLD, 16));
		title.setBounds(10, 0, 150, 35);
		add(title);

		int y = 55;

		// ===== Datei auswählen Titel =====
		JLabel fileTitle = new JLabel("Datei auswählen (jpg, docx, pdf, usw)");
		fileTitle.setFont(new Font("Arial", Font.PLAIN, 13));
		fileTitle.setBounds(20, y, 300, 20);
		add(fileTitle);

		y += 35;

		// ===== Upload-Feld =====
		JPanel dropPanel = new JPanel(null);
		dropPanel.setBounds(20, y, 500, 120);
		dropPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		dropPanel.setBackground(Color.WHITE);
		add(dropPanel);

		JLabel uploadLabel = new JLabel(
				"<html><center>⬆️<br>Datei auswählen<br><span style='font-size:10px;'>Drag and drop files here</span></center></html>",
				SwingConstants.CENTER);
		uploadLabel.setBounds(0, 0, 500, 120);
		uploadLabel.setForeground(Color.GRAY);
		dropPanel.add(uploadLabel);

		JButton chooseBtn = new JButton();
		chooseBtn.setBounds(0, 0, 500, 120);
		chooseBtn.setOpaque(false);
		chooseBtn.setContentAreaFilled(false);
		chooseBtn.setBorderPainted(false);
		dropPanel.add(chooseBtn);

		chooseBtn.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Datei auswählen");
			chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
				"Unterstützte Dateien (PDF, DOCX)", "pdf", "docx"));

			int result = chooser.showOpenDialog(this);

			if (result == JFileChooser.APPROVE_OPTION) {
				selectedFile = chooser.getSelectedFile();
				uploadLabel.setText("<html><center><b>" + selectedFile.getName() + "</b></center></html>");
			}
		});

		y += 150;

		// ===== Beschreibung =====
		JLabel beschrLabel = new JLabel("Beschreibung");
		beschrLabel.setFont(new Font("Arial", Font.PLAIN, 13));
		beschrLabel.setBounds(20, y, 200, 20);
		add(beschrLabel);

		y += 30;

		JTextArea beschreibung = new JTextArea();
		beschreibung.setLineWrap(true);
		beschreibung.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane(beschreibung);
		scroll.setBounds(20, y, 500, 120);
		add(scroll);

		y += 150;

		// ===== Hochladen Button =====
		JButton uploadBtn = new JButton("Hochladen");
		uploadBtn.setBounds(200, y, 150, 40);
		uploadBtn.setBackground(new Color(0, 102, 204));
		uploadBtn.setForeground(Color.WHITE);
		uploadBtn.setFont(new Font("Arial", Font.BOLD, 14));
		uploadBtn.setBorderPainted(false);
		uploadBtn.setOpaque(true);
		add(uploadBtn);

		uploadBtn.addActionListener(e -> {
			if (selectedFile == null) {
				JOptionPane.showMessageDialog(this,
					"Bitte wählen Sie zuerst eine Datei aus!",
					"Keine Datei", JOptionPane.WARNING_MESSAGE);
				return;
			}

			try {
				// Betreuer-MNR holen
				Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(
					"SELECT betreuer_mnr FROM antraege WHERE student_mnr = ? AND status = 'dekan_genehmigt' LIMIT 1"
				);
				ps.setInt(1, mnr);
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					int betreuerMnr = rs.getInt("betreuer_mnr");
					String kommentar = beschreibung.getText();

					// Finale Abgabe speichern
					VersionDAO.finaleAbgabe(mnr, betreuerMnr, selectedFile.getAbsolutePath(), kommentar);

					JOptionPane.showMessageDialog(this,
						"<html><center>Ihre Bachelorarbeit wurde erfolgreich abgegeben!<br>" +
						"Ihr Betreuer und Sie wurden benachrichtigt.<br>" +
						"Sie erhalten nach der Notenvergabe eine Benachrichtigung.</center></html>",
						"Abgabe erfolgreich", JOptionPane.INFORMATION_MESSAGE);

					new DashboardStudent(mnr);
					SwingUtilities.getWindowAncestor(this).dispose();
				} else {
					JOptionPane.showMessageDialog(this,
						"Kein Betreuer gefunden. Bitte kontaktieren Sie das Prüfungsamt.",
						"Fehler", JOptionPane.ERROR_MESSAGE);
				}
				conn.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this,
					"Fehler beim Hochladen: " + ex.getMessage(),
					"Fehler", JOptionPane.ERROR_MESSAGE);
			}
		});

		// =========================
		// Zurück Button
		// =========================
		JButton backBtn = new JButton("Zurück");
		backBtn.setBounds(20, y + 60, 120, 35);
		backBtn.setBackground(new Color(0, 102, 204));
		backBtn.setForeground(Color.WHITE);
		backBtn.setFont(new Font("Arial", Font.BOLD, 13));
		backBtn.setBorderPainted(false);
		backBtn.setOpaque(true);
		add(backBtn);

		backBtn.addActionListener(e -> {
			new DashboardStudent(mnr); // Dashboard öffnen
			SwingUtilities.getWindowAncestor(this).dispose(); // Fenster schließen
		});
	}

	// Test
	/**
	 * Test-Main für das Upload-Panel.
	 * @param args Argumente
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Bachelorarbeit Upload");
		frame.setSize(560, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new AbgabeBachelorarbeit(4711));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
