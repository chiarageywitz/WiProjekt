package GUIKlassen;

import Datenbank.StudentDAO.StudentInfo;
import GUIKlassen.Noteneingabe;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import Datenbank.DBConnection;
import Datenbank.StudentDAO;
import GUIKlassen.GenehmigungDerBachelorarbeitStudiendekan;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Haupt-Dashboard für den Studiendekan.
 * Zeigt Studentenübersicht, Notenliste,
 * Genehmigungs- und Noteneingabefunktionen sowie Portal-Benachrichtigungen.
 */
public class DashboardStudiendekan extends JFrame {

	private DashboardStudiendekan dashboard;

	/**
	 * Konstruktor für das Studiendekan-Dashboard.
	 * 
	 * @param dashboard Referenz auf sich selbst, für Navigation zwischen Fenstern.
	 */
	public DashboardStudiendekan(DashboardStudiendekan dashboard) {
		this.dashboard = dashboard;

		setTitle("Studiendekan - Übersicht");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1400, 650);
		setLocationRelativeTo(null);

		Color hftRed = new Color(226, 0, 26);
		Color hftBlue = new Color(0, 45, 150);
		Color panelBg = new Color(240, 240, 240);
		Color grayButton = new Color(230, 230, 230);

		// --- Top Bar ---
		JPanel topBar = new JPanel(new BorderLayout());
		topBar.setBackground(Color.WHITE);
		topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

		JLabel title = new JLabel("Studiendekan-Portal");
		title.setFont(new Font("Arial", Font.BOLD, 20));
		topBar.add(title, BorderLayout.WEST);

		JButton logoutBtn = new JButton("Ausloggen");
		logoutBtn.setBackground(hftRed);
		logoutBtn.setForeground(Color.WHITE);
		logoutBtn.setOpaque(true);
		logoutBtn.setFocusPainted(false);
		logoutBtn.setBorderPainted(false);
		logoutBtn.setPreferredSize(new Dimension(120, 36));
		logoutBtn.addActionListener(e -> System.exit(0));
		topBar.add(logoutBtn, BorderLayout.EAST);

		add(topBar, BorderLayout.NORTH);

		// --- Center Panels ---
		JPanel center = new JPanel(new GridLayout(1, 4, 15, 0));
		center.setBackground(panelBg);
		center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// --- 1) Studenten-Suche ---
		JPanel pStudenten = createCardPanel("Studenten-Suche", hftRed);
		JPanel p1Inner = new JPanel();
		p1Inner.setBackground(Color.WHITE);
		p1Inner.setLayout(new BoxLayout(p1Inner, BoxLayout.Y_AXIS));
		p1Inner.add(Box.createVerticalStrut(30));

