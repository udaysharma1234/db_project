package screens.waiter;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Calendar;

public class GenerateInvoicePage extends JFrame {

    public GenerateInvoicePage(Connection sql_con, int order_id, int waiter_id) {
        JTextField firstNameField, lastNameField;
        JComboBox<String> ratingComboBox;
        JLabel billAmountLabel;
        setTitle("Generate Invoice");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        try {
            Statement stmt = sql_con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(
                    "select sum(m.price * o.quantity_ordered) as bill_amount from ordered_item o join menu_item m using (item_id) where o.order_id="
                            + order_id + " and o.item_status='served';");
            rs.first();
            final double billAmount = rs.getDouble("bill_amount");
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new GridLayout(5, 2, 10, 10));

            // Bill Amount
            JLabel billLabel = new JLabel("Bill Amount:");
            billAmountLabel = new JLabel("$" + billAmount);

            // Customer First Name
            JLabel firstNameLabel = new JLabel("First Name:");
            firstNameField = new JTextField();

            // Customer Last Name
            JLabel lastNameLabel = new JLabel("Last Name:");
            lastNameField = new JTextField();

            // Rating
            JLabel ratingLabel = new JLabel("Rating (out of 5):");
            String[] ratings = { "1", "2", "3", "4", "5" };
            ratingComboBox = new JComboBox<>(ratings);
            ratingComboBox.setSelectedItem("5");

            // Add components to the main panel
            mainPanel.add(billLabel);
            mainPanel.add(billAmountLabel);
            mainPanel.add(firstNameLabel);
            mainPanel.add(firstNameField);
            mainPanel.add(lastNameLabel);
            mainPanel.add(lastNameField);
            mainPanel.add(ratingLabel);
            mainPanel.add(ratingComboBox);

            // Button panel
            JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
            JButton cashButton = new JButton("Cash");
            JButton cardButton = new JButton("Card");
            JButton upiButton = new JButton("UPI");

            // Add buttons to button panel
            buttonPanel.add(cashButton);
            buttonPanel.add(cardButton);
            buttonPanel.add(upiButton);

            // Add action listeners to buttons
            cashButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    generateBill(sql_con, billAmount, "cash", order_id, firstNameField.getText(),
                            lastNameField.getText(), Integer.parseInt((String) ratingComboBox.getSelectedItem()));
                    dispose();
                    new TableListPage(waiter_id, sql_con);
                    return;
                }
            });

            cardButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    generateBill(sql_con, billAmount, "card", order_id, firstNameField.getText(),
                            lastNameField.getText(), Integer.parseInt((String) ratingComboBox.getSelectedItem()));
                    dispose();
                    new TableListPage(waiter_id, sql_con);
                    return;
                }
            });

            upiButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    generateBill(sql_con, billAmount, "UPI", order_id, firstNameField.getText(),
                            lastNameField.getText(), Integer.parseInt((String) ratingComboBox.getSelectedItem()));
                    dispose();
                    new TableListPage(waiter_id, sql_con);
                    return;
                }
            });
            JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    new TableListPage(waiter_id, sql_con);
                }
            });
            // Add main panel and button panel to content pane
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(backButton, BorderLayout.NORTH);
            getContentPane().add(mainPanel, BorderLayout.CENTER);
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);

            setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateBill(Connection sql_con, double bill_amount, String payment_method, int order_id,
            String cfirst_name, String clast_name, int rating) {
        try {
            PreparedStatement stmt1 = sql_con
                    .prepareStatement(
                            "UPDATE restaurant_order SET bill_amount=?, order_status=?, completion_time=? WHERE order_id=?");
            stmt1.setDouble(1, bill_amount);
            stmt1.setString(2, "completed");
            stmt1.setTimestamp(3, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            stmt1.setInt(4, order_id);
            stmt1.executeUpdate();
            PreparedStatement stmt2 = sql_con.prepareStatement(
                    "INSERT INTO invoice (waiter_rating, invoice_time, mode_of_payment) VALUES (?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            stmt2.setInt(1, rating);
            stmt2.setTimestamp(2, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            stmt2.setString(3, payment_method);
            stmt2.executeUpdate();
            ResultSet invoice_rs = stmt2.getGeneratedKeys();
            invoice_rs.first();
            int invoice_id = invoice_rs.getInt(1);
            PreparedStatement oi_stmt = sql_con
                    .prepareStatement("INSERT INTO order_invoice (invoice_id, order_id) VALUES (?,?)");
            oi_stmt.setInt(1, invoice_id);
            oi_stmt.setInt(2, order_id);
            oi_stmt.executeUpdate();
            PreparedStatement stmt3 = sql_con.prepareStatement(
                    "INSERT INTO customer_information (invoice_id, cfirst_name, clast_name) VALUES (?,?,?)");
            stmt3.setInt(1, invoice_id);
            stmt3.setString(2, cfirst_name);
            stmt3.setString(3, clast_name);
            stmt3.executeUpdate();
            Statement stmt4 = sql_con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet table_set = stmt4
                    .executeQuery("SELECT table_number FROM seated_at WHERE order_id=" + order_id + ";");
            table_set.first();
            int table_number = table_set.getInt(1);
            PreparedStatement stmt5 = sql_con
                    .prepareStatement("UPDATE restaurant_table SET table_status='available' WHERE table_number=?");
            stmt5.setInt(1, table_number);
            stmt5.executeUpdate();
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
    // new GenerateInvoicePage(con, 1, 2); // Pass the bill amount as an argument
    // } catch (Exception e) {
    // System.out.println(e);
    // }
    // }
    // });
    // }
}