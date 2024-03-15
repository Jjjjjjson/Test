import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class DatabaseApp {
    private static final String URL = "jdbc:postgresql://localhost:5432/Assignment_03";
    private static final String USER = "postgres";
    private static final String PASSWORD = "001125";
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public static void main(String[] args) {
        DatabaseApp app = new DatabaseApp();
        while (true) {
            System.out.println("1. Add Student");
            System.out.println("2. Delete Student");
            System.out.println("3. List All Students");
            System.out.println("4. Update Student Email");
            System.out.println("5. Exit\n");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    app.addStudent();
                    break;
                case 2:
                    app.deleteStudent();
                    break;
                case 3:
                    app.getAllStudents();
                    break;
                case 4:
                    app.updateStudentEmail();
                    break;
                case 5:
                    System.exit(0);
                default:
                    System.out.println("---Invalid option. Please choose again---");
            }
            System.out.println("\n------------------------------------------------\n");
        }
    }

    private void getAllStudents() {
        String query = "SELECT * FROM students;";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println(rs.getInt("student_id") + ": " +
                        rs.getString("first_name") + " " +
                        rs.getString("last_name") + ", " +
                        rs.getString("email") + ", " +
                        rs.getDate("enrollment_date"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addStudent() {
        // User input section
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        LocalDate enrollmentDate = null;
        while (enrollmentDate == null) {
            System.out.print("Enter enrollment date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine();
            enrollmentDate = validateDate(dateInput);
            if (enrollmentDate == null) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        String query = "INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES (?, ?, ?, ?);";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setDate(4, Date.valueOf(enrollmentDate));
            pstmt.executeUpdate();
            System.out.println("Student added successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private LocalDate validateDate(String dateInput) {
        try {
            return LocalDate.parse(dateInput, dateFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void deleteStudent() {
        System.out.print("Enter student ID to delete: ");
        int studentId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String query = "DELETE FROM students WHERE student_id = ?;";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Student deleted successfully.");
            } else {
                System.out.println("Student with ID " + studentId + " not found.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateStudentEmail() {
        System.out.print("Enter student ID to update: ");
        int studentId = scanner.nextInt();
        scanner.nextLine(); // Consume newline left after reading integer

        System.out.print("Enter new email: ");
        String newEmail = scanner.nextLine();

        String query = "UPDATE students SET email = ? WHERE student_id = ?;";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newEmail);
            pstmt.setInt(2, studentId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Email updated successfully.");
            } else {
                System.out.println("Student with ID " + studentId + " not found.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