		// --- Tabelle mit Studenten aus der DB ---
		String[] spalten = { "Matrikelnummer", "Name", "Thema" };
		DefaultTableModel model = new DefaultTableModel(spalten, 0);
		JTable table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(600, 300));
		p1Inner.add(scrollPane);

		// --- Liste aller Studenten für Filterung ---
		List<StudentInfo> allStudents = new java.util.ArrayList<>();
		try {
			List<StudentInfo> studentenListe = StudentDAO.getAllStudents();
			allStudents.addAll(studentenListe);

			for (StudentInfo s : allStudents) {
				model.addRow(new Object[] { s.mnr, s.name, s.thema });
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Fehler beim Laden der Studentendaten!");
		}

		// --- Suchfeld ---
		JTextField searchField = new JTextField("Suche nach Studierenden…");
		searchField.setMaximumSize(new Dimension(220, 36)); // schmaler
		searchField.setForeground(Color.GRAY);
		searchField.setFont(new Font("Arial", Font.PLAIN, 14));
		searchField.setAlignmentX(Component.CENTER_ALIGNMENT);
		searchField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (searchField.getText().equals("Suche nach Studierenden…")) {
					searchField.setText("");
					searchField.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (searchField.getText().isEmpty()) {
					searchField.setText("Suche nach Studierenden…");
					searchField.setForeground(Color.GRAY);
				}
			}
		});
		p1Inner.add(Box.createVerticalStrut(10));
		p1Inner.add(searchField);

		// --- Buchstabenleiste A-Z ---
		// --- Buchstaben-Buttons horizontal scrollen ---
		JPanel lettersPanel = new JPanel();
		lettersPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

		char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		for (char c : alphabet) {
			JButton btn = new JButton(String.valueOf(c));
			btn.setMargin(new Insets(2, 6, 2, 6));
			btn.setFocusPainted(false);
			btn.setBackground(new Color(0, 45, 150));
			btn.setForeground(Color.WHITE);
			btn.setOpaque(true);
			btn.setBorderPainted(false);

			btn.addActionListener(e -> {
				String letter = btn.getText();
				filterTableByLetter(table, allStudents, letter);
			});

			lettersPanel.add(btn);
		}

		// ScrollPane für Buchstaben
		JScrollPane lettersScroll = new JScrollPane(lettersPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		lettersScroll.setPreferredSize(new Dimension(400, 40));
		lettersScroll.setBorder(null);

		p1Inner.add(Box.createVerticalStrut(10));
		p1Inner.add(lettersScroll);

		// --- Live-Suche ---
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				filterTable(model, allStudents, searchField.getText());
			}

			public void removeUpdate(DocumentEvent e) {
				filterTable(model, allStudents, searchField.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				filterTable(model, allStudents, searchField.getText());
			}
		});

		p1Inner.add(Box.createVerticalGlue());
		pStudenten.add(p1Inner, BorderLayout.CENTER);

		// --- 2) Notenliste ---
		JPanel pNoten = createCardPanel("Notenliste", hftRed);
		JPanel p2Inner = new JPanel();
		p2Inner.setBackground(Color.WHITE);
		p2Inner.setLayout(new BoxLayout(p2Inner, BoxLayout.Y_AXIS));
		p2Inner.add(Box.createVerticalStrut(60));
		JButton openGrades = new JButton("Hier klicken, um die Notenliste zu öffnen");
		openGrades.setBackground(grayButton);
		openGrades.setForeground(Color.DARK_GRAY);
		openGrades.setFocusPainted(false);
		openGrades.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
		openGrades.setMaximumSize(new Dimension(400, 44));
		openGrades.setAlignmentX(Component.CENTER_ALIGNMENT);
		openGrades.addActionListener(e -> SwingUtilities.invokeLater(() -> {
			NotenlisteStudiendekan notenliste = new NotenlisteStudiendekan(DashboardStudiendekan.this);
			notenliste.setVisible(true);
			DashboardStudiendekan.this.setVisible(false);
		}));
		p2Inner.add(openGrades);
		p2Inner.add(Box.createVerticalGlue());
		pNoten.add(p2Inner, BorderLayout.CENTER);

		// --- 3) Meine Funktionen ---
		JPanel pFunk = createCardPanel("Meine Funktionen", hftRed);
		JPanel p3Inner = new JPanel();
		p3Inner.setBackground(Color.WHITE);
		p3Inner.setLayout(new BoxLayout(p3Inner, BoxLayout.Y_AXIS));
		p3Inner.add(Box.createVerticalGlue());

		JButton btnGenehmigen = new JButton("Genehmigung erteilen");
		btnGenehmigen.setBackground(hftBlue);
		btnGenehmigen.setForeground(Color.WHITE);
		btnGenehmigen.setOpaque(true);
		btnGenehmigen.setFocusPainted(false);
		btnGenehmigen.setBorderPainted(false);
		btnGenehmigen.setMaximumSize(new Dimension(300, 60));
		btnGenehmigen.setPreferredSize(new Dimension(300, 60));
		btnGenehmigen.setAlignmentX(Component.CENTER_ALIGNMENT);

		btnGenehmigen.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow == -1) {
				JOptionPane.showMessageDialog(DashboardStudiendekan.this,
						"Bitte zuerst einen Studenten in der Tabelle auswählen!");
				return;
			}
			int mnr = (int) table.getValueAt(selectedRow, 0);
			StudentInfo student = null;
			try {
				student = StudentDAO.getStudentInfo(mnr);
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(DashboardStudiendekan.this, "Fehler beim Laden der Studentendaten!");
				return;
			}
			if (student == null) {
				JOptionPane.showMessageDialog(DashboardStudiendekan.this, "Student konnte nicht gefunden werden!");
				return;
			}
			GenehmigungDerBachelorarbeitStudiendekan genehmigung = new GenehmigungDerBachelorarbeitStudiendekan(
					DashboardStudiendekan.this, student);
			genehmigung.setVisible(true);
			DashboardStudiendekan.this.setVisible(false);
		});
		p3Inner.add(btnGenehmigen);
		p3Inner.add(Box.createVerticalStrut(30));

		JButton btnNoteneingabe = new JButton("Noteneingabe");
		btnNoteneingabe.setBackground(hftBlue);
		btnNoteneingabe.setForeground(Color.WHITE);
		btnNoteneingabe.setOpaque(true);
		btnNoteneingabe.setFocusPainted(false);
		btnNoteneingabe.setBorderPainted(false);
		btnNoteneingabe.setMaximumSize(new Dimension(300, 60));
		btnNoteneingabe.setPreferredSize(new Dimension(300, 60));
		btnNoteneingabe.setAlignmentX(Component.CENTER_ALIGNMENT);

		btnNoteneingabe.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow == -1) {
				JOptionPane.showMessageDialog(DashboardStudiendekan.this,
						"Bitte zuerst einen Studenten in der Tabelle auswählen!");
				return;
			}
			int mnr = (int) table.getValueAt(selectedRow, 0);
			StudentInfo student = null;
			try {
				student = StudentDAO.getStudentInfo(mnr);
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(DashboardStudiendekan.this, "Fehler beim Laden der Studentendaten!");
				return;
			}
			if (student == null) {
				JOptionPane.showMessageDialog(DashboardStudiendekan.this, "Student konnte nicht gefunden werden!");
				return;
			}
			Noteneingabe noteneingabe = new Noteneingabe(student, "studiendekan", DashboardStudiendekan.this);
			noteneingabe.setVisible(true);
			DashboardStudiendekan.this.setVisible(false);
		});
		p3Inner.add(btnNoteneingabe);
		p3Inner.add(Box.createVerticalGlue());
		pFunk.add(p3Inner, BorderLayout.CENTER);

		// --- 4) Portal-Benachrichtigungen ---
		JPanel pPortal = createCardPanel("Portal-Benachrichtigungen", hftRed);
		JPanel p4Inner = new JPanel();
		p4Inner.setBackground(Color.WHITE);
		p4Inner.setLayout(new BoxLayout(p4Inner, BoxLayout.Y_AXIS));
		p4Inner.add(Box.createVerticalStrut(18));
		JLabel msgIcon = new JLabel("💬");
		msgIcon.setFont(new Font("Arial", Font.PLAIN, 28));
		msgIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
		p4Inner.add(msgIcon);
		p4Inner.add(Box.createVerticalStrut(10));
		JLabel noMsgs = new JLabel("Keine neuen Benachrichtigungen");
		noMsgs.setFont(new Font("Arial", Font.PLAIN, 13));
		noMsgs.setForeground(Color.DARK_GRAY);
		noMsgs.setAlignmentX(Component.CENTER_ALIGNMENT);
		p4Inner.add(noMsgs);
		p4Inner.add(Box.createVerticalGlue());
		pPortal.add(p4Inner, BorderLayout.CENTER);

		// --- Panels zum Center hinzufügen ---
		center.add(pStudenten);
		center.add(pNoten);
		center.add(pFunk);
		center.add(pPortal);
		add(center, BorderLayout.CENTER);
		setVisible(true);
	}

	// --- Filter Methoden ---
	/**
	 * Filtert die Studententabelle nach einem eingegebenen Text.
	 * 
	 * @param model       TableModel der JTable.
	 * @param allStudents Liste aller Studenten.
	 * @param text        Suchtext.
	 */
	private void filterTable(DefaultTableModel model, List<StudentInfo> allStudents, String text) {
		text = text.toLowerCase();
		if (text.equals("suche nach studierenden…"))
			text = "";
		model.setRowCount(0);
		for (StudentInfo s : allStudents) {
			if (s.name.toLowerCase().contains(text)) {
				model.addRow(new Object[] { s.mnr, s.name, s.thema });
			}
		}
	}

	/**
	 * Filtert die Studententabelle nach dem Anfangsbuchstaben.
	 * 
	 * @param model       TableModel der JTable.
	 * @param allStudents Liste aller Studenten.
	 * @param letter      Anfangsbuchstabe. für die Filterung.
	 */
	private void filterByLetter(DefaultTableModel model, List<StudentInfo> allStudents, char letter) {
		model.setRowCount(0);
		for (StudentInfo s : allStudents) {
			if (s.name.toUpperCase().startsWith(String.valueOf(letter))) {
				model.addRow(new Object[] { s.mnr, s.name, s.thema });
			}
		}
	}

	/**
	 * Erstellt ein farbiges Panel mit Header für Dashboard-Karten.
	 * 
	 * @param headerText  Text des Headers.
	 * @param headerColor Hintergrundfarbe des Headers.
	 * @return JPanel mit Header.
	 */
	private JPanel createCardPanel(String headerText, Color headerColor) {
		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));

		JLabel header = new JLabel(headerText, SwingConstants.CENTER);
		header.setOpaque(true);
		header.setBackground(headerColor);
		header.setForeground(Color.WHITE);
		header.setFont(new Font("Arial", Font.BOLD, 14));
		header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		card.add(header, BorderLayout.NORTH);
		return card;
	}

	/**
	 * Filtert die Studententabelle nach dem Anfangsbuchstaben des Namens. Diese
	 * Methode wird von der Buchstabenleiste (A-Z) aufgerufen. Zeigt nur Studenten,
	 * deren Name mit dem angegebenen Buchstaben beginnt.
	 *
	 * @param table       JTable, die gefiltert werden soll.
	 * @param allStudents Liste aller Studenten, die als Basis für die Filterung dient.
	 * @param letter      Der Buchstabe, nach dem gefiltert werden soll (Großbuchstabe empfohlen).
	 */
	private void filterTableByLetter(JTable table, List<StudentInfo> allStudents, String letter) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setRowCount(0);

		for (StudentInfo s : allStudents) {
			if (s.name != null && s.name.toUpperCase().startsWith(letter)) {
				model.addRow(new Object[] { s.mnr, s.name, s.thema });
			}
		}
	}

	/**
	 * Main-Methode zum Starten des Dashboards für den Studiendekan.
	 * 
	 * @param args Kommandozeilenargumente (werden nicht verwendet).
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new DashboardStudiendekan(null).setVisible(true));
	}
}
