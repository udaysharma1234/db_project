package screens.manager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class AddItemScreen extends JFrame {
    private Connection sql_con;

    public AddItemScreen(Connection sql_con) {
        this.sql_con = sql_con;
        setTitle("Add New Item");
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2));

        // Item name input field
        JLabel nameLabel = new JLabel("Item Name:");
        JTextField nameField = new JTextField();
        panel.add(nameLabel);
        panel.add(nameField);

        // Item price input field
        JLabel priceLabel = new JLabel("Item Price:");
        JTextField priceField = new JTextField();
        panel.add(priceLabel);
        panel.add(priceField);

        JLabel vegNonVegLabel = new JLabel("Veg/Non-Veg");
        String[] options = { "Veg", "Non-Veg" };
        JComboBox<String> vegNonVegDropdown = new JComboBox<>(options);
        panel.add(vegNonVegLabel);
        panel.add(vegNonVegDropdown);
        // Cuisine dropdown
        JLabel cuisineLabel = new JLabel("Cuisine:");
        try {
            Statement stmt = sql_con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT cusine_name FROM cusine_cook;");
            String[] cuisineOption = {};
            ArrayList<String> cuisineOptions = new ArrayList<String>();
            for (; rs.next();) {
                cuisineOptions.add(rs.getString("cusine_name"));
            }
            cuisineOption = cuisineOptions.toArray(cuisineOption);
            JComboBox<String> cuisineDropdown = new JComboBox<>(cuisineOption);
            panel.add(cuisineLabel);
            panel.add(cuisineDropdown);

            // Add button
            JButton addButton = new JButton("Add Item");
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Get input values
                    String itemName = nameField.getText();
                    String itemPrice = priceField.getText();
                    String selectedCuisine = (String) cuisineDropdown.getSelectedItem();
                    String vegNonVeg = (String) vegNonVegDropdown.getSelectedItem();

                    // Validate input
                    if (itemName.isEmpty() || itemPrice.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please fill in all fields", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        // Perform action with input values (e.g., add the new item)
                        try {

                            addItem(itemName, itemPrice, selectedCuisine, vegNonVeg);
                        } catch (Exception err) {
                            JOptionPane.showMessageDialog(null, "Please enter correct data", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                        // Provide feedback to the user or close the window
                        JOptionPane.showMessageDialog(null, "Item added successfully", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // Close the window after adding the item
                    }
                }
            });
            panel.add(addButton);

            // Cancel button
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Close the window without adding the item
                    new UpdateItemScreen(sql_con).setVisible(true);
                }
            });
            panel.add(cancelButton);

            getContentPane().add(panel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addItem(String itemName, String itemPrice, String selectedCuisine, String vegNonVeg)
            throws NumberFormatException {
        try {
            double dob_itemPrice = Double.parseDouble(itemPrice);
            PreparedStatement stmt = sql_con
                    .prepareStatement("INSERT INTO menu_item (item_name, price, item_availability, item_type)"
                            + " VALUES (?,?,'yes',?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, itemName);
            stmt.setDouble(2, dob_itemPrice);
            stmt.setString(3, vegNonVeg.toLowerCase());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.first();
            int item_id = rs.getInt(1);

            Statement stmt2 = sql_con.createStatement();
            ResultSet rs2 = stmt2.executeQuery(
                    "SELECT DISTINCT employee_id from cusine_cook WHERE cusine_name='" + selectedCuisine + "';");
            for (; rs2.next();) {
                int cook_id = rs2.getInt(1);
                PreparedStatement stmt3 = sql_con.prepareStatement(
                        "INSERT INTO cusine_cook (item_id, employee_id, cusine_name)" + " VALUES (?,?,?)");
                stmt3.setInt(1, item_id);
                stmt3.setInt(2, cook_id);
                stmt3.setString(3, selectedCuisine);
                stmt3.executeUpdate();
            }
            dispose();
            new UpdateItemScreen(sql_con).setVisible(true);
        } catch (NumberFormatException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public static void main(String[] args) {
    // SwingUtilities.invokeLater(new Runnable() {
    // public void run() {
    // new AddItemScreen(null).setVisible(true);
    // }
    // });
    // }
}
