package UserInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ParkedVehiclesPanel extends JPanel {

    private LocalDate currentDate;
    private JLabel dateLabel;
    private JTable table;
    private DefaultTableModel tableModel;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ParkedVehiclesPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.PRIMARY);

        currentDate = LocalDate.now();

        add(createTopBar(), BorderLayout.NORTH);
        add(createTableSection(), BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(ThemeColors.PRIMARY);

        RoundedButton downBtn = new RoundedButton("▼", ThemeColors.SECONDARY);
        RoundedButton upBtn   = new RoundedButton("▲", ThemeColors.SECONDARY);

        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dateLabel.setForeground(Color.WHITE);
        updateDateLabel();

        downBtn.addActionListener(e -> {
            currentDate = currentDate.minusDays(1);
            updateDateLabel();
            refreshTable();
        });

        upBtn.addActionListener(e -> {
            currentDate = currentDate.plusDays(1);
            updateDateLabel();
            refreshTable();
        });

        topPanel.add(downBtn);
        topPanel.add(dateLabel);
        topPanel.add(upBtn);

        return topPanel;
    }

    private JPanel createTableSection() {

        String[] columns = {
                "LICENSE PLATE",
                "DATE",
                "ENTRY TIME",
                "EXIT TIME",
                "SPOT ID",
                "SPOT TYPE"
        };

        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);

        styleTable();
        loadDummyData();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Put table inside a rounded "card"
        RoundedPanel card = new RoundedPanel(30);
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(1000, 420));
        card.add(scrollPane, BorderLayout.CENTER);

        // Center the card
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(ThemeColors.PRIMARY);
        wrapper.add(card);

        return wrapper;
    }

    private void styleTable() {
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(new Color(40, 40, 40));
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(210, 235, 255));

        // Center all text
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JTableHeader header = table.getTableHeader();
        header.setBackground(ThemeColors.SECONDARY);
        header.setForeground(ThemeColors.PRIMARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(JLabel.CENTER);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                }

                return c;
            }
        });

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    private void updateDateLabel() {
        dateLabel.setText(currentDate.format(formatter));
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        // For future DB query
        loadDummyData();
    }

    private void loadDummyData() {
        // Temporary data 
        tableModel.addRow(new Object[]{
                "ABC1234",
                currentDate.format(formatter),
                "10:20 AM",
                "11:30 AM",
                "F1-R1-S1",
                "Regular"
        });

        tableModel.addRow(new Object[]{
                "WXY5678",
                currentDate.format(formatter),
                "09:05 AM",
                "—",
                "F2-R3-S2",
                "Compact"
        });
    }
}
