package UserInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class OutstandingFinesPanel extends JPanel {

    private JTextField plateField;
    private JTable table;
    private DefaultTableModel tableModel;

    public OutstandingFinesPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeColors.PRIMARY);

        add(createTopSection(), BorderLayout.NORTH);
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

        styleTable();
        loadMockData();  

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

    private void loadMockData() {
        tableModel.setRowCount(0);

        tableModel.addRow(new Object[]{
                "ABC1234",
                "RM 67",
                "Overstay",
                "20-2-2067"
        });

        tableModel.addRow(new Object[]{
                "WXY5678",
                "RM 50",
                "Illegal Parking",
                "19-2-2067"
        });

        tableModel.addRow(new Object[]{
                "ABC1234",
                "RM 120",
                "No Ticket Displayed",
                "18-2-2067"
        });
    }

    private void searchFines() {
        String plate = plateField.getText().trim().toUpperCase();

        tableModel.setRowCount(0);

        // If empty then show all again
        if (plate.isEmpty()) {
            loadMockData();
            return;
        }

        // Filter mock data 
        if ("ABC1234".contains(plate)) {
            tableModel.addRow(new Object[]{
                    "ABC1234",
                    "RM 67",
                    "Overstay",
                    "20-2-2067"
            });

            tableModel.addRow(new Object[]{
                    "ABC1234",
                    "RM 120",
                    "No Ticket Displayed",
                    "18-2-2067"
            });
        }

        if ("WXY5678".contains(plate)) {
            tableModel.addRow(new Object[]{
                    "WXY5678",
                    "RM 50",
                    "Illegal Parking",
                    "19-2-2067"
            });
        }
    }
}
