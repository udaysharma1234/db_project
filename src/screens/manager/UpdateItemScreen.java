package screens.manager;

import javax.swing.*;

import utils.ErrorScreen;

import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateItemScreen extends JFrame {

    public UpdateItemScreen(Connection sql_con) {
        setTitle("Update Items");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        try {
            Statement stmt = sql_con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(
                    "SELECT i.item_name, i.item_id, i.price, i.item_availability, i.item_type,  c.cusine_name from menu_item i left join cusine_cook c using(item_id) order by c.cusine_name;");
            // Create a panel to hold the list
            JPanel itemListPanel = new JPanel();
            itemListPanel.setLayout(new BoxLayout(itemListPanel, BoxLayout.Y_AXIS));

            // Add items to the list panel
            for (; rs.next();) {
                int item_id = rs.getInt("item_id");
                JPanel itemPanel = new JPanel(new GridLayout(1, 6));

                JLabel itemNameLabel = new JLabel(rs.getString("item_name"));
                itemPanel.add(itemNameLabel);
                JLabel itemCusineLabel = new JLabel("Cusine: " + rs.getString("cusine_name"));
                itemPanel.add(itemCusineLabel);
                JPanel itemPricePanel = new JPanel();
                JLabel itemPriceLabel = new JLabel("Price: ");
                JTextField itemPriceTextField = new JTextField(rs.getString("price")); // Initial value
                itemPricePanel.add(itemPriceLabel);
                itemPricePanel.add(itemPriceTextField);
                itemPanel.add(itemPricePanel);

                String[] availabilityOptions = { "Available", "Unavailable" };
                JComboBox<String> availabilityDropdown = new JComboBox<>(availabilityOptions);
                String item_avail = rs.getString("item_availability");
                if (item_avail.equals("yes")) {
                    availabilityDropdown.setSelectedIndex(0);
                } else {
                    availabilityDropdown.setSelectedIndex(1);
                }
                itemPanel.add(availabilityDropdown);

                // Add update button for this item
                JButton updateButton = new JButton("Update");
                updateButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            String x;
                            if (availabilityDropdown.getSelectedItem().equals("Available")) {
                                x = "yes";
                            } else {
                                x = "no";
                            }
                            PreparedStatement stmt2 = sql_con.prepareStatement(
                                    "UPDATE menu_item SET price=?,item_availability=? WHERE item_id=?");
                            stmt2.setDouble(1, Double.parseDouble(itemPriceTextField.getText()));
                            stmt2.setString(2, x);
                            stmt2.setInt(3, item_id);
                            stmt2.executeUpdate();
                            dispose();
                            new UpdateItemScreen(sql_con).setVisible(true);
                        } catch (SQLException err) {
                            err.printStackTrace();
                        } catch (NumberFormatException err) {
                            dispose();
                            new ErrorScreen("Please enter valid price");
                            return;
                        }
                    }
                });
                itemPanel.add(updateButton);
                itemListPanel.add(itemPanel);
            }
            JScrollPane scrollPane = new JScrollPane(itemListPanel);
            panel.add(scrollPane, BorderLayout.CENTER);

            // Add a back button
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
            JButton addButton = new JButton("Add New Item");
            JButton backButton = new JButton("Back");
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    new AddItemScreen(sql_con).setVisible(true);
                }
            });
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Close the window
                    new ManagerInterface(sql_con).setVisible(true);
                }
            });
            buttonPanel.add(addButton);
            buttonPanel.add(backButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            getContentPane().add(panel);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add the list panel to a scroll pane
    }

    // public static void main(String[] args) {
    // SwingUtilities.invokeLater(new Runnable() {
    // public void run() {
    // try {
    // Class.forName("com.mysql.cj.jdbc.Driver");

    // Connection con =
    // DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurant_maestro",
    // "root",
    // "root@123");
    // new UpdateItemScreen(con).setVisible(true);
    // } catch (Exception e) {
    // System.out.println(e);
    // }
    // }
    // });
    // }
}
