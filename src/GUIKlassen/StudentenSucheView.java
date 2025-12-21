package GUIKlassen;

import Datenbank.StudentDAO;
import Datenbank.StudentDAO.StudentInfo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * JFrame-Klasse für die Studenten-Suche. Ermöglicht die Suche nach Studenten
 * nach Name und öffnet das Dashboard des Betreuers für den ausgewählten
 * Studenten.
 */
public class StudentenSucheView extends JFrame {

	private DefaultListModel<StudentInfo> studentModel;
	private JList<StudentInfo> studentList;

	/**
	 * Konstruktor für die GUI der Studenten-Suche. Baut die Suchmaske auf und lädt
	 * Studenten aus der Datenbank.
	 */
	public StudentenSucheView() {
		setTitle("Studenten-Suche");
		setSize(800, 450);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Color hftRed = new Color(204, 0, 0);
		Color panelBg = new Color(245, 245, 245);

		// ================= HEADER =================
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(Color.WHITE);
		header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel title = new JLabel("Studenten-Suche");
		title.setFont(new Font("Arial", Font.BOLD, 22));

		JButton logoutBtn = new JButton("Ausloggen");
		logoutBtn.setBackground(hftRed);
		logoutBtn.setForeground(Color.WHITE);
		logoutBtn.setFocusPainted(false);
		logoutBtn.addActionListener(e -> {
			new LoginFenster();
			dispose();
		});

		header.add(title, BorderLayout.WEST);
		header.add(logoutBtn, BorderLayout.EAST);
		add(header, BorderLayout.NORTH);

		// ================= MAIN =================
		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBackground(panelBg);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		JTextField searchField = new JTextField();
		JButton suchenBtn = new JButton("Suchen");
		suchenBtn.setPreferredSize(new Dimension(100, 30));

		JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
		searchPanel.add(searchField, BorderLayout.CENTER);
		searchPanel.add(suchenBtn, BorderLayout.EAST);

		studentModel = new DefaultListModel<>();
		studentList = new JList<>(studentModel);
		JScrollPane scrollPane = new JScrollPane(studentList);

		JButton weiterBtn = new JButton("Weiter");
		weiterBtn.setBackground(hftRed);
		weiterBtn.setForeground(Color.WHITE);
		weiterBtn.setFocusPainted(false);

		suchenBtn.addActionListener(e -> sucheStudenten(searchField.getText()));
		weiterBtn.addActionListener(e -> öffneDashboard());

		mainPanel.add(searchPanel, BorderLayout.NORTH);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(weiterBtn, BorderLayout.SOUTH);

		add(mainPanel, BorderLayout.CENTER);
		setVisible(true);
	}

	/**
	 * Führt die Suche nach Studenten anhand des eingegebenen Namens aus.
	 *
	 * @param name Suchtext.
	 */
	private void sucheStudenten(String name) {
		try {
			studentModel.clear();
			List<StudentInfo> studenten = StudentDAO.sucheStudenten(name);
			for (StudentInfo s : studenten) {
				studentModel.addElement(s);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Fehler bei der Suche");
		}
	}

	/**
	 * Öffnet das Dashboard für den ausgewählten Studenten.
	 */
	private void öffneDashboard() {
		StudentInfo student = studentList.getSelectedValue();
		if (student == null) {
			JOptionPane.showMessageDialog(this, "Bitte zuerst einen Studenten auswählen!");
			return;
		}
		new DashboardBetreuerView(student);
		dispose();
	}
	public static void main(String[] args) {
		new StudentenSucheView();
	}
}
