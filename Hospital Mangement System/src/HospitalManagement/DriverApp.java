package HospitalManagement;

import java.sql.*;
import java.util.Scanner;

public class DriverApp {
    private static final String URL = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Root@#07";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(URL, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while (true){
                System.out.println("--------- HOSPITAL MANAGEMENT SYSTEM ----------");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice){
                    case 1 : patient.addPatient();
                        System.out.println();
                        break;
                    case 2 : patient.viewPatient();
                        System.out.println();
                        break;
                    case 3 : doctor.viewDoctor();
                        System.out.println();
                        break;
                    case 4 : bookApppointment(connection,patient,doctor,scanner);
                        System.out.println();
                        break;
                    case 5 :  return;
                    default:
                        System.out.println("Invalid choice. Choose correctly!");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void bookApppointment(Connection connection,Patient patient,Doctor doctor,Scanner scanner){
        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            if(checkAvailability(doctorId,appointmentDate,connection)){
                String appointmentQuery = "INSERT INTO appointments (patientId,doctorId,appointmentDate) VALUES (?,?,?) ";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appointmentDate);
                    int rowsEffected = preparedStatement.executeUpdate();
                    if(rowsEffected > 0){
                        System.out.println("Appointment booked..");
                    }else {
                        System.out.println("Appointment not booked..");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("Doctor not available on this date");
            }
        }else{
            System.out.println("Patient Id or Doctor Id doesn't exist!");
        }
    }

    public static boolean checkAvailability(int doctorId,String appointmentDate,Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctorId = ? AND appointmentDate = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count =  resultSet.getInt(1);
                if(count == 0){
                    return true;
                }else{
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
