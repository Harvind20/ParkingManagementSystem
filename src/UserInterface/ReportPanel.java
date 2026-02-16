package UserInterface;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class ReportPanel extends JPanel {

    private JComboBox<String> reportTypeDropdown;
    private JTextField fromDateField;
    private JTextField toDateField;
    private RoundedButton filterBtn;

    private JPanel tableArea;
    private JTable table;
    private DefaultTableModel model;

    public ReportPanel() {

        setLayout(new BorderLayout());
        setBackground(ThemeColors.PRIMARY);

        add(createTopBar(), BorderLayout.NORTH);

        tableArea = new JPanel(new BorderLayout());
        tableArea.setBackground(ThemeColors.PRIMARY);
        tableArea.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        add(tableArea, BorderLayout.CENTER);

        loadTable("Parked Vehicles");
    }

    private JPanel createTopBar() {

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        top.setBackground(ThemeColors.PRIMARY);

        JLabel typeLbl = new JLabel("Report:");
        typeLbl.setForeground(ThemeColors.SECONDARY);
        typeLbl.setFont(new Font("Arial", Font.BOLD, 15));

        reportTypeDropdown = new JComboBox<>(new String[]{
                "Parked Vehicles",
                "Revenue",
                "Occupancy"
        });

        reportTypeDropdown.setFont(new Font("Arial", Font.PLAIN, 14));
        reportTypeDropdown.addActionListener(e ->
                loadTable((String) reportTypeDropdown.getSelectedItem())
        );

        JLabel fromLbl = new JLabel("From:");
        fromLbl.setForeground(ThemeColors.SECONDARY);

        fromDateField = new JTextField(10);

        JLabel toLbl = new JLabel("To:");
        toLbl.setForeground(ThemeColors.SECONDARY);

        toDateField = new JTextField(10);

        filterBtn = new RoundedButton("Filter", ThemeColors.SECONDARY);
        filterBtn.setForeground(ThemeColors.PRIMARY);
        filterBtn.setPreferredSize(new Dimension(90, 32));

        filterBtn.addActionListener(e ->
                loadTable((String) reportTypeDropdown.getSelectedItem())
        );

        top.add(typeLbl);
        top.add(reportTypeDropdown);
        top.add(fromLbl);
        top.add(fromDateField);
        top.add(toLbl);
        top.add(toDateField);
        top.add(filterBtn);

        return top;
    }

    private void loadTable(String type) {

        tableArea.removeAll();

        if(type.equals("Parked Vehicles"))
            buildVehiclesTable();
        else if(type.equals("Revenue"))
            buildRevenueTable();
        else
            buildOccupancyTable();

        tableArea.revalidate();
        tableArea.repaint();
    }

    private void buildVehiclesTable() {

        String[] cols = {
                "Plate",
                "Date",
                "Entry Time",
                "Exit Time",
                "Spot ID",
                "Spot Type"
        };

        model = new DefaultTableModel(cols,0);

        model.addRow(new Object[]{"ABC1234","12-03-2026","10:15 AM","11:30 AM","F1-R1-S1","Regular"});
        model.addRow(new Object[]{"XYZ9988","12-03-2026","09:05 AM","—","F2-R2-S4","VIP"});

        createStretchTable();
    }

    private void buildRevenueTable() {

        String[] cols = {
                "Plate",
                "Parking Fee",
                "Fine Paid",
                "Total Paid",
                "Payment Method",
                "Date"
        };

        model = new DefaultTableModel(cols,0);

        model.addRow(new Object[]{"ABC1234","RM 5.00","RM 0.00","RM 5.00","Cash","12-03-2026"});
        model.addRow(new Object[]{"XYZ9988","RM 7.00","RM 50.00","RM 57.00","Card","12-03-2026"});

        createStretchTable();
    }

    private void buildOccupancyTable() {

        String[] cols = {
                "Date",
                "Vehicles Parked",
                "Peak Occupancy",
                "Average Occupancy",
                "Average Utilization %"
        };

        model = new DefaultTableModel(cols,0);

        int capacity = 120;
        int avg = 72;
        int peak = 96;
        double util = (avg * 100.0) / capacity;

        model.addRow(new Object[]{"12-03-2026", 110, peak, avg, String.format("%.1f%%",util)});
        model.addRow(new Object[]{"11-03-2026", 95, 88, 60, "50.0%"});

        createStretchTable();
    }

    private void createStretchTable() {

        table = new JTable(model);

        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(230,230,230));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0;i<table.getColumnCount();i++)
            table.getColumnModel().getColumn(i).setCellRenderer(center);

        JTableHeader header = table.getTableHeader();
        header.setBackground(ThemeColors.SECONDARY);
        header.setForeground(ThemeColors.PRIMARY);
        header.setFont(new Font("Segoe UI",Font.BOLD,14));
        header.setPreferredSize(new Dimension(header.getWidth(),42));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        RoundedPanel card = new RoundedPanel(30);
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        card.add(scroll, BorderLayout.CENTER);

        tableArea.add(card, BorderLayout.CENTER);
    }
}
