package GUIKlassen;

import Datenbank.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NotenlisteStudiendekan extends JFrame {

	private DashboardStudiendekan dashboard;

	public NotenlisteStudiendekan(DashboardStudiendekan dashboard) {
		this.dashboard = dashboard;

		setTitle("Notenliste");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(850, 500);
		setLocationRelativeTo(null);

		JPanel main = new JPanel();
		main.setLayout(null);
		add(main);

		// Header-Balken
		JPanel header = new JPanel();
		header.setBackground(new Color(0, 45, 150));
		header.setBounds(10, 10, 150, 35);
		JLabel headerLabel = new JLabel("Notenliste");
		headerLabel.setForeground(Color.WHITE);
		headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
		header.add(headerLabel);
		main.add(header);

		int y = 60;

		JLabel pruefungLabel = new JLabel("Prüfung");
		pruefungLabel.setBounds(20, y, 200, 20);
		main.add(pruefungLabel);

		JTextField pruefungField = new JTextField();
		pruefungField.setBounds(20, y + 25, 250, 30);
		main.add(pruefungField);

		JLabel semLabel = new JLabel("Semester");
		semLabel.setBounds(300, y, 200, 20);
		main.add(semLabel);

		JTextField semField = new JTextField();
		semField.setBounds(300, y + 25, 80, 30);
		main.add(semField);

		JButton filterBtn = new JButton("Filter anwenden");
		filterBtn.setBounds(500, y + 15, 150, 35);
		filterBtn.setBackground(new Color(0, 45, 150));
		filterBtn.setForeground(Color.WHITE);
		filterBtn.setFocusPainted(false);
		filterBtn.setBorderPainted(false);
		filterBtn.setOpaque(true);
		main.add(filterBtn);

		y += 90;

		String[] columnNames = { "Matrikelnummer", "Name, Vorname", "Note", "Bestanden" };

		DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return (columnIndex == 3) ? Boolean.class : String.class;
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // reine Anzeige
			}
		};

		JTable table = new JTable(model);
		table.setRowHeight(28);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(20, y, 780, 250);
		main.add(scrollPane);

		JButton speichernBtn = new JButton("Speichern");
		speichernBtn.setBounds(650, 420, 150, 35);
		speichernBtn.setBackground(new Color(0, 45, 150));
		speichernBtn.setForeground(Color.WHITE);
		speichernBtn.setFocusPainted(false);
		speichernBtn.setBorderPainted(false);
		speichernBtn.setOpaque(true);
		main.add(speichernBtn);

		JButton zurueckBtn = new JButton("Zurück");
		zurueckBtn.setBounds(500, 420, 120, 35);
		zurueckBtn.setBackground(new Color(0, 45, 150));
		zurueckBtn.setForeground(Color.WHITE);
		zurueckBtn.setFocusPainted(false);
		zurueckBtn.setBorderPainted(false);
		zurueckBtn.setOpaque(true);
		main.add(zurueckBtn);

		zurueckBtn.addActionListener(e -> {
			dispose();
			if (dashboard != null) {
				dashboard.setVisible(true);
			}
		});

		// 🔥 HIER: Daten aus der DB laden
		ladeNotenAusDatenbank(model);
	}

	// =========================
	// NUR DIESE METHODE IST NEU
	// =========================
	private void ladeNotenAusDatenbank(DefaultTableModel model) {
		model.setRowCount(0); // alte / Beispiel-Daten löschen

		try (Connection conn = DBConnection.getConnection()) {
			String sql = "SELECT s.MNR, s.Nachname, s.Vorname, n.note_studiendekan " + "FROM studentendb s "
					+ "LEFT JOIN noten n ON s.MNR = n.mnr";

			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int mnr = rs.getInt("MNR"); // ✅ Großbuchstaben wie in DB
				String name = rs.getString("Nachname") + ", " + rs.getString("Vorname"); // ✅ Großbuchstaben
				Double note = rs.getObject("note_studiendekan", Double.class);
				boolean bestanden = note != null && note <= 4.0;

				model.addRow(new Object[] { mnr, name, note != null ? note.toString() : "", bestanden });
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Fehler beim Laden der Noten aus der Datenbank!\n\n" + ex.getMessage());
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new NotenlisteStudiendekan(null).setVisible(true));
	}
}
