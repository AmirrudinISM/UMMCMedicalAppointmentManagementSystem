/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unikl.umams.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
            updateMissedAppointments();
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
                        + "PatientID, NRIC, Email, Password, FirstName, LastName, Ethnicity, PhoneNumber, Address, Height, BloodType, ChronicDiseases) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
                pstmt.setString(12, patient.getChronicIllnesses());
        
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
            System.out.println("Size is: " + rs.getFetchSize());
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
            pstmt = conn.prepareStatement("INSERT into appointments (AppointmentID, CreatedTime, Symptoms, OtherDescription, AppointmentDate, AppointmentTime, AppointmentStatus, Weight, Temperature, OxygenLevel, AdditionalNotes, Diagnosis, PatientID, DoctorID, Prescription, SystolicBP, DiastolicBP)"
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
            pstmt.setString(1, IDGenerator.generateAppointmentID(10));
            pstmt.setString(2, inAppointment.getCreatedDateTime());
            pstmt.setString(3, inAppointment.getSymptoms());
            pstmt.setString(4, inAppointment.getOtherDescription());
            pstmt.setString(5, inAppointment.getAppointmentDate());
            pstmt.setString(6, inAppointment.getAppointmentTime());
            pstmt.setString(7, inAppointment.getAppointmentStatus());
            pstmt.setFloat(8, 0);//weight
            pstmt.setFloat(9, 0);//temperature
            pstmt.setFloat(10, 0);//oxygenlevel
            pstmt.setString(11, "");//additional notes
            pstmt.setString(12, "");//diagnosis
            pstmt.setString(13, inAppointment.getPatientID());
            pstmt.setString(14, "");//doctorID
            pstmt.setString(15, "");//prescription
            pstmt.setFloat(16, 0);//systolicBP
            pstmt.setFloat(17, 0);//diastolicBP
            
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
        appointment.setTemperature(rs.getFloat("Temperature"));
        appointment.setOxygenLevel(rs.getFloat("OxygenLevel"));
        appointment.setAdditionalNotes(rs.getString("AdditionalNotes"));
        appointment.setDiagnosis(rs.getString("Diagnosis"));
        appointment.setDoctorID(rs.getString("DoctorID"));
        appointment.setPrescription(rs.getString("Prescription"));
        appointment.setSystolicBP(rs.getFloat("SystolicBP"));
        appointment.setDiastolicBP(rs.getFloat("DiastolicBP"));
        
        return appointment;
    }
    
    public JsonObject viewAppointmentJSON(String appointmentID) throws SQLException{
        JsonObject response = new JsonObject();
        
            
        pstmt = conn.prepareStatement("SELECT \n" +
            "appointments.AppointmentID, \n" +
            "appointments.CreatedTime, \n" +
            "appointments.Symptoms, \n" +
            "appointments.OtherDescription, \n" +
            "appointments.AppointmentDate, \n" +
            "appointments.AppointmentTime, \n" +
            "appointments.AppointmentStatus, \n" +
            "appointments.Weight, \n" +
            "appointments.systolicBP, \n" +
            "appointments.diastolicBP, \n" +
            "appointments.Temperature, \n" +
            "appointments.OxygenLevel, \n" +
            "appointments.AdditionalNotes,\n" +
            "appointments.Diagnosis,\n" +
            "appointments.Prescription,\n" +
            "appointments.PatientID,\n" +
            "appointments.DoctorID,\n" +
            "doctors.FullName\n" +
            "FROM appointments\n" +
            "INNER JOIN doctors\n" +
            "ON appointments.DoctorID = doctors.DoctorID\n" +
            "WHERE AppointmentID = ? ");
             
        pstmt.setString(1, appointmentID);
        
        ResultSet rs =  pstmt.executeQuery();
        if(rs.isBeforeFirst()){
            rs.next();
        
            response.addProperty("appointmentID", rs.getString("AppointmentID"));
            response.addProperty("createdTime", rs.getString("CreatedTime"));
            response.addProperty("symptoms", rs.getString("Symptoms"));
            response.addProperty("otherDescription", rs.getString("OtherDescription"));
            response.addProperty("appointmentDate", rs.getString("AppointmentDate"));
            response.addProperty("appointmentTime", rs.getString("AppointmentTime"));
            response.addProperty("appointmentStatus", rs.getString("AppointmentStatus"));
            response.addProperty("weight", rs.getString("Weight"));
            response.addProperty("systolicBP", rs.getString("systolicBP"));
            response.addProperty("diastolicBP", rs.getString("diastolicBP"));
            response.addProperty("temperature", rs.getString("Temperature"));
            response.addProperty("oxygenLevel", rs.getString("OxygenLevel"));
            response.addProperty("additionalNotes", rs.getString("AdditionalNotes"));
            response.addProperty("diagnosis", rs.getString("Diagnosis"));
            response.addProperty("prescription", rs.getString("Prescription"));
            response.addProperty("patientID", rs.getString("PatientID"));
            response.addProperty("doctorID", rs.getString("DoctorID"));
            response.addProperty("fullName", rs.getString("FullName"));
        }
        else{
            pstmt = conn.prepareStatement("SELECT * FROM appointments WHERE AppointmentID = ? ");
            pstmt.setString(1, appointmentID);
            rs =  pstmt.executeQuery();
            
            rs.next();
            response.addProperty("appointmentID", rs.getString("AppointmentID"));
            response.addProperty("createdTime", rs.getString("CreatedTime"));
            response.addProperty("symptoms", rs.getString("Symptoms"));
            response.addProperty("otherDescription", rs.getString("OtherDescription"));
            response.addProperty("appointmentDate", rs.getString("AppointmentDate"));
            response.addProperty("appointmentTime", rs.getString("AppointmentTime"));
            response.addProperty("appointmentStatus", rs.getString("AppointmentStatus"));
            response.addProperty("weight", rs.getString("Weight"));
            response.addProperty("systolicBP", rs.getString("systolicBP"));
            response.addProperty("diastolicBP", rs.getString("diastolicBP"));
            response.addProperty("temperature", rs.getString("Temperature"));
            response.addProperty("oxygenLevel", rs.getString("OxygenLevel"));
            response.addProperty("additionalNotes", rs.getString("AdditionalNotes"));
            response.addProperty("diagnosis", rs.getString("Diagnosis"));
            response.addProperty("patientID", rs.getString("PatientID"));
            response.addProperty("doctorID", rs.getString("DoctorID"));
            response.addProperty("fullName", "");
        }
        
        
        return response;
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

    void completeAppointment(String appointmentID, float weight, float temperature, float oxygenLevel, String diagnosis, String additionalNotes, String prescription, float systolicBP, float diastolicBP) throws SQLException {
        pstmt = conn.prepareStatement("UPDATE appointments SET "
                + "Weight = ?, "
                + "Temperature = ?, "
                + "OxygenLevel = ?, "
                + "AdditionalNotes = ?, "
                + "Diagnosis = ?, "
                + "AppointmentStatus = 'COMPLETED', "
                + "Prescription = ?,"
                + "SystolicBP = ?,"
                + "DiastolicBP = ?"
                + "WHERE AppointmentID = ?");
        
        pstmt.setFloat(1, weight);
        pstmt.setFloat(2, temperature);
        pstmt.setFloat(3, oxygenLevel);
        pstmt.setString(4, additionalNotes);
        pstmt.setString(5, diagnosis);
        pstmt.setString(6, prescription);
        pstmt.setFloat(7, systolicBP);
        pstmt.setFloat(8, diastolicBP);
        pstmt.setString(9, appointmentID);
        pstmt.execute();
    }

    void updateMissedAppointments() throws SQLException {
       pstmt = conn.prepareStatement("UPDATE appointments SET AppointmentStatus = 'MISSED' WHERE (AppointmentStatus = 'PENDING' OR AppointmentStatus = 'CONFIRMED') AND AppointmentDate < CURDATE()");
       pstmt.execute();
       System.out.println("Missed appointments updated");
    }

    Patient viewProfile(String patientID) throws SQLException{
        Patient patient = new Patient();
        pstmt = conn.prepareStatement("SELECT * FROM patients WHERE PatientID = ? ");
        pstmt.setString(1, patientID);
        ResultSet rs =  pstmt.executeQuery();
        rs.next();
        
        patient.setPatientID(rs.getString("PatientID"));
        patient.setNRIC(rs.getString("NRIC"));
        patient.setEmail(rs.getString("Email"));
        patient.setFirstName(rs.getString("FirstName"));
        patient.setLastName(rs.getString("LastName"));
        patient.setEthnicity(rs.getString("Ethnicity"));
        patient.setPhoneNumber(rs.getString("PhoneNumber"));
        patient.setAddress(rs.getString("Address"));
        patient.setHeight(rs.getFloat("Height"));
        patient.setBloodType(rs.getString("BloodType"));
        patient.setChronicIllnesses(rs.getString("ChronicDiseases"));
        
        return patient;
    }
    
    public boolean patientExist(String email){
        int count = 0;
        
        try {
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM patients WHERE Email = ?");
            pstmt.setString(1, email);
            
            ResultSet rs =  pstmt.executeQuery();
            rs.next();
            count = rs.getInt(1);
            
            
        } catch (SQLException e) {
            System.out.println("ERROR!!! " + e.getMessage());
        }
        return (count > 0);
        
    }

    void updateProfile(String patientID, String phoneNumber, String address, String height) throws SQLException {
        pstmt = conn.prepareStatement("UPDATE patients SET PhoneNumber = ?, Address = ?, Height = ? WHERE PatientID = ?");
        pstmt.setString(1, phoneNumber);
        pstmt.setString(2, address);
        pstmt.setFloat(3, Float.valueOf(height));
        pstmt.setString(4, patientID);
        pstmt.execute();
    }

    JsonObject viewDrProfile(String doctorID) throws SQLException {
        JsonObject response = new JsonObject();
        pstmt = conn.prepareStatement("SELECT * FROM doctors WHERE DoctorID = ? ");
        pstmt.setString(1, doctorID);
        ResultSet rs =  pstmt.executeQuery();
        rs.next();
        
        response.addProperty("name",rs.getString("FullName"));
        response.addProperty("qualification",rs.getString("EducationBackground"));
        response.addProperty("doctorID", rs.getString("DoctorID"));
        response.addProperty("email", rs.getString("DoctorEmail"));
        response.addProperty("phoneNumber", rs.getString("PhoneNumber"));
        response.addProperty("location", rs.getString("Location"));
        
        return response;
        
    }
    
}
