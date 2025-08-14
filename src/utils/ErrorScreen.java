package utils;

import javax.swing.*;
import java.awt.*;

public class ErrorScreen extends JFrame {
    private JLabel errorMessageLabel;

    public ErrorScreen(String errorMessage) {
        setTitle("Error");
        setSize(300, 150);
        setLocationRelativeTo(null);

        // Create error message label
        errorMessageLabel = new JLabel(errorMessage);
        errorMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add error message label to the frame
        getContentPane().add(errorMessageLabel, BorderLayout.CENTER);
    }

}