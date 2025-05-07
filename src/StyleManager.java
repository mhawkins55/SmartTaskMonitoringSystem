import javax.swing.*;
import java.awt.*;

public class StyleManager {

    // Global Look & Feel (call once)
    public static void applyNimbusLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Set background color for a frame
    public static void styleFrameBackground(JFrame frame) {
        frame.getContentPane().setBackground(new Color(200, 220, 240)); // light blue
    }

    // Style a button (color, font, etc.)
    public static void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(100, 149, 237)); // Cornflower blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    // Style multiple buttons
    public static void styleButtons(JButton... buttons) {
        for (JButton button : buttons) {
            styleButton(button);
        }
    }
}

