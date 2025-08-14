package screens.manager;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManageEmployee extends JFrame {

    public ManageEmployee(Connection sql_con) {
        setTitle("Employee List");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 5));
        JScrollPane scrollPane = new JScrollPane(panel);
        try {
            Statement stmt = sql_con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "(select e.employee_id, e.first_name, e.last_name, e.salary, e.shift, 'Waiter' as emp_role from waiter w left join employee e using (employee_id) union (select e.employee_id, e.first_name, e.last_name, e.salary, e.shift, 'Cook' as emp_role from cook c left join employee e using (employee_id) union (select e.employee_id, e.first_name, e.last_name, e.salary, e.shift, 'Delivery' as emp_role from delivery_boy b left join employee e using (employee_id))));");
            // Add employees dynamically
            for (; rs.next();) {

                JLabel nameLabel = new JLabel("Name: " + rs.getString("first_name") + " " + rs.getString("last_name"));
                JLabel roleLabel = new JLabel("Role: " + rs.getString("emp_role"));

                // Create a text field for salary with default value
                JPanel salaryPanel = new JPanel();
                JLabel salaryLabel = new JLabel("Salary: ");
                JTextField salaryField = new JTextField(rs.getString("salary"));
                salaryPanel.add(salaryLabel);
                salaryPanel.add(salaryField);

                // Create a dropdown for shift
                JPanel shiftPanel = new JPanel();
                JLabel shiftLabel = new JLabel("Shift");
                JComboBox<String> shiftDropdown = new JComboBox<>(new String[] { "Lunch", "Dinner", "Both" });
                shiftDropdown.setSelectedItem(rs.getString("shift"));
                shiftPanel.add(shiftLabel);
                shiftPanel.add(shiftDropdown);
                int employee_id = rs.getInt("employee_id");

                JButton updateButton = new JButton("Update");
                updateButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            PreparedStatement stmt = sql_con
                                    .prepareStatement("UPDATE employee SET salary=?,shift=? WHERE employee_id=?");
                            stmt.setInt(1, Integer.parseInt(salaryField.getText()));
                            stmt.setString(2, (String) shiftDropdown.getSelectedItem());
                            stmt.setInt(3, employee_id);
                            stmt.executeUpdate();
                            dispose();
                            new ManageEmployee(sql_con).setVisible(true);
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    }
                });

                panel.add(nameLabel);
                panel.add(roleLabel);
                panel.add(salaryPanel);
                panel.add(shiftPanel);
                panel.add(updateButton);
            }
            JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    new ManagerInterface(sql_con).setVisible(true);
                }
            });

            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(backButton, BorderLayout.SOUTH);
            add(mainPanel);
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
    // new ManageEmployee(con).setVisible(true);
    // } catch (Exception e) {
    // System.out.println(e);
    // }
    // }
    // });
    // }
}