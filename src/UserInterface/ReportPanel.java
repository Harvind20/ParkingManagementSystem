package UserInterface;
import coreParkingSystem.DatabaseConnection;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.table.*;

public class ReportPanel extends JPanel {

    private JComboBox<String> reportTypeDropdown;

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

        top.add(typeLbl);
        top.add(reportTypeDropdown);

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
        "Exit Time"
    };

    model = new DefaultTableModel(cols,0);

    try {
        Connection conn = DatabaseConnection.connect();

        // ========================
        // 1) ACTIVE VEHICLES (tickets)
        // ========================
        String activeSql =
            "SELECT plate_num, entry_time FROM tickets WHERE status='ACTIVE'";

        PreparedStatement pst1 = conn.prepareStatement(activeSql);
        ResultSet rs1 = pst1.executeQuery();

        while(rs1.next()) {
            String plate = rs1.getString("plate_num");
            String entry = rs1.getString("entry_time");

            String date = entry.split(" ")[0];

            model.addRow(new Object[]{
                plate,
                date,
                entry,
                "—"   // still parked
            });
        }

        rs1.close();
        pst1.close();

        // ========================
        // 2) EXITED VEHICLES (receipts)
        // ========================
        String historySql =
            "SELECT plate_num, entry_time, exit_time FROM receipts";

        PreparedStatement pst2 = conn.prepareStatement(historySql);
        ResultSet rs2 = pst2.executeQuery();

        while(rs2.next()) {
            String plate = rs2.getString("plate_num");
            String entry = rs2.getString("entry_time");
            String exit = rs2.getString("exit_time");

            String date = entry.split(" ")[0];

            model.addRow(new Object[]{
                plate,
                date,
                entry,
                exit
            });
        }

        rs2.close();
        pst2.close();
        conn.close();

    } catch(Exception e) {
        e.printStackTrace();
    }

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

    try {
        Connection conn = DatabaseConnection.connect();

        String sql = "SELECT plate_num, parking_fee, fine_amount, total_paid, payment_method, entry_time FROM receipts";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        while(rs.next()) {
            String plate = rs.getString("plate_num");
            double fee = rs.getDouble("parking_fee");
            double fine = rs.getDouble("fine_amount");
            double total = rs.getDouble("total_paid");
            String method = rs.getString("payment_method");
            String date = rs.getString("entry_time").split(" ")[0];

            model.addRow(new Object[]{
                plate,
                "RM " + String.format("%.2f", fee),
                "RM " + String.format("%.2f", fine),
                "RM " + String.format("%.2f", total),
                method,
                date
            });
        }

        rs.close();
        pst.close();
        conn.close();

    } catch(Exception e) {
        e.printStackTrace();
    }

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

    try {
        Connection conn = DatabaseConnection.connect();

        // Total capacity
        String capSql = "SELECT COUNT(*) AS total FROM parking_spots";
        PreparedStatement capStmt = conn.prepareStatement(capSql);
        ResultSet capRs = capStmt.executeQuery();
        int capacity = capRs.getInt("total");

        // Active parked vehicles
        String occSql = "SELECT COUNT(*) AS occ FROM parking_spots WHERE status='OCCUPIED'";
        PreparedStatement occStmt = conn.prepareStatement(occSql);
        ResultSet occRs = occStmt.executeQuery();
        int occupied = occRs.getInt("occ");

        double util = (occupied * 100.0) / capacity;

        String today = java.time.LocalDate.now().toString();

        model.addRow(new Object[]{
            today,
            occupied,
            occupied,     // peak = current for now
            occupied,     // avg = current for now
            String.format("%.1f%%", util)
        });

        capRs.close();
        occRs.close();
        conn.close();

    } catch(Exception e) {
        e.printStackTrace();
    }

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
