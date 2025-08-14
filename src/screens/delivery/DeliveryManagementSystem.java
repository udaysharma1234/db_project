package screens.delivery;
import screens.login.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DeliveryManagementSystem extends JFrame {

    public DeliveryManagementSystem(int waiter_id, Connection sql_con) {
        super("Delivery Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        getContentPane().setBackground(new Color(240, 240, 240)); // Set background color

        try {

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new GridLayout(0, 1));
            mainPanel.setBackground(new Color(240, 240, 240)); // Set background color

            Statement stmt = sql_con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT o.order_id, d.address, o.order_status FROM restaurant_order o LEFT JOIN delivered_by d USING (order_id) WHERE d.employee_id="
                            + waiter_id + " AND o.order_status <> 'completed';");

            for (; rs.next();) {
                JPanel deliveryPanel = createDeliveryPanel(waiter_id, rs.getInt("order_id"), rs.getString("address"),
                        rs.getString("order_status"), sql_con);
                mainPanel.add(deliveryPanel);
            }

            JScrollPane scrollPane = new JScrollPane(mainPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.getViewport().setBackground(new Color(240, 240, 240)); // Set background color for viewport

            JButton logoutButton = new JButton("Logout");

            logoutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Close current window
                    new RoleSelectionPage(sql_con).setVisible(true);
                }
            });
            setLayout(new BorderLayout());
            add(scrollPane, BorderLayout.CENTER);
            add(logoutButton, BorderLayout.SOUTH);
            setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createDeliveryPanel(int waiter_id, int order_id, String address, String status, Connection sql_con) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2)); // Add border
        panel.setLayout(new GridLayout(4, 1));
        panel.setBackground(Color.white); // Set background color

        JLabel orderLabel = new JLabel("Order: " + order_id);
        JLabel addressLabel = new JLabel("Address: " + address);
        JLabel statusLabel = new JLabel("Status: " + status);
        JButton deliveredButton = new JButton("Delivered");

        deliveredButton.setBackground(new Color(50, 150, 50)); // Set button background color
        deliveredButton.setForeground(Color.white); // Set button text color
        deliveredButton.setFocusPainted(false); // Remove focus border

        deliveredButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PreparedStatement stmt = sql_con
                            .prepareStatement("UPDATE restaurant_order SET order_status='completed' WHERE order_id=?");
                    stmt.setInt(1, order_id);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Delivery for Order " + order_id + " is completed!"); // Show pop-up message
                    dispose();
                    new DeliveryManagementSystem(waiter_id, sql_con);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });

        panel.add(orderLabel);
        panel.add(addressLabel);
        panel.add(statusLabel);
        panel.add(deliveredButton);

        return panel;
    }

}
