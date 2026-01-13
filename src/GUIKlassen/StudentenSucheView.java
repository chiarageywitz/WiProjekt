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
 * JFrame-Klasse für die Studenten-Suche.
 */
public class StudentenSucheView extends JFrame {

    private DefaultListModel<StudentInfo> studentModel;
    private JList<StudentInfo> studentList;
    private JTextArea portalArea;
    private int currentBetreuerMnr; // NEU: MNR des aktuellen Betreuers

    public StudentenSucheView() {
        // NEU: Aktuelle Benutzer-MNR setzen (für Testzwecke, später von Login übernehmen)
        this.currentBetreuerMnr = LoginSession.getCurrentMnr();
        
        setTitle("Studenten-Suche");
        setSize(BaseFrame.WIDTH, BaseFrame.HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color hftRed = UIColors.HFT_RED;
        Color panelBg = UIColors.BACKGROUND;

        // ================= HEADER =================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Studenten-Suche - Betreuer-Portal");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JButton logoutBtn = new JButton("Ausloggen");
        logoutBtn.setBackground(hftRed);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            LoginSession.clear(); // NEU: Session löschen
            new LoginFenster();
            dispose();
        });

        header.add(title, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ================= MAIN =================
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBackground(panelBg);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== LINKE SEITE: STUDENTENSUCHE =====
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(panelBg);

        JTextField searchField = new JTextField();
        JButton suchenBtn = new JButton("Suchen");
        suchenBtn.setPreferredSize(new Dimension(120, 30));
        suchenBtn.setBackground(UIColors.PRIMARY_BLUE);
        suchenBtn.setForeground(Color.WHITE);
        suchenBtn.setFocusPainted(false);
        suchenBtn.setBorderPainted(false);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(panelBg);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(suchenBtn, BorderLayout.EAST);

        studentModel = new DefaultListModel<>();
        studentList = new JList<>(studentModel);
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // NEU: Renderer für bessere Anzeige der Studenteninfo
        studentList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof StudentInfo) {
                    StudentInfo s = (StudentInfo) value;
                    String thema = s.thema != null && !s.thema.isEmpty() ? 
                                   " - " + s.thema : " - Kein Thema";
                    setText(s.name + " (" + s.mnr + ")" + thema);
                }
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentList);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JButton weiterBtn = new JButton("Weiter zum Dashboard");
        weiterBtn.setBackground(hftRed);
        weiterBtn.setForeground(Color.WHITE);
        weiterBtn.setFocusPainted(false);
        weiterBtn.setPreferredSize(new Dimension(200, 40));
        weiterBtn.setFont(new Font("Arial", Font.BOLD, 14));

        // NEU: Button für direkte Anzeige der Benachrichtigungen
        JButton refreshBtn = new JButton("Benachrichtigungen aktualisieren");
        refreshBtn.setBackground(UIColors.PRIMARY_BLUE);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setPreferredSize(new Dimension(220, 35));
        refreshBtn.setFont(new Font("Arial", Font.PLAIN, 12));

        suchenBtn.addActionListener(e -> sucheStudenten(searchField.getText()));
        weiterBtn.addActionListener(e -> oeffneDashboard());
        refreshBtn.addActionListener(e -> ladeBenachrichtigungenFuerBetreuer());

        // Button-Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(panelBg);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(weiterBtn);

        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ===== RECHTE SEITE: PORTAL-BENACHRICHTIGUNGEN =====
        JPanel portalBox = createBoxPanel("Portal-Benachrichtigungen", hftRed);
        portalArea = new JTextArea();
        portalArea.setEditable(false);
        portalArea.setLineWrap(true);
        portalArea.setWrapStyleWord(true);
        portalArea.setFont(new Font("Arial", Font.PLAIN, 12));
        portalArea.setBackground(new Color(250, 250, 250));
        
        JScrollPane portalScroll = new JScrollPane(portalArea);
        portalScroll.setPreferredSize(new Dimension(400, 400));
        portalBox.add(portalScroll);

        // ===== ZUSAMMENBAU =====
        mainPanel.add(leftPanel);
        mainPanel.add(portalBox);

        add(mainPanel, BorderLayout.CENTER);
        
        // NEU: Beim Start alle Studenten laden und Benachrichtigungen anzeigen
        sucheStudenten(""); // Leere Suche zeigt alle Studenten
        ladeBenachrichtigungenFuerBetreuer();
        
        setVisible(true);
    }

    // ================= LOGIK =================
    private void sucheStudenten(String name) {
        try {
            studentModel.clear();
            List<StudentInfo> studenten = StudentDAO.sucheStudenten(name);
            
            if (studenten.isEmpty()) {
                // NEU: Information wenn keine Studenten gefunden
                studentModel.addElement(new StudentInfo(0, "Keine Studenten gefunden", ""));
            } else {
                for (StudentInfo s : studenten) {
                    studentModel.addElement(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Fehler bei der Suche: " + e.getMessage(), 
                "Fehler", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void oeffneDashboard() {
        StudentInfo student = studentList.getSelectedValue();
        if (student == null || student.mnr == 0) {
            JOptionPane.showMessageDialog(this, 
                "Bitte zuerst einen Studenten auswählen!", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new DashboardBetreuerView(student);
        dispose();
    }

    // NEU: Methode um Benachrichtigungen für Betreuer zu laden
    private void ladeBenachrichtigungenFuerBetreuer() {
        try (Connection conn = DBConnection.getConnection()) {
            
            // NEU: Benachrichtigungen für diesen Betreuer laden
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
                    
                    // Erst wenn wir mindestens eine Benachrichtigung haben,
                    // den Betreuernamen laden und anzeigen
                    if (counter == 1) {
                        // Betreuername holen für personalisierte Anzeige
                        String betreuerName = "";
                        String sqlName = "SELECT Vorname, Nachname FROM studentendb WHERE MNR = ?";
                        try (PreparedStatement psName = conn.prepareStatement(sqlName)) {
                            psName.setInt(1, currentBetreuerMnr);
                            ResultSet rsName = psName.executeQuery();
                            if (rsName.next()) {
                                String vorname = rsName.getString("Vorname");
                                String nachname = rsName.getString("Nachname");
                                
                                // NEU: Intelligente Namenszusammenstellung
                                if (vorname != null && nachname != null && !nachname.isEmpty()) {
                                    // Fall 1: Beide Namen vorhanden und Nachname nicht leer
                                    if (!vorname.equals(nachname)) {
                                        betreuerName = vorname + " " + nachname;
                                    } else {
                                        // Fall 2: Vorname und Nachname sind gleich (wahrscheinlich nur Username eingegeben)
                                        betreuerName = vorname;
                                    }
                                } else if (vorname != null) {
                                    // Fall 3: Nur Vorname vorhanden
                                    betreuerName = vorname;
                                } else {
                                    // Fall 4: Kein Name gefunden
                                    betreuerName = "Unbekannt";
                                }
                            }
                        }
                        sb.append("=== Benachrichtigungen für ").append(betreuerName).append(" ===\n\n");
                    }
                    
                    String text = rs.getString("text");
                    String datum = rs.getDate("datum").toString();
                    String vorname = rs.getString("Vorname");
                    String nachname = rs.getString("Nachname");
                    String empfaengerInfo = rs.getString("empfaenger_info");
                    
                    sb.append("[").append(counter).append("] [").append(datum).append("]\n");
                    sb.append("   ").append(text).append("\n");
                    
                    // NEU: Intelligente Sender-Namensanzeige
                    if (vorname != null) {
                        String senderName;
                        if (nachname != null && !nachname.isEmpty() && !vorname.equals(nachname)) {
                            senderName = vorname + " " + nachname;
                        } else {
                            senderName = vorname;
                        }
                        sb.append("   Von: ").append(senderName).append("\n");
                    }
                    
                    sb.append("   Art: ").append(empfaengerInfo).append("\n");
                    sb.append("   ────────────────────\n");
                }
                
                if (counter == 0) {
                    // Keine Benachrichtigungen vorhanden
                    sb.append("Keine neuen Benachrichtigungen.\n");
                    sb.append("────────────────────\n");
                }
                
                portalArea.setText(sb.toString());
                
                // NEU: Optional: Titel anpassen wenn neue Benachrichtigungen da sind
                if (counter > 0) {
                    setTitle("Studenten-Suche - Betreuer-Portal (" + counter + " neue Benachrichtigungen)");
                } else {
                    setTitle("Studenten-Suche - Betreuer-Portal");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            portalArea.setText("Fehler beim Laden der Benachrichtigungen.\n\nFehler: " + e.getMessage());
        }
    }

    // ================= HILFSMETHODEN =================
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

    public static void main(String[] args) {
        // NEU: Für Testzwecke eine Session setzen (in der echten Anwendung geschieht dies beim Login)
        LoginSession.setCurrentUser(2, "betreuer"); // Beispiel: Betreuer mit MNR 2
        
        SwingUtilities.invokeLater(() -> {
            new StudentenSucheView();
        });
    }
}