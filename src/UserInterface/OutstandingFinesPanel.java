package UserInterface;

import coreParkingSystem.DatabaseConnection;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class OutstandingFinesPanel extends JPanel {

    private JTextField plateField;
    private JTable table;
    private DefaultTableModel tableModel;

    public OutstandingFinesPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.PRIMARY);

        // top section for searching fines by plate
        add(createTopSection(), BorderLayout.NORTH);

        // table showing unpaid fines
        add(createTableSection(), BorderLayout.CENTER);
    }

    private JPanel createTopSection() {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(ThemeColors.PRIMARY);
        top.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JLabel label = new JLabel("Filter by Number Plate:");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchRow.setBackground(ThemeColors.PRIMARY);
        searchRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        plateField = new JTextField();
        plateField.setPreferredSize(new Dimension(260, 40));
        plateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        plateField.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));

        // button triggers search using entered plate
        RoundedButton findBtn = new RoundedButton("FIND", ThemeColors.SECONDARY);
        findBtn.setPreferredSize(new Dimension(90, 40));
        findBtn.addActionListener(e -> searchFines());

        searchRow.add(plateField);
        searchRow.add(findBtn);

        top.add(label);
        top.add(Box.createVerticalStrut(6));
        top.add(searchRow);

        return top;
    }

    private JPanel createTableSection() {

        String[] columns = {
                "LICENSE PLATE",
                "FINE AMOUNT",
                "REASON",
                "DATE ISSUED"
        };

        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        // apply visual styling to table
        styleTable();

        // load all unpaid fines initially
        loadFinesFromDB("");  

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        RoundedPanel card = new RoundedPanel(30);
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(1000, 420));
        card.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
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
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));

        JTableHeader header = table.getTableHeader();
        header.setBackground(ThemeColors.SECONDARY);
        header.setForeground(ThemeColors.PRIMARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // center align all cells and add alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {

                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                c.setHorizontalAlignment(JLabel.CENTER);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248,248,248));
                }

                return c;
            }
        });
    }

    // pulls unpaid fines from DB, optionally filtered by plate
    private void loadFinesFromDB(String plateQuery) {
        tableModel.setRowCount(0);
        
        String sql = "SELECT * FROM fines WHERE status = 'UNPAID'";
        if (!plateQuery.isEmpty()) {
            sql += " AND plate_num LIKE ?";
        }

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (!plateQuery.isEmpty()) {
                pstmt.setString(1, "%" + plateQuery + "%");
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String plate = rs.getString("plate_num");
                double amount = rs.getDouble("amount");
                String reason = rs.getString("reason");
                String date = rs.getString("date_issued");
                
                if (date == null) date = "-";
                
                tableModel.addRow(new Object[]{
                        plate,
                        String.format("RM %.2f", amount),
                        reason,
                        date 
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // triggered when user clicks FIND
    private void searchFines() {
        String plate = plateField.getText().trim();
        loadFinesFromDB(plate);
    }
}
