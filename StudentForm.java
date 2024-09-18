import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class StudentForm {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydatabase";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {

         // Initialize database
    initializeDatabase();
    
    // Create and show GUI
        SwingUtilities.invokeLater(StudentForm::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Student Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(6, 2));

        // Create form fields
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        JLabel ageLabel = new JLabel("Age:");
        JTextField ageField = new JTextField();

        JLabel marksLabel = new JLabel("Marks:");
        JTextField marksField = new JTextField();

        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField();

        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField();

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String ageText = ageField.getText().trim();
                String marksText = marksField.getText().trim();
                String address = addressField.getText().trim();
                String phone = phoneField.getText().trim();

                // Validation
                if (name.isEmpty() || ageText.isEmpty() || marksText.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int age;
                float marks;
                try {
                    age = Integer.parseInt(ageText);
                    marks = Float.parseFloat(marksText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Age and Marks must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (age < 0 || marks < 0) {
                    JOptionPane.showMessageDialog(frame, "Age and Marks must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Initialize database
                initializeDatabase();

                // Save to database
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    String sql = "INSERT INTO students (name, age, marks, address, phone_number) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, name);
                        stmt.setInt(2, age);
                        stmt.setFloat(3, marks);
                        stmt.setString(4, address);
                        stmt.setString(5, phone);

                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Student data submitted successfully.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error saving data to database.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add components to frame
        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(ageLabel);
        frame.add(ageField);
        frame.add(marksLabel);
        frame.add(marksField);
        frame.add(addressLabel);
        frame.add(addressField);
        frame.add(phoneLabel);
        frame.add(phoneField);
        frame.add(new JLabel()); // Empty label for layout alignment
        frame.add(submitButton);

        frame.setVisible(true);
    }


    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD)) {
            // Create database if not exists
            try (Statement stmt = conn.createStatement()) {
                String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS mydatabase";
                stmt.executeUpdate(createDatabaseSQL);
            }
    
            // Use the database
            conn.setCatalog("mydatabase");
    
            // Create table if not exists
            String createTableSQL = "CREATE TABLE IF NOT EXISTS students (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100), " +
                    "age INT, " +
                    "marks FLOAT, " +
                    "address VARCHAR(255), " +
                    "phone_number VARCHAR(15))";
    
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createTableSQL);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error initializing database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
          
}
