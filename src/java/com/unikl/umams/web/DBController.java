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

    boolean verified(String email, String password) {
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

    Patient getPatient(String email) {
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
}
