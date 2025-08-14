import java.sql.*;
import javax.swing.*;

import screens.login.RoleSelectionPage;

public class RMS {
    public static void main(String args[]) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurant_maestro", "root",
                    "root@123");
            SwingUtilities.invokeLater(() -> {
                new RoleSelectionPage(con).setVisible(true);
            });
        } catch (SQLException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
