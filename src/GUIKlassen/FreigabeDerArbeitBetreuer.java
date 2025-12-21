package GUIKlassen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FreigabeDerArbeitBetreuer extends JFrame {

    public FreigabeDerArbeitBetreuer() {
        setTitle("Freigabe der Arbeit");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        /* ===== ÜBERSCHRIFT ===== */
        JLabel title = new JLabel("Freigabe der Arbeit");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(30, 90, 200));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(title);

        mainPanel.add(Box.createVerticalStrut(15));

        /* ===== TABELLE ===== */
        Object[][] data = {
                {"Name", ""},
                {"Thema", ""},
                {"Betreuer", ""},
                {"Abgabedatum", ""},
                {"Kurzfassung", ""},
                {"Bachelorarbeit", ""}
        };

        DefaultTableModel model = new DefaultTableModel(data, new Object[]{"", ""}) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 1;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);
        table.setTableHeader(null);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        table.getColumnModel().getColumn(0).setPreferredWidth(250);

        JScrollPane tablePane = new JScrollPane(
                table,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        // 🔥 ENTSCHEIDENDER TEIL 🔥
        int tableHeight = table.getRowHeight() * table.getRowCount();
        tablePane.setPreferredSize(new Dimension(800, tableHeight + 2));
        tablePane.setMaximumSize(new Dimension(Integer.MAX_VALUE, tableHeight + 2));
        tablePane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tablePane.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(tablePane);

        mainPanel.add(Box.createVerticalStrut(20));

        /* ===== BUTTONS ===== */
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        buttonPanel.add(new JButton("Rückgabe an Studierenden"));
        buttonPanel.add(new JButton("Freigeben"));

        mainPanel.add(buttonPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FreigabeDerArbeitBetreuer().setVisible(true));
    }
}
