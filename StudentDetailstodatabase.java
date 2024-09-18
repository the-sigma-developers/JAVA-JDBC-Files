import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

class StudentDetailstodatabase {

    private String name;
    private int age, grade;

    public StudentDetailstodatabase(String name, int age, int grade) {
        this.name = name;
        setAge(age);
        setGrade(grade);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 5 || age > 25) {
            throw new IllegalArgumentException("Enter Valid age (Between 5 and 25)");
        }
        this.age = age;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        if (grade < 1 || grade > 10) {
            throw new IllegalArgumentException("Enter Valid grade (Between 1 and 10)");
        }
        this.grade = grade;
    }

    // Database URL, username, and password
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "school";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Method to create database and table if they don't exist
    private static void initializeDatabase() {
        String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + DB_NAME + ".students (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "age INT NOT NULL, " +
                "grade INT NOT NULL" +
                ")";

        // Create database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Create database if it doesn't exist
            stmt.executeUpdate(createDatabaseQuery);
            System.out.println("Database created or already exists.");

        } catch (SQLException e) {
            System.out.println("Error creating database: " + e.getMessage());
            e.printStackTrace();
        }

        // Use the database and create table
        try (Connection conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Create table if it doesn't exist
            stmt.executeUpdate(createTableQuery);
            System.out.println("Table created or already exists.");

        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to insert student details into the database
    public void insertIntoDatabase() {
        String insertQuery = "INSERT INTO students (name, age, grade) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, this.name);
            pstmt.setInt(2, this.age);
            pstmt.setInt(3, this.grade);
            pstmt.executeUpdate();

            System.out.println("Student details inserted successfully.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Initialize database and table
        initializeDatabase();

        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                System.out.print("\nEnter Name of the student: ");
                String nm = sc.nextLine();

                System.out.print("Enter Age of the Student: ");
                int ag = sc.nextInt();

                System.out.print("Enter Grade of the Student: ");
                int gd = sc.nextInt();
                sc.nextLine();  // Consume newline

                StudentDetailstodatabase st = new StudentDetailstodatabase(nm, ag, gd);

                System.out.println("\n- : Student Details : -");
                System.out.println("Name: " + st.getName());
                System.out.println("Age: " + st.getAge());
                System.out.println("Grade: " + st.getGrade());

                // Insert the student details into the database
                st.insertIntoDatabase();

                System.out.print("Do you want to Enter Another Student (Yes/No): ");
                String rspnc = sc.nextLine();

                if (rspnc.equalsIgnoreCase("No")) {
                    break;
                }

            } catch (IllegalArgumentException e) {
                System.out.println("Exception Occurred: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Enter Valid Input");
            }
        }

        sc.close();
    }
}
