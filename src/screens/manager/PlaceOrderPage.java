package screens.manager;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import models.OrderItem;

import javax.swing.*;

public class PlaceOrderPage extends JFrame {
    private DefaultListModel<OrderItem> orderListModel;
    private JList<OrderItem> orderList;

    public PlaceOrderPage(Connection sql_con) {
        setTitle("Restaurant Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the window
        setResizable(false);

        try {

            Statement stmt = sql_con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT cusine_name FROM cusine_cook");

            // Create a tabbed pane
            JTabbedPane tabbedPane = new JTabbedPane();

            // Initialize order list
            orderListModel = new DefaultListModel<>();
            orderList = new JList<>(orderListModel);

            // Loop through cuisines to create tabs
            for (; rs.next();) {
                String cuisine = rs.getString("cusine_name");
                Statement stmt2 = sql_con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

                ResultSet items_rs = stmt2.executeQuery(
                        "SELECT i.item_id, i.item_name, i.item_type, i.price FROM menu_item i JOIN cusine_cook c using (item_id) WHERE c.cusine_name='"
                                + cuisine + "' AND i.item_availability='yes'");

                int item_count = 0;
                for (; items_rs.next();) {
                    item_count++;
                }
                items_rs.beforeFirst();

                // Create panel for each cuisine
                JPanel cuisinePanel = new JPanel(new GridLayout(item_count, 1));

                // Loop through items to create dish panels
                for (; items_rs.next();) {
                    // Create panel for each item
                    String item_name = items_rs.getString("item_name");
                    int item_id = items_rs.getInt("item_id");
                    JPanel itemPanel = new JPanel(new GridLayout(3, 2));
                    itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border
                    JLabel nameLabel = new JLabel(item_name);
                    JTextArea quantityTextField = new JTextArea();
                    JTextArea commentTextArea = new JTextArea();
                    JButton addButton = new JButton("Add");

                    // Quantity panel with label and text field
                    JPanel quantityPanel = new JPanel(new BorderLayout());
                    quantityPanel.add(new JLabel("Quantity: "), BorderLayout.NORTH);
                    quantityPanel.add(quantityTextField);

                    // Comment panel with label and text area
                    JPanel commentPanel = new JPanel(new BorderLayout());
                    commentPanel.add(new JLabel("Comment: "), BorderLayout.NORTH);
                    commentPanel.add(new JScrollPane(commentTextArea), BorderLayout.CENTER);

                    // Buttons panel with "Add" button
                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    buttonPanel.add(addButton); // Add button in the second column

                    // Add components to item panel
                    itemPanel.add(nameLabel);
                    itemPanel.add(quantityPanel);
                    itemPanel.add(new JLabel()); // Empty cell for spacing
                    itemPanel.add(commentPanel);
                    itemPanel.add(new JLabel()); // Empty cell for spacing
                    itemPanel.add(buttonPanel);

                    // Add action listener to add button
                    addButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String quantity = quantityTextField.getText();
                            String comment = commentTextArea.getText();
                            if (!quantity.isEmpty()) {
                                OrderItem orderItem = new OrderItem(item_id, item_name, Integer.parseInt(quantity),
                                        comment);
                                orderListModel.addElement(orderItem);
                            }
                        }
                    });

                    // Add item panel to cuisine panel
                    cuisinePanel.add(itemPanel);
                }

                // Add cuisine panel to tabbed pane
                tabbedPane.addTab(cuisine, cuisinePanel);
            }

            // Send to kitchen button
            JButton sendToKitchenButton = new JButton("Send Order to Kitchen");
            sendToKitchenButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        sendOrderToKitchen(sql_con);
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            });
            JButton backButton = new JButton("Back");

            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    new ManagerInterface(sql_con).setVisible(true);
                }
            });

            // Add order list, send to kitchen button, and back button to a panel
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.add(new JScrollPane(orderList), BorderLayout.CENTER);
            rightPanel.add(sendToKitchenButton, BorderLayout.SOUTH);
            rightPanel.add(backButton, BorderLayout.NORTH);

            // Add the tabbed pane and the panel with order list, send to kitchen button,
            // and back button to the frame
            getContentPane().add(tabbedPane, BorderLayout.CENTER);
            getContentPane().add(rightPanel, BorderLayout.EAST);

            // Add order list and send to kitchen button to a panel
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendOrderToKitchen(Connection sql_con) throws Exception {
        List<OrderItem> orderItems = new ArrayList<OrderItem>(orderList.getModel().getSize());
        for (int i = 0; i < orderList.getModel().getSize(); i++) {
            orderItems.add(orderList.getModel().getElementAt(i));
        }
        Calendar calendar = Calendar.getInstance();
        PreparedStatement order_stmt = sql_con.prepareStatement(
                "INSERT INTO restaurant_order ( order_time,  order_status, discount, order_type)"
                        + " VALUES (?,?,?,?);",
                Statement.RETURN_GENERATED_KEYS);
        order_stmt.setTimestamp(1, new Timestamp(calendar.getTimeInMillis()));
        order_stmt.setString(2, "active");
        order_stmt.setInt(3, 0);
        order_stmt.setString(4, "delivery");
        order_stmt.executeUpdate();
        ResultSet rs = order_stmt.getGeneratedKeys();
        int order_id = -1;
        if (rs.next()) {
            order_id = rs.getInt(1);
        }
        if (order_id == -1) {
            return;
        }
        for (OrderItem item : orderItems) {
            PreparedStatement stmt = sql_con.prepareStatement(
                    "INSERT INTO ordered_item (order_id, item_id, comment, quantity_ordered, item_status)"
                            + " VALUES (?, ?, ?, ?, ?);");
            stmt.setInt(1, order_id);
            stmt.setInt(2, item.getId());
            stmt.setString(3, item.getComment());
            stmt.setInt(4, item.getQuantity());
            stmt.setString(5, "sent");
            stmt.execute();

            dispose();
            new GetAddressScreen(sql_con, order_id).setVisible(true);
        }
    }

}