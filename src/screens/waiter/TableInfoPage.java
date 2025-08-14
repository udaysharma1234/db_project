package screens.waiter;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TableInfoPage extends JFrame {

    public TableInfoPage(int table_number, Connection sql_con, int waiter_id) {
        super("Table Info");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        try {

            Statement stmt = sql_con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(
                    "SELECT order_id FROM restaurant_order WHERE order_id IN (SELECT order_id FROM seated_at WHERE table_number="
                            + table_number + " AND order_status='active')");
            int order_id = -1;
            if (rs.isBeforeFirst()) {
                rs.first();
                order_id = rs.getInt("order_id");
            }
            final int fin_order_id = order_id;
            rs = stmt.executeQuery(
                    "SELECT mi.item_id, mi.item_name, oi.comment, oi.quantity_ordered, oi.item_status, oi.order_id FROM menu_item mi JOIN ordered_item oi USING (item_ID) WHERE order_id="
                            + order_id + ";");
            // Panel for current order section
            JPanel currentOrderPanel = new JPanel(new BorderLayout());
            JTextArea orderTextArea = new JTextArea(10, 20);
            orderTextArea.setEditable(false);
            JScrollPane orderScrollPane = new JScrollPane(orderTextArea);
            currentOrderPanel.add(new JLabel("Current Order"), BorderLayout.NORTH);
            currentOrderPanel.add(orderScrollPane, BorderLayout.CENTER);

            // Populate current orders
            updateOrderTextArea(orderTextArea, rs);

            // Add current order panel to the frame
            JPanel actionsPanel = new JPanel(new FlowLayout());
            JButton placeOrderButton = new JButton("Place Order");
            placeOrderButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    new PlaceOrderPage(table_number, fin_order_id, waiter_id, sql_con).setVisible(true);

                }
            });
            JButton generateInvoiceButton = new JButton("Generate Invoice");
            actionsPanel.add(placeOrderButton);
            actionsPanel.add(generateInvoiceButton);

            generateInvoiceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    new GenerateInvoicePage(sql_con, fin_order_id, waiter_id).setVisible(true);
                }
            });

            JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Statement stmt = sql_con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
                        ResultSet rs = stmt.executeQuery(
                                "SELECT DISTINCT employee_id FROM assign_to WHERE table_number=" + table_number + ";");
                        rs.first();
                        int waiter_id = rs.getInt("employee_id");
                        dispose();
                        new TableListPage(waiter_id, sql_con);
                    } catch (SQLException err) {
                        err.printStackTrace();
                    }

                }
            });

            // Add panels to the frame
            add(currentOrderPanel, BorderLayout.CENTER);
            add(actionsPanel, BorderLayout.SOUTH);
            add(backButton, BorderLayout.NORTH);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void updateOrderTextArea(JTextArea orderTextArea, ResultSet rs) throws SQLException {
        orderTextArea.setText("");
        for (; rs.next();) {
            orderTextArea.append(
                    String.format("Item: %s | Status: %s | Quantity: %s | Comment: %s\n", rs.getString("item_name"),
                            rs.getString("item_status"), rs.getString("quantity_ordered"), rs.getString("comment")));
        }

    }

}
