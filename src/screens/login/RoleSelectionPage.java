package screens.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class RoleSelectionPage extends JFrame {

    public RoleSelectionPage(Connection sql_con) {
        setTitle("Role Selection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);// Center the window
        setResizable(false);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new GridLayout(2, 2));

        // Create buttons for different roles
        JButton waiterButton = new JButton("Waiter");
        JButton managerButton = new JButton("Manager");
        JButton cookButton = new JButton("Cook");
        JButton deliveryButton = new JButton("Delivery");

        // Add action listeners to the buttons
        waiterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the role selection window
                new LoginWindow("Waiter", sql_con).setVisible(true); // Open the login window for Waiter
            }
        });

        managerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the role selection window
                new LoginWindow("Manager", sql_con).setVisible(true); // Open the login window for Manager
            }
        });

        cookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the role selection window
                new LoginWindow("Cook", sql_con).setVisible(true); // Open the login window for Cook
            }
        });

        deliveryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the role selection window
                new LoginWindow("Delivery", sql_con).setVisible(true); // Open the login window for Delivery
            }
        });

        // Add buttons to the main panel
        mainPanel.add(waiterButton);
        mainPanel.add(managerButton);
        mainPanel.add(cookButton);
        mainPanel.add(deliveryButton);

        getContentPane().add(mainPanel);
    }

    // public static void main(String[] args) {
    // SwingUtilities.invokeLater(() -> {
    // new RoleSelectionPage().setVisible(true);
    // });
    // }
}
