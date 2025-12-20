package GUIKlassen;

import Datenbank.StudentDAO.StudentInfo;
import GUIKlassen.Noteneingabe;

import javax.swing.*;
import java.awt.*;

import Datenbank.DBConnection;
import Datenbank.StudentDAO;
import GUIKlassen.GenehmigungDerBachelorarbeitStudiendekan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class DashboardStudiendekan extends JFrame {

	private DashboardStudiendekan dashboard;

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
		String[] spalten = {"Matrikelnummer", "Name", "Thema"};
		Object[][] daten;

		try {
		    // Studentendaten aus DB laden
		    java.util.List<StudentInfo> studentenListe = StudentDAO.getAllStudents();
		    daten = new Object[studentenListe.size()][3];
		    for (int i = 0; i < studentenListe.size(); i++) {
		        StudentInfo s = studentenListe.get(i);
		        daten[i][0] = s.mnr;     // Matrikelnummer
		        daten[i][1] = s.name;    // Name
		        daten[i][2] = s.thema;   // Thema
		    }
		} catch (Exception ex) {
		    ex.printStackTrace();
		    JOptionPane.showMessageDialog(this, "Fehler beim Laden der Studentendaten!");
		    daten = new Object[0][3]; // leere Tabelle bei Fehler
		}

		JTable table = new JTable(daten, spalten);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(600, 300));
		p1Inner.add(scrollPane);
		pStudenten.add(p1Inner, BorderLayout.CENTER);

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
		p1Inner.add(searchField);
		p1Inner.add(Box.createVerticalStrut(15));

		JLabel letters = new JLabel("A B C D E F G H I J K L M N O P Q R S T U V W X Y Z");
		letters.setFont(new Font("Arial", Font.PLAIN, 12));
		letters.setForeground(Color.DARK_GRAY);
		letters.setAlignmentX(Component.CENTER_ALIGNMENT);
		p1Inner.add(Box.createVerticalGlue());
		p1Inner.add(letters);
		p1Inner.add(Box.createVerticalStrut(18));
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
			DashboardStudiendekan.this.setVisible(false); // Dashboard ausblenden
		}));
		p2Inner.add(openGrades);
		p2Inner.add(Box.createVerticalGlue());
		pNoten.add(p2Inner, BorderLayout.CENTER);

		// --- 3) Meine Funktionen ---
		JPanel pFunk = createCardPanel("Meine Funktionen", hftRed);
		JPanel p3Inner = new JPanel();
		p3Inner.setBackground(Color.WHITE);
		p3Inner.setLayout(new BoxLayout(p3Inner, BoxLayout.Y_AXIS));
		p3Inner.add(Box.createVerticalGlue()); // oben flexible Lücke für Zentrierung

		// Genehmigung Button
		JButton btnGenehmigen = new JButton("Genehmigung erteilen");
		btnGenehmigen.setBackground(hftBlue);
		btnGenehmigen.setForeground(Color.WHITE);
		btnGenehmigen.setOpaque(true);
		btnGenehmigen.setFocusPainted(false);
		btnGenehmigen.setBorderPainted(false);
		btnGenehmigen.setMaximumSize(new Dimension(300, 60));
		btnGenehmigen.setPreferredSize(new Dimension(300, 60));
		btnGenehmigen.setAlignmentX(Component.CENTER_ALIGNMENT);

		btnGenehmigen.addActionListener(e -> SwingUtilities.invokeLater(() -> {
			GenehmigungDerBachelorarbeitStudiendekan genehmigung = new GenehmigungDerBachelorarbeitStudiendekan(
					DashboardStudiendekan.this);
			genehmigung.setVisible(true);
			DashboardStudiendekan.this.setVisible(false);
		}));

		p3Inner.add(btnGenehmigen);

		p3Inner.add(Box.createVerticalStrut(30)); // Abstand zwischen Buttons

		// Noteneingabe Button
		JButton btnNoteneingabe = new JButton("Noteneingabe");
		btnNoteneingabe.setBackground(hftBlue);
		btnNoteneingabe.setForeground(Color.WHITE);
		btnNoteneingabe.setOpaque(true);
		btnNoteneingabe.setFocusPainted(false);
		btnNoteneingabe.setBorderPainted(false);
		btnNoteneingabe.setMaximumSize(new Dimension(300, 60));
		btnNoteneingabe.setPreferredSize(new Dimension(300, 60));
		btnNoteneingabe.setAlignmentX(Component.CENTER_ALIGNMENT);
//		btnNoteneingabe.addActionListener(e -> {
//			NotenlisteStudiendekan liste = new NotenlisteStudiendekan(DashboardStudiendekan.this);
//			liste.setVisible(true);
//			DashboardStudiendekan.this.setVisible(false);
//		});
		btnNoteneingabe.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow == -1) {
				JOptionPane.showMessageDialog(DashboardStudiendekan.this,
						"Bitte zuerst einen Studenten in der Tabelle auswählen!");
				return;
			}
			// Matrikelnummer aus der Tabelle holen (Spalte 0 anpassen, falls nötig)
			int mnr = (int) table.getValueAt(selectedRow, 0);
			// StudentInfo aus der Datenbank laden
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
			// Noteneingabe-Fenster öffnen
			Noteneingabe noteneingabe = new Noteneingabe(student, "studiendekan", DashboardStudiendekan.this);
			noteneingabe.setVisible(true);
			// Dashboard ausblenden
			DashboardStudiendekan.this.setVisible(false);
		});

		p3Inner.add(btnNoteneingabe);

		p3Inner.add(Box.createVerticalGlue()); // unten flexible Lücke für Zentrierung
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

	private static class NoteneingabeFenster extends JFrame {
		NoteneingabeFenster() {
			setTitle("Noteneingabe");
			setSize(520, 380);
			setLocationRelativeTo(null);
			add(new JLabel("Noteneingabe-Fenster (Eingabefelder kommen später)", SwingConstants.CENTER));
			setVisible(true);
		}
	}

	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new DashboardStudiendekan(null).setVisible(true);
		});
	}

}