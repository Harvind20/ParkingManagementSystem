package UserInterface;

import coreParkingSystem.DatabaseConnection;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ParkedVehiclesPanel extends JPanel {

    private LocalDate currentDate;
    private JLabel dateLabel;
    private JTable table;
    private DefaultTableModel tableModel;
    private DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private DateTimeFormatter displayTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    private DateTimeFormatter dbTicketFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss"); 

    public ParkedVehiclesPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.PRIMARY);

        // always show vehicles for the current day
        currentDate = LocalDate.now();

        add(createTopBar(), BorderLayout.NORTH);
        add(createTableSection(), BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(ThemeColors.PRIMARY);

        // displays today's date
        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dateLabel.setForeground(Color.WHITE);
        updateDateLabel();

        JLabel todayNote = new JLabel("Showing Today's Parked Vehicles");
        todayNote.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        todayNote.setForeground(Color.LIGHT_GRAY);

        topPanel.add(dateLabel);
        topPanel.add(todayNote);

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

        // pull today's parked vehicle data from DB
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

        // center align all columns
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

        // zebra striping effect for better readability
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
        dateLabel.setText(currentDate.format(displayDateFormatter));
    }

    private void loadDataFromDB() {
        tableModel.setRowCount(0);

        // query for vehicles that are still parked 
        String sqlActive = "SELECT t.plate_num, t.entry_time, s.spot_id, s.type " +
                           "FROM tickets t " +
                           "LEFT JOIN parking_spots s ON t.plate_num = s.plate_num " +
                           "WHERE t.status = 'ACTIVE'";

        // query for vehicles that already exited 
        String sqlHistory = "SELECT r.plate_num, r.entry_time, r.exit_time, r.spot_id, s.type " +
                            "FROM receipts r " +
                            "LEFT JOIN parking_spots s ON r.spot_id = s.spot_id";

        try (Connection conn = DatabaseConnection.connect()) {
            
            // process active vehicles
            PreparedStatement pstActive = conn.prepareStatement(sqlActive);
            ResultSet rsActive = pstActive.executeQuery();

            while (rsActive.next()) {
                String entryStr = rsActive.getString("entry_time");
                try {
                    LocalDateTime entry = LocalDateTime.parse(entryStr, dbTicketFormatter);
                    
                    // only show rows for today
                    if (entry.toLocalDate().equals(currentDate)) {
                        String spotId = rsActive.getString("spot_id");
                        String type = rsActive.getString("type");
                        
                        tableModel.addRow(new Object[]{
                            rsActive.getString("plate_num"),
                            entry.format(displayDateFormatter),
                            entry.format(displayTimeFormatter),
                            "—", // still parked
                            (spotId != null ? spotId : "Unknown"),
                            (type != null ? type : "Regular")
                        });
                    }
                } catch (DateTimeParseException e) {
                    System.err.println("Skipping active row due to date format error: " + entryStr);
                }
            }
            rsActive.close();
            pstActive.close();

            // process exited vehicles from receipts
            PreparedStatement pstHist = conn.prepareStatement(sqlHistory);
            ResultSet rsHist = pstHist.executeQuery();

            while (rsHist.next()) {
                String entryStr = rsHist.getString("entry_time");
                String exitStr = rsHist.getString("exit_time");
                
                try {
                    LocalDateTime entry = LocalDateTime.parse(entryStr);
                    LocalDateTime exit = LocalDateTime.parse(exitStr);

                    // only include today's records
                    if (entry.toLocalDate().equals(currentDate)) {
                        String type = rsHist.getString("type");
                        if (type == null) type = "Unknown";

                        tableModel.addRow(new Object[]{
                            rsHist.getString("plate_num"),
                            entry.format(displayDateFormatter),
                            entry.format(displayTimeFormatter),
                            exit.format(displayTimeFormatter),
                            rsHist.getString("spot_id"),
                            type
                        });
                    }
                } catch (DateTimeParseException e) {
                   System.err.println("Skipping history row due to date format error: " + entryStr);
                }
            }
            rsHist.close();
            pstHist.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
