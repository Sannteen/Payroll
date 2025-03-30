import javax.swing.SwingUtilities;
//javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(() -> {
            PayrollGUI payrollGUI = new PayrollGUI();
            payrollGUI.setVisible(true);
        });
    }
}