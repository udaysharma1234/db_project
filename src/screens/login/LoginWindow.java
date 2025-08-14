package screens.login;

import javax.swing.*;

import screens.cook.KitchenInterface;
import screens.delivery.DeliveryManagementSystem;
import screens.waiter.TableListPage;
import screens.manager.ManagerInterface;
import utils.ErrorScreen;
import utils.PasswordHash;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginWindow extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private String role;

    public LoginWindow(String role, Connection sql_con) {
        this.role = role;

        setTitle("Login - " + this.role);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);// Center the window
        setResizable(false);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create panel for login fields with GridLayout
        JPanel loginPanel = new JPanel(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBorder(BorderFactory.createEmptyBorder(10, 40, 0, 10));
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(150, 15)); // Set preferred size
        usernameField.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 40));
        usernameField.setBorder(BorderFactory.createLineBorder(Color.black, 1));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(10, 40, 0, 10));
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(150, 15)); // Set preferred size
        passwordField.setBorder(BorderFactory.createLineBorder(Color.black, 1));

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        // loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);

        // Add login panel to main panel (centered)
        mainPanel.add(loginPanel, BorderLayout.CENTER);

        // Create panel for login button with FlowLayout (centered)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());
                try {

                    Statement stmt = sql_con
                            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet employee = stmt.executeQuery("SELECT * FROM employee WHERE username='" + username
                            + "' AND password_hash='" + PasswordHash.hashPassword(password) + "'");

                    if (!employee.first()) {
                        dispose();
                        new ErrorScreen("Invalid username or password").setVisible(true);

                        return;
                    }
                    int employee_id = employee.getInt("employee_id");
                    final String role_string;
                    if (role == "Delivery") {
                        role_string = "delivery_boy";
                    } else {
                        role_string = role.toLowerCase();
                    }
                    ResultSet role_emp = stmt
                            .executeQuery("SELECT employee_id FROM " + role_string + " WHERE employee_id="
                                    + employee_id + ";");
                    if (!role_emp.first()) {
                        dispose();
                        new ErrorScreen("You're not a " + role).setVisible(true);
                        return;
                    }

                    switch (role) {
                        case "Waiter":
                            dispose();
                            new TableListPage(employee_id, sql_con).setVisible(true);
                            break;
                        case "Cook":
                            dispose();
                            new KitchenInterface(employee_id, sql_con).setVisible(true);
                            break;
                        case "Manager":
                            dispose();
                            new ManagerInterface(sql_con).setVisible(true);
                            break;
                        case "Delivery":
                            dispose();
                            new DeliveryManagementSystem(employee_id, sql_con).setVisible(true);
                            break;
                        default:
                            break;
                    }
                } catch (SQLException err) {
                    System.err.println(err);
                }

            }
        });

        buttonPanel.add(loginButton);

        // Add button panel to main panel (south position)
        mainPanel.add(buttonPanel, BorderLayout.AFTER_LAST_LINE);

        getContentPane().add(mainPanel);
    }

}
