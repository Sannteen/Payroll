import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PayrollGUI extends JFrame {
    private DefaultTableModel tableModel;
    private JTable employeeTable;
    private JComboBox<String> employeeTypeCombo;
    private JTextField firstNameField, lastNameField, ssnField;
    private JPanel inputPanel;
    private JPanel dynamicFieldsPanel;

    // Employee type specific fields
    private JTextField salaryField, salesField, rateField, baseField;
    private JTextField wageField, hoursField, descField, qtyField, priceField;

    public PayrollGUI() {
        setTitle("Payroll Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize all components first
        initializeComponents();

        // Set up the layout
        setupUI();

        // Set initial state
        updateInputFields();
    }

    private void initializeComponents() {
        // Initialize all field components
        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);
        ssnField = new JTextField(15);
        salaryField = new JTextField(15);
        salesField = new JTextField(15);
        rateField = new JTextField(15);
        baseField = new JTextField(15);
        wageField = new JTextField(15);
        hoursField = new JTextField(15);
        descField = new JTextField(15);
        qtyField = new JTextField(15);
        priceField = new JTextField(15);

        employeeTypeCombo = new JComboBox<>(new String[]{
                "Salaried", "Hourly", "Commission", "Base+Commission", "Invoice"
        });
        employeeTypeCombo.addActionListener(e -> updateInputFields());
    }

    private void setupUI() {
        // Create main input panel
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Static fields panel
        JPanel staticFieldsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        staticFieldsPanel.add(new JLabel("Type:"));
        staticFieldsPanel.add(employeeTypeCombo);
        staticFieldsPanel.add(new JLabel("First Name:"));
        staticFieldsPanel.add(firstNameField);
        staticFieldsPanel.add(new JLabel("Last Name:"));
        staticFieldsPanel.add(lastNameField);
        staticFieldsPanel.add(new JLabel("SSN/TRN:"));
        staticFieldsPanel.add(ssnField);

        // Dynamic fields panel
        dynamicFieldsPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        inputPanel.add(staticFieldsPanel);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(dynamicFieldsPanel);

        add(inputPanel, BorderLayout.NORTH);

        // Create employee table
        tableModel = new DefaultTableModel(
                new Object[]{"Type", "Full Name", "SSN/TRN", "Payment"}, 0);
        employeeTable = new JTable(tableModel);
        add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        // Create button panel
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addEmployee());
        panel.add(addButton);

        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> removeEmployee());
        panel.add(removeButton);

        JButton payButton = new JButton("Generate Pay Stubs");
        payButton.addActionListener(e -> generatePayStubs());
        panel.add(payButton);

        return panel;
    }

    private void updateInputFields() {
        // Clear previous dynamic fields
        dynamicFieldsPanel.removeAll();

        String type = (String) employeeTypeCombo.getSelectedItem();
        switch (type) {
            case "Salaried":
                addFieldPair("Weekly Salary:", salaryField);
                break;
            case "Hourly":
                addFieldPair("Hourly Wage:", wageField);
                addFieldPair("Hours Worked:", hoursField);
                break;
            case "Commission":
                addFieldPair("Gross Sales:", salesField);
                addFieldPair("Commission Rate:", rateField);
                break;
            case "Base+Commission":
                addFieldPair("Gross Sales:", salesField);
                addFieldPair("Commission Rate:", rateField);
                addFieldPair("Base Salary:", baseField);
                break;
            case "Invoice":
                addFieldPair("Part Description:", descField);
                addFieldPair("Quantity:", qtyField);
                addFieldPair("Price Per Item:", priceField);
                break;
        }

        dynamicFieldsPanel.revalidate();
        dynamicFieldsPanel.repaint();
    }

    private void addFieldPair(String label, JTextField field) {
        dynamicFieldsPanel.add(new JLabel(label));
        dynamicFieldsPanel.add(field);
    }

    private void addEmployee() {
        String type = (String) employeeTypeCombo.getSelectedItem();
        String name = firstNameField.getText() + " " + lastNameField.getText();
        String id = ssnField.getText();
        double amount = 0;

        try {
            switch (type) {
                case "Salaried":
                    amount = Double.parseDouble(salaryField.getText());
                    break;
                case "Hourly":
                    double wage = Double.parseDouble(wageField.getText());
                    double hours = Double.parseDouble(hoursField.getText());
                    amount = wage * hours;
                    break;
                case "Commission":
                    double sales = Double.parseDouble(salesField.getText());
                    double rate = Double.parseDouble(rateField.getText());
                    amount = sales * rate;
                    break;
                case "Base+Commission":
                    sales = Double.parseDouble(salesField.getText());
                    rate = Double.parseDouble(rateField.getText());
                    double base = Double.parseDouble(baseField.getText());
                    amount = (sales * rate) + base;
                    break;
                case "Invoice":
                    int qty = Integer.parseInt(qtyField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    amount = qty * price;
                    name = descField.getText(); // Use description as name for invoices
                    break;
            }

            tableModel.addRow(new Object[]{
                    type,
                    name,
                    id,
                    String.format("$%.2f", amount)
            });
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers in all fields",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee to remove",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void generatePayStubs() {
        try (FileWriter writer = new FileWriter("paystub.txt")) {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                writer.write(String.format("Name: %s%n", tableModel.getValueAt(i, 1)));
                writer.write(String.format("ID: %s%n", tableModel.getValueAt(i, 2)));
                writer.write(String.format("Payment Amount: %s%n", tableModel.getValueAt(i, 3)));
                writer.write(String.format("Date: %s%n%n", date));
            }

            JOptionPane.showMessageDialog(this,
                    "Pay stubs generated to paystub.txt",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error writing pay stubs: " + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        ssnField.setText("");
        salaryField.setText("");
        salesField.setText("");
        rateField.setText("");
        baseField.setText("");
        wageField.setText("");
        hoursField.setText("");
        descField.setText("");
        qtyField.setText("");
        priceField.setText("");
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            PayrollGUI gui = new PayrollGUI();
//            gui.setVisible(true);
//        });
//    }
}