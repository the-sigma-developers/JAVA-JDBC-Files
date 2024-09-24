import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Scanner;

public class Calculator {
    // Database credentials
    static final String DB_URL = "jdbc:mysql://localhost:3306";
    static final String USER = "root";
    static final String PASS = "";
    static final String DATABASE = "calculator_db";

    public static void main(String[] args) {
        // Create the database and table if they don't exist
        createDatabaseAndTableIfNotExists();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nCalculator Menu:");
            System.out.println("1. Perform Calculation");
            System.out.println("2. View Previous Calculations");
            System.out.println("3. Exit");
            System.out.print("\nChoose an option: ");
            int choice = scanner.nextInt();

            if (choice == 1) {
                performCalculation();
            } else if (choice == 2) {
                retrieveCalculations();
            } else if (choice == 3) {
                System.out.println("Exiting...");
                break;
            } else {
                System.out.println("Invalid option! Please try again.");
            }
        }
        scanner.close();
    }

    // Method to create the database and table if they do not exist
    public static void createDatabaseAndTableIfNotExists() {
        String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + DATABASE;
        String useDatabaseSQL = "USE " + DATABASE;
        String createTableSQL = "CREATE TABLE IF NOT EXISTS calculations ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "expression VARCHAR(255) NOT NULL, "
                + "result DOUBLE NOT NULL, "
                + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            // Create the database if it doesn't exist
            stmt.execute(createDatabaseSQL);
            System.out.println("\nDatabase '" + DATABASE + "' is ready!");

            // Switch to the newly created database
            stmt.execute(useDatabaseSQL);

            // Create the table if it doesn't exist
            stmt.execute(createTableSQL);
            System.out.println("Table 'calculations' is ready!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void performCalculation() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter first number: ");
        double num1 = scanner.nextDouble();
        System.out.print("Enter an operator (+, -, *, /): ");
        char operator = scanner.next().charAt(0);
        System.out.print("Enter second number: ");
        double num2 = scanner.nextDouble();
        double result = 0;

        try {
            switch (operator) {
                case '+':
                    result = num1 + num2;
                    break;
                case '-':
                    result = num1 - num2;
                    break;
                case '*':
                    result = num1 * num2;
                    break;
                case '/':
                    if (num2 == 0) {
                        throw new ArithmeticException("Cannot divide by zero!");
                    }
                    result = num1 / num2;
                    break;
                default:
                    System.out.println("Invalid operator!");
                    return;
            }

            String expression = num1 + " " + operator + " " + num2;
            System.out.println("Result: " + result);

            // Save to database
            saveCalculation(expression, result);

        } catch (ArithmeticException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void saveCalculation(String expression, double result) {
        String insertSQL = "INSERT INTO calculations (expression, result) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL + "/" + DATABASE, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, expression);
            pstmt.setDouble(2, result);
            pstmt.executeUpdate();
            System.out.println("Calculation saved to database!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void retrieveCalculations() {
        String querySQL = "SELECT * FROM calculations ORDER BY timestamp DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL + "/" + DATABASE, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {

            System.out.println("\nPrevious Calculations:");
            while (rs.next()) {
                String expression = rs.getString("expression");
                double result = rs.getDouble("result");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                System.out.println(timestamp + " | " + expression + " = " + result);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
