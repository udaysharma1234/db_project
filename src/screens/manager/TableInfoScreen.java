package screens.manager;

import javax.swing.*;

import utils.ErrorScreen;

import java.awt.*;
import java.sql.*;
import java.awt.event.*;

public class TableInfoScreen extends JFrame {

    public TableInfoScreen(Connection sql_con) {
        setTitle("Table Reservation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Initialize tables
        try {
            Statement stmt = sql_con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM restaurant_table");

            JPanel mainPanel = new JPanel(new GridLayout(0, 3));

            // Headers

            // Add table details
            for (; rs.next();) {
                int tableNumber = rs.getInt("table_number");
                JLabel tableNumberLabel = new JLabel("Table " + tableNumber);
                JLabel statusLabel = new JLabel("Status: " + rs.getString("table_status"));
                JButton reserveButton = new JButton("Reserve");
                JLabel reservationInfoLabel = new JLabel(
                        "For: " + rs.getString("res_first_name") + " " + rs.getString("res_last_name") + " ("
                                + rs.getString("res_phone_number") + ")");
                reserveButton.setEnabled(false);
                if (statusLabel.getText().equalsIgnoreCase("Status: Available")) {
                    reserveButton.setEnabled(true);
                    reserveButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                String firstName = JOptionPane.showInputDialog("Enter customer first name:");
                                String lastName = JOptionPane.showInputDialog("Enter customer last name:");
                                String phoneNumber = JOptionPane.showInputDialog("Enter customer phone number:");
                                if (firstName != null && lastName != null && phoneNumber != null && !firstName.isBlank()
                                        && !lastName.isBlank() && !phoneNumber.isBlank()
                                        && phoneNumber.length() == 10) {
                                    try {

                                        PreparedStatement stmt = sql_con.prepareStatement(
                                                "UPDATE restaurant_table SET table_status='Reserved', res_first_name=? ,res_last_name=?, res_phone_number=? WHERE table_number=?");
                                        stmt.setString(1, firstName);
                                        stmt.setString(2, lastName);
                                        stmt.setString(3, phoneNumber);
                                        stmt.setInt(4, tableNumber);
                                        stmt.executeUpdate();
                                        JOptionPane.showMessageDialog(null, "Table " + tableNumber + " reserved!");
                                        dispose();
                                        new TableInfoScreen(sql_con).setVisible(true);
                                    } catch (NumberFormatException err1) {
                                        new ErrorScreen("Enter valid phone number!").setVisible(true);
                                    }
                                } else {
                                    new ErrorScreen("Please enter all the details!").setVisible(true);
                                }
                            } catch (Exception err) {
                                err.printStackTrace();
                            }
                        }
                    });
                }

                mainPanel.add(tableNumberLabel);
                mainPanel.add(statusLabel);
                if (statusLabel.getText().equalsIgnoreCase("Status: Reserved")) {
                    mainPanel.add(reservationInfoLabel);
                } else {
                    mainPanel.add(reserveButton);
                }
            }

            // JScrollPane scrollPane = new JScrollPane(mainPanel);
            // add(scrollPane);

            JScrollPane scrollPane = new JScrollPane(mainPanel);
            add(scrollPane, BorderLayout.CENTER);

            // Back button
            JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Close the current window
                    new ManagerInterface(sql_con).setVisible(true);
                }
            });
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(backButton);
            add(buttonPanel, BorderLayout.SOUTH);
            setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    // new TableInfoScreen(con);
    // } catch (Exception e) {
    // }
    // }
    // });
    // }

}
