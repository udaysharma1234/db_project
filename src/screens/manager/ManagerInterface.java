package screens.manager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import screens.login.*;

import javax.swing.*;

public class ManagerInterface extends JFrame {

    public ManagerInterface(Connection sql_con) {
        JPanel selectionPanel;
        setTitle("Manager Interface");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        selectionPanel = new JPanel(new GridLayout(2, 2));

        // Add buttons for property selection
        JButton menuButton = createStyledButton("Menu");
        JButton tablesButton = createStyledButton("Tables");
        JButton employeeButton = createStyledButton("Employee");
        JButton customerButton = createStyledButton("Customer Response");
        JButton placeOrderButton = createStyledButton("Place Delivery Order");
        JButton logoutButton = createStyledButton("Logout");

        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new UpdateItemScreen(sql_con).setVisible(true);
            }
        });

        employeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ManageEmployee(sql_con).setVisible(true);
            }
        });

        tablesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new TableInfoScreen(sql_con).setVisible(true);

            }
        });

        placeOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new PlaceOrderPage(sql_con).setVisible(true);
            }
        });

        customerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new CustomerResponse(sql_con).setVisible(true);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close current window
                new RoleSelectionPage(sql_con).setVisible(true);
            }
        });

        selectionPanel.add(menuButton);
        selectionPanel.add(tablesButton);
        selectionPanel.add(employeeButton);
        selectionPanel.add(customerButton);

        // Show property selection initially
        setLayout(new BorderLayout());
        add(selectionPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(placeOrderButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH); // Add buttons panel to the bottom of the frame
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.setMargin(new Insets(10, 10, 10, 10));
        return button;
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurant_maestro", "root",
                    "root@123");
            SwingUtilities.invokeLater(() -> {
                new ManagerInterface(con).setVisible(true);
            });
        } catch (SQLException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
