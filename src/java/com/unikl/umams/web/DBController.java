/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unikl.umams.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.unikl.umams.utilities.*;
import com.unikl.umams.entities.*;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
/**
 *
 * @author Amirrudin
 */
public class DBController {
    private PreparedStatement pstmt;
    private Connection conn;

    public DBController() {
      initializeJdbc();
    }

    /** Initialize database connection */
    private void initializeJdbc() {
        try {
            // Declare driver and connection string
            String driver = "com.mysql.cj.jdbc.Driver";
            String connectionString = "jdbc:mysql://localhost:3306/ummcmedicalappointmentmanagementsystem?zeroDateTimeBehavior=CONVERT_TO_NULL&useSSL=false";

            // Load the Oracle JDBC Thin driver
            Class.forName(driver);
            System.out.println("Driver " + driver + " loaded");
               
            
            // Connect to the sample database
            conn = DriverManager.getConnection(connectionString, DBCredentials.getDbUserName(), DBCredentials.getDbPassword());
            System.out.println("Database " + connectionString + " connected");
        }
        catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex);
        }
    }
    
    public boolean createPatient(Patient patient) throws SQLException {
        boolean stat = false;
        int count = 0;
        count = DuplicateEmailOrNRIC(patient);
        try {
            if(count == 0){
                    // Create a Statement
                pstmt = conn.prepareStatement("INSERT INTO patients ("
                        + "PatientID, NRIC, Email, Password, FirstName, LastName, Ethnicity, PhoneNumber, Address, Height, BloodType) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                pstmt.setString(1, IDGenerator.generatePatientID(10));
                pstmt.setString(2, patient.getNRIC());
                pstmt.setString(3, patient.getEmail());
                pstmt.setString(4, patient.getPassword());
                pstmt.setString(5, patient.getFirstName());
                pstmt.setString(6, patient.getLastName());
                pstmt.setString(7, patient.getEthnicity());
                pstmt.setString(8, patient.getPhoneNumber());
                pstmt.setString(9, patient.getAddress());
                pstmt.setFloat(10, patient.getHeight());
                pstmt.setString(11, patient.getBloodType());
        
                pstmt.executeUpdate();
                stat = true;
            }
            else{
                stat = false;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stat;
    }
    
    public int DuplicateEmailOrNRIC(Patient patient){
        
        String countQuery = "SELECT COUNT(*) AS count FROM patients WHERE Email = ? OR NRIC = ?";
        int count = 0;
        try {
            pstmt = conn.prepareStatement(countQuery);
            pstmt.setString(1, patient.getEmail());
            pstmt.setString(2, patient.getNRIC());
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt("count");
                // Do something with the count value
            }
            return count;
        } catch (SQLException e) {
        }
        return count;
    }

    public boolean verified(String email, String password) {
        int count = 0;
        
        try {
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM patients WHERE Email = ? AND Password = ?");
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            ResultSet rs =  pstmt.executeQuery();
            rs.next();
            count = rs.getInt(1);
            
            
        } catch (SQLException e) {
            System.out.println("ERROR!!! " + e.getMessage());
        }
        return (count > 0);
    }
    
    public boolean verifiedDoctor(String email, String password) {
        int count = 0;
        
        try {
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM doctors WHERE DoctorEmail = ? AND Password = ?");
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            ResultSet rs =  pstmt.executeQuery();
            rs.next();
            count = rs.getInt(1);
            
            
        } catch (SQLException e) {
            System.out.println("ERROR!!! " + e.getMessage());
        }
        return (count > 0);
    }

    public Patient getPatient(String email) {
        Patient loggedInPatient = new Patient();
        try {
            pstmt = conn.prepareStatement("SELECT PatientID, Email, FirstName, Password FROM patients WHERE Email = ?");
            pstmt.setString(1, email);
            
            ResultSet rs =  pstmt.executeQuery();
            rs.next();
            
            loggedInPatient.setPatientID(rs.getString("PatientID"));
            loggedInPatient.setPassword(rs.getString("Password"));
            loggedInPatient.setEmail(rs.getString("Email"));
            loggedInPatient.setFirstName(rs.getString("FirstName"));
            
        } catch (SQLException e) {
            System.out.println("ERROR!!! " + e.getMessage());
        }
       
        return loggedInPatient;
    }
    
    public void createAppointment(Appointment inAppointment){
        try {
            pstmt = conn.prepareStatement("INSERT into appointments (AppointmentID, CreatedTime, Symptoms, OtherDescription, AppointmentDate, AppointmentTime, AppointmentStatus, Weight, BloodPressure, Temperature, OxygenLevel, AdditionalNotes, Diagnosis, PatientID, DoctorID)"
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
            pstmt.setString(1, IDGenerator.generateAppointmentID(10));
            pstmt.setString(2, inAppointment.getCreatedDateTime());
            pstmt.setString(3, inAppointment.getSymptoms());
            pstmt.setString(4, inAppointment.getOtherDescription());
            pstmt.setString(5, inAppointment.getAppointmentDate());
            pstmt.setString(6, inAppointment.getAppointmentTime());
            pstmt.setString(7, inAppointment.getAppointmentStatus());
            pstmt.setFloat(8, inAppointment.getWeight());
            pstmt.setFloat(9, inAppointment.getBloodPressure());
            pstmt.setFloat(10, inAppointment.getTemperature());
            pstmt.setFloat(11, inAppointment.getOxygenLevel());
            pstmt.setString(12, "");//additional notes
            pstmt.setString(13, "");//diagnosis
            pstmt.setString(14, inAppointment.getPatientID());
            pstmt.setString(15, "");//doctorID
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public ArrayList<Appointment> getAppointments(String patientID) throws SQLException{
        ArrayList<Appointment> tempArray = new ArrayList<>();
        pstmt = conn.prepareStatement("SELECT AppointmentID, AppointmentDate, AppointmentTime, AppointmentStatus, CreatedTime FROM appointments WHERE PatientID = ? ORDER BY AppointmentDate DESC");
        pstmt.setString(1, patientID);
        ResultSet rs =  pstmt.executeQuery();
            while (rs.next()) {
                Appointment temp = new Appointment();
                temp.setAppointmentID(rs.getString("AppointmentID"));
                temp.setAppointmentDate(rs.getString("AppointmentDate"));
                temp.setAppointmentTime(rs.getString("AppointmentTime"));
                temp.setAppointmentStatus(rs.getString("AppointmentStatus"));
                temp.setCreatedDateTime(rs.getString("CreatedTime"));
           
                tempArray.add(temp);
            }
        return tempArray;
    }
    
    public ArrayList<Appointment> getAllAppointments() throws SQLException{
        ArrayList<Appointment> tempArray = new ArrayList<>();
        pstmt = conn.prepareStatement("SELECT AppointmentID, AppointmentDate, AppointmentTime, AppointmentStatus FROM appointments ORDER BY AppointmentStatus DESC ");
        ResultSet rs =  pstmt.executeQuery();
            while (rs.next()) {
                Appointment temp = new Appointment();
                temp.setAppointmentID(rs.getString("AppointmentID"));
                temp.setAppointmentDate(rs.getString("AppointmentDate"));
                temp.setAppointmentTime(rs.getString("AppointmentTime"));
                temp.setAppointmentStatus(rs.getString("AppointmentStatus"));
           
                tempArray.add(temp);
            }
        return tempArray;
    }
    
    public ArrayList<Appointment> getAssignedAppointments(String doctorID) throws SQLException{
        ArrayList<Appointment> tempArray = new ArrayList<>();
        pstmt = conn.prepareStatement("SELECT AppointmentID, AppointmentDate, AppointmentTime, AppointmentStatus FROM appointments WHERE DoctorID = ? ORDER BY AppointmentStatus DESC ");
        pstmt.setString(1, doctorID);
        ResultSet rs =  pstmt.executeQuery();
            while (rs.next()) {
                Appointment temp = new Appointment();
                temp.setAppointmentID(rs.getString("AppointmentID"));
                temp.setAppointmentDate(rs.getString("AppointmentDate"));
                temp.setAppointmentTime(rs.getString("AppointmentTime"));
                temp.setAppointmentStatus(rs.getString("AppointmentStatus"));
           
                tempArray.add(temp);
            }
        return tempArray;
    }
    
    public String getDoctorID(String doctorEmail) throws SQLException{
        String doctorID = "";
        pstmt = conn.prepareStatement("SELECT DoctorID FROM doctors WHERE DoctorEmail = ? ");
        pstmt.setString(1, doctorEmail);
        
        ResultSet rs =  pstmt.executeQuery();
        rs.next();
        doctorID = rs.getString("DoctorID");
        return doctorID;
    }

    public Appointment viewAppointment(String appointmentID) throws SQLException {
        Appointment appointment = new Appointment();
        pstmt = conn.prepareStatement("SELECT * FROM appointments WHERE AppointmentID = ? ");
        pstmt.setString(1, appointmentID);
        
        ResultSet rs =  pstmt.executeQuery();
        rs.next();
        
        appointment.setAppointmentID(rs.getString("AppointmentID"));
        appointment.setCreatedDateTime(rs.getString("CreatedTime"));
        appointment.setSymptoms(rs.getString("Symptoms"));
        appointment.setOtherDescription(rs.getString("OtherDescription"));
        appointment.setAppointmentDate(rs.getString("AppointmentDate"));
        appointment.setAppointmentTime(rs.getString("AppointmentTime"));
        appointment.setAppointmentStatus(rs.getString("AppointmentStatus"));
        appointment.setWeight(rs.getFloat("Weight"));
        appointment.setBloodPressure(rs.getFloat("BloodPressure"));
        appointment.setTemperature(rs.getFloat("Temperature"));
        appointment.setOxygenLevel(rs.getFloat("OxygenLevel"));
        appointment.setAdditionalNotes(rs.getString("AdditionalNotes"));
        appointment.setDiagnosis(rs.getString("Diagnosis"));
        appointment.setDoctorID(rs.getString("DoctorID"));
        
        return appointment;
    }

    public void cancelAppointment(String appointmentID) throws SQLException {
        
        pstmt = conn.prepareStatement("UPDATE appointments SET AppointmentStatus = 'CANCELLED' WHERE AppointmentID = ?");
        pstmt.setString(1, appointmentID);
        pstmt.execute();
        
        
    }
    
    public ResultSet getDoctors() throws SQLException{
        ResultSet res;
        pstmt = conn.prepareStatement("SELECT * FROM doctors");
        
        res = pstmt.executeQuery();
        return res;
    }

    void registerDoctor(String name, String eduBackground, String phoneNumber, String location, String email, String password) {
        try {
            pstmt = conn.prepareStatement("INSERT into doctors (DoctorID, FullName, EducationBackground, PhoneNumber, Password, DoctorEmail, Location)"
                    + "VALUES (?,?,?,?,?,?,?)");
            
            pstmt.setString(1, IDGenerator.generatePatientID(8));
            pstmt.setString(2, name);
            pstmt.setString(3, eduBackground);
            pstmt.setString(4, phoneNumber);
            pstmt.setString(5, password);
            pstmt.setString(6, email);
            pstmt.setString(7, location);
            
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void confirmAppointment(String appointmentID, String doctorID) throws SQLException {
        pstmt = conn.prepareStatement("UPDATE appointments SET AppointmentStatus = 'CONFIRMED', DoctorID = ? WHERE AppointmentID = ?");
        pstmt.setString(1, doctorID);
        pstmt.setString(2, appointmentID);
        pstmt.execute();
    }

    void completeAppointment(String appointmentID, float weight, float bloodPressure, float temperature, float oxygenLevel, String diagnosis, String additionalNotes) throws SQLException {
        pstmt = conn.prepareStatement("UPDATE appointments SET "
                + "Weight = ?, "
                + "BloodPressure = ?, "
                + "Temperature = ?, "
                + "OxygenLevel = ?, "
                + "AdditionalNotes = ?, "
                + "Diagnosis = ?, "
                + "AppointmentStatus = 'COMPLETED' "
                + "WHERE AppointmentID = ?");
        
        pstmt.setFloat(1, weight);
        pstmt.setFloat(2, bloodPressure);
        pstmt.setFloat(3, temperature);
        pstmt.setFloat(4, oxygenLevel);
        pstmt.setString(5, additionalNotes);
        pstmt.setString(6, diagnosis);
        pstmt.setString(7, appointmentID);
        pstmt.execute();
    }
    
}
