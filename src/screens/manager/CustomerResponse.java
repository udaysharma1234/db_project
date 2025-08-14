package screens.manager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class CustomerResponse extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    public CustomerResponse(Connection sql_con) {
        setTitle("Order Details");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create table model with columns
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Invoice ID");
        tableModel.addColumn("Waiter Rating");
        tableModel.addColumn("Completion Time");
        tableModel.addColumn("Bill Amount");
        tableModel.addColumn("Mode of Payment");
        tableModel.addColumn("Customer Name");

        // Create table with model
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        try {
            Statement stmt = sql_con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT i.invoice_id, i.waiter_rating, CONCAT(ci.cfirst_name, ' ', ci.clast_name) as name, i.mode_of_payment, o.order_id, TIMEDIFF(r.completion_time , r.order_time) as completion_time, r.bill_amount FROM order_invoice o JOIN invoice i USING (invoice_id) JOIN customer_information ci USING (invoice_id) JOIN restaurant_order r USING (order_id);");

            for (; rs.next();) {
                Vector<String> row = new Vector<String>();
                row.add(rs.getString("invoice_id"));
                row.add(rs.getString("waiter_rating"));
                row.add(rs.getString("completion_time"));
                row.add(rs.getString("bill_amount"));
                row.add(rs.getString("mode_of_payment"));
                row.add(rs.getString("name"));
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ManagerInterface(sql_con).setVisible(true);
            }
        });
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(backButton, BorderLayout.SOUTH);
    }

    // public static void main(String[] args) {
    // try {
    // Class.forName("com.mysql.cj.jdbc.Driver");

    // Connection con =
    // DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurant_maestro",
    // "root",
    // "root@123");
    // SwingUtilities.invokeLater(() -> {
    // new CustomerResponse(con).setVisible(true);
    // });
    // } catch (SQLException e) {
    // System.out.println(e);
    // } catch (Exception e) {
    // System.out.println(e);
    // }
    // }
}