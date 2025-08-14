package screens.cook;

import javax.swing.*;
import screens.login.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

import models.OrderItem;

public class KitchenInterface extends JFrame {
    private ArrayList<Order> orders;

    public KitchenInterface(int cook_id, Connection sql_con) {
        super("Kitchen Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        orders = new ArrayList<>();
        try {
            Statement stmt = sql_con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT oi.order_id, oi.item_id, mi.item_name, oi.quantity_ordered, oi.comment, oi.item_status FROM ordered_item oi JOIN menu_item mi USING (item_id) WHERE oi.item_id IN ( SELECT item_id FROM cusine_cook WHERE employee_id ="
                    + cook_id
                    + ") AND oi.order_id IN ( SELECT order_id FROM restaurant_order WHERE order_status = 'active' ) AND oi.item_status in ('preparing', 'sent') ORDER BY oi.order_id;";

            ResultSet rs = stmt.executeQuery(query);
            int order_id = 0;
            Order create_order;
            for (; rs.next();) {
                if (rs.getInt("order_id") != order_id) {
                    order_id = rs.getInt("order_id");
                    Statement stmt2 = sql_con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
                    ResultSet table_rs = stmt2
                            .executeQuery("SELECT table_number FROM seated_at WHERE order_id=" + order_id + ";");
                    table_rs.first();
                    int table_number = table_rs.getInt("table_number");
                    create_order = new Order(table_number, order_id);
                    orders.add(create_order);
                }
                orders.getLast().appendItem(new OrderItem(rs.getInt("item_id"), rs.getString("item_name"),
                        rs.getInt("quantity_ordered"), rs.getString("comment"), rs.getString("item_status")));

            }

            JPanel mainPanel = new JPanel(new BorderLayout());

            JPanel orderPanel = new JPanel();
            orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
            JScrollPane scrollPane = new JScrollPane(orderPanel);

            // Populate the order list
            for (Order order : orders) {
                System.out.println(order.getItems().size());
                JPanel orderCard = createOrderPanel(order, cook_id, sql_con);
                orderPanel.add(orderCard);
            }
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            JButton refreshButton = new JButton("Refresh");

            refreshButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Dispose of the current page
                    new KitchenInterface(cook_id, sql_con).setVisible(true); // Create a new page
                }
            });

            JButton logoutButton = new JButton("Logout");

            logoutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Close current window
                    new RoleSelectionPage(sql_con).setVisible(true);
                }
            });

            JPanel buttonPanel = new JPanel(); // Panel for the refresh button
            buttonPanel.add(refreshButton); // Add refresh button to the panel
            buttonPanel.add(logoutButton); 

            mainPanel.add(buttonPanel, BorderLayout.SOUTH); // Add button panel to main panel

            add(mainPanel);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createOrderPanel(Order order, int cook_id, Connection sql_con) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2)); // Add border
        panel.setLayout(new BorderLayout());

        JLabel tableLabel = new JLabel("Table: " + order.getTableNumber());
        JPanel orderItemsPanel = new JPanel();
        orderItemsPanel.setLayout(new BoxLayout(orderItemsPanel, BoxLayout.Y_AXIS)); // Use BoxLayout with Y_AXIS

        for (OrderItem item : order.getItems()) {
            JLabel nameLabel = new JLabel(item.getName());
            JLabel quantityLabel = new JLabel("Quantity: " + item.getQuantity());
            JLabel commentLabel = new JLabel("Comment: " + item.getComment());
            JLabel statusLabel = new JLabel("Status: " + item.getStatus());
            JButton advButton = new JButton("Advance");

            advButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String query = "UPDATE ordered_item SET item_status=? WHERE order_id=? AND item_id=?";
                        PreparedStatement stmt = sql_con.prepareStatement(query);
                        String next_state = "";
                        switch (item.getStatus()) {

                            case "Received":
                                next_state = "preparing";
                                break;
                            case "preparing":
                                next_state = "served";
                                break;
                        }
                        stmt.setString(1, next_state);
                        stmt.setInt(2, order.getId());
                        stmt.setInt(3, item.getId());
                        stmt.executeUpdate();
                        dispose();
                        new KitchenInterface(cook_id, sql_con).setVisible(true);
                    } catch (SQLException err) {
                        err.printStackTrace();
                    }
                }
            });
            JPanel itemPanel = new JPanel(new GridLayout(1, 5));
            itemPanel.add(nameLabel);
            itemPanel.add(quantityLabel);
            itemPanel.add(commentLabel);
            itemPanel.add(statusLabel);
            itemPanel.add(advButton);
            orderItemsPanel.add(itemPanel);
        }

        panel.add(tableLabel, BorderLayout.NORTH);
        panel.add(orderItemsPanel, BorderLayout.CENTER);

        return panel;

    }

    // public static void main(String[] args) {
    // try {
    // Class.forName("com.mysql.cj.jdbc.Driver");

    // Connection con =
    // DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurant_maestro",
    // "root",
    // "root@123");
    // SwingUtilities.invokeLater(new Runnable() {
    // @Override
    // public void run() {
    // new KitchenInterface(8, con).setVisible(true);
    // }
    // });
    // } catch (SQLException e) {
    // System.out.println(e);
    // } catch (Exception e) {
    // System.out.println(e);
    // }
    // }
}

class Order {
    private int tableNumber;
    private int id;
    private ArrayList<OrderItem> items;
    private String status;

    public Order(int tableNumber, OrderItem... items) {
        this.tableNumber = tableNumber;
        this.items = new ArrayList<>();
        for (OrderItem item : items) {
            this.items.add(item);
        }
        this.status = "Received"; // Default status
    }

    public Order(int tableNumber, int order_id) {
        this.tableNumber = tableNumber;
        this.id = order_id;
        this.items = new ArrayList<>();
        this.status = "Received";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public ArrayList<OrderItem> getItems() {
        return items;
    }

    public boolean appendItem(OrderItem item) {
        return items.add(item);
    }

    public String getStatus() {
        return status;
    }
}
