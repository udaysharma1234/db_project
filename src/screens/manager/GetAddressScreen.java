package screens.manager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class GetAddressScreen extends JFrame {
    public GetAddressScreen(Connection sql_con, int order_id) {
        setTitle("Delivery Screen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        try {
            JPanel mainPanel = new JPanel(new BorderLayout());
            JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));

            JLabel addressLabel = new JLabel("Address:");
            JTextField addressField = new JTextField();
            Statement stmt = sql_con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(
                    "SELECT d.employee_id, CONCAT(e.first_name, ' ', e.last_name) as name FROM delivery_boy d LEFT JOIN employee e USING (employee_id);");
            ArrayList<String> representatives = new ArrayList<String>();
            for (; rs.next();) {
                representatives.add(rs.getString("employee_id") + "|" + rs.getString("name"));
            }
            String rep_arr[] = (String[]) representatives.toArray(new String[0]);
            JLabel representativeLabel = new JLabel("Delivery Representative:");
            JComboBox<String> representativeDropdown = new JComboBox<>(rep_arr);

            inputPanel.add(addressLabel);
            inputPanel.add(addressField);
            inputPanel.add(representativeLabel);
            inputPanel.add(representativeDropdown);

            JButton submitButton = new JButton("Submit");

            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String address = addressField.getText();
                    String selectedRepresentative = (String) representativeDropdown.getSelectedItem();
                    try {

                        PreparedStatement stmt1 = sql_con.prepareStatement(
                                "INSERT INTO delivered_by (employee_id, order_id, address) VALUES (?,?,?)");
                        stmt1.setInt(1, Integer.parseInt(selectedRepresentative.split("|")[0]));
                        stmt1.setInt(2, order_id);
                        stmt1.setString(3, address);
                        stmt1.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Delivery order placed successfully!"); // Show pop-up message
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            });

            mainPanel.add(inputPanel, BorderLayout.CENTER);
            mainPanel.add(submitButton, BorderLayout.SOUTH);
            JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Close the current window
                    new PlaceOrderPage(sql_con).setVisible(true);
                }
            });
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(backButton);
            add(buttonPanel, BorderLayout.SOUTH);
            add(mainPanel);
            setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");

                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurant_maestro",
                            "root",
                            "root@123");
                    new GetAddressScreen(con, 1).setVisible(true);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }
}
