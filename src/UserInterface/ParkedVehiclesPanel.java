package UserInterface;

import coreParkingSystem.DatabaseConnection;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ParkedVehiclesPanel extends JPanel {

    private LocalDate currentDate;
    private JLabel dateLabel;
    private JTable table;
    private DefaultTableModel tableModel;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

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
        loadDataFromDB();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        RoundedPanel card = new RoundedPanel(30);
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(1000, 420));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.add(scrollPane, BorderLayout.CENTER);

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
        loadDataFromDB();
    }

    private void loadDataFromDB() {
        String targetDate = currentDate.toString(); 

        String sqlActive = "SELECT t.plate_num, t.entry_time, s.spot_id, s.type " +
                           "FROM tickets t " +
                           "JOIN parking_spots s ON t.plate_num = s.plate_num " +
                           "WHERE t.status = 'ACTIVE' AND date(t.entry_time) = ?";

        String sqlHistory = "SELECT r.plate_num, r.entry_time, r.exit_time, r.spot_id, s.type " +
                            "FROM receipts r " +
                            "LEFT JOIN parking_spots s ON r.spot_id = s.spot_id " +
                            "WHERE date(r.entry_time) = ?";

        try (Connection conn = DatabaseConnection.connect()) {
            
            PreparedStatement pstActive = conn.prepareStatement(sqlActive);
            pstActive.setString(1, targetDate);
            ResultSet rsActive = pstActive.executeQuery();

            while (rsActive.next()) {
                LocalDateTime entry = LocalDateTime.parse(rsActive.getString("entry_time"));
                
                tableModel.addRow(new Object[]{
                    rsActive.getString("plate_num"),
                    entry.format(formatter),
                    entry.format(timeFormatter),
                    "—", 
                    rsActive.getString("spot_id"),
                    rsActive.getString("type")
                });
            }

            PreparedStatement pstHist = conn.prepareStatement(sqlHistory);
            pstHist.setString(1, targetDate);
            ResultSet rsHist = pstHist.executeQuery();

            while (rsHist.next()) {
                LocalDateTime entry = LocalDateTime.parse(rsHist.getString("entry_time"));
                LocalDateTime exit = LocalDateTime.parse(rsHist.getString("exit_time"));
                String type = rsHist.getString("type");
                if (type == null) type = "Unknown"; 

                tableModel.addRow(new Object[]{
                    rsHist.getString("plate_num"),
                    entry.format(formatter),
                    entry.format(timeFormatter),
                    exit.format(timeFormatter),
                    rsHist.getString("spot_id"),
                    type
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}