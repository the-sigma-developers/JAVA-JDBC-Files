import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class StudentDatabaseManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static final String CREATE_DATABASE_IF_NOT_EXISTS = "CREATE DATABASE IF NOT EXISTS school";
    private static final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS students (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "name VARCHAR(255) NOT NULL, " +
            "age INT NOT NULL, " +
            "grade INT NOT NULL" +
            ")";
    private static final String INSERT_STUDENT = "INSERT INTO students (name, age, grade) VALUES (?, ?, ?)";
    private static final String RETRIEVE_STUDENT_BY_ID = "SELECT * FROM students WHERE id = ?";
    private static final String RETRIEVE_STUDENT_BY_NAME = "SELECT * FROM students WHERE name = ?";

    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        try (Connection connection = getConnection()) {
            initializeDatabase(connection);

            while (true) {
                System.out.println("\nMenu:");
                System.out.println("1. Insert Student");
                System.out.println("2. Retrieve Student");
                System.out.println("3. Exit");
                System.out.print("\nPlease enter your choice : ");

                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        insertStudent(scanner, connection);
                        break;
                    case "2":
                        retrieveStudent(scanner, connection);
                        break;
                    case "3":
                        System.out.println("Exiting...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid Choice. Please try again.");
                }

                System.out.print("\nContinue? (y/n): ");
                String continueChoice = scanner.nextLine();
                if (!continueChoice.equalsIgnoreCase("y")) {
                    break;
                }
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private static void initializeDatabase(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_DATABASE_IF_NOT_EXISTS);
            System.out.println("Database created or already exists.");

            statement.execute("USE school"); // Assuming 'school' database exists

            statement.executeUpdate(CREATE_TABLE_IF_NOT_EXISTS);
            System.out.println("Table created or already exists.");
        }
    }

    private static void insertStudent(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();

        System.out.print("Enter student age (5-25): ");
        int age = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        if (age < 5 || age > 25) {
            throw new IllegalArgumentException("Age must be between 5 and 25.");
        }

        System.out.print("Enter student grade (1-10): ");
        int grade = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        if (grade < 1 || grade > 10) {
            throw new IllegalArgumentException("Grade must be between 1 and 10.");
        }

        try (PreparedStatement statement = connection.prepareStatement(INSERT_STUDENT)) {
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.setInt(3, grade);
            statement.executeUpdate();
            System.out.println("Student inserted successfully.");
        }
    }

    private static void retrieveStudent(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("\nRetrieve by:");
        System.out.println("1. ID");
        System.out.println("2. Name");
        System.out.print("\nPlease enter your choice : ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                retrieveStudentById(scanner, connection);
                break;
            case "2":
                retrieveStudentByName(scanner, connection);
                break;
            default:
                System.out.println("Invalid Choice. Please try again.");
        }
    }

    private static void retrieveStudentById(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter student ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        try (PreparedStatement statement = connection.prepareStatement(RETRIEVE_STUDENT_BY_ID)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("\nStudent Details:");
                System.out.println("ID: " + resultSet.getInt("id"));
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("Age: " + resultSet.getInt("age"));
                System.out.println("Grade: " + resultSet.getInt("grade"));
            } else {
                System.out.println("Student not found.");
            }
        }
    }

    private static void retrieveStudentByName(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();

        try (PreparedStatement statement = connection.prepareStatement(RETRIEVE_STUDENT_BY_NAME)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("\nStudent Details:");
                System.out.println("ID: " + resultSet.getInt("id"));
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("Age: " + resultSet.getInt("age"));
                System.out.println("Grade: " + resultSet.getInt("grade"));
            } else {
                System.out.println("Student not found.");
            }
        }
    }
}