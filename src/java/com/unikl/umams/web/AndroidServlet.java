/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unikl.umams.web;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysql.cj.protocol.Resultset;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.unikl.umams.entities.*;
import static java.lang.System.out;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.ResultSet;

/**
 *
 * @author Amirrudin
 */
public class AndroidServlet extends HttpServlet {
    DBController db;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AndroidServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AndroidServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        db = new DBController();
        System.out.println("Request type: " + request.getParameter("action"));
        
        if(request.getParameter("action").equals("register")){
            String nric = request.getParameter("NRIC");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String ethnicity = request.getParameter("ethnicity");
            String phoneNumber = request.getParameter("phoneNumber");
            String address = request.getParameter("address");
            float height = Float.parseFloat(request.getParameter("height"));
            String bloodType = request.getParameter("bloodType");
            String chronicIllnesses = request.getParameter("chronicIllnesses");
            System.out.println("Params received: " + nric + ", " + email+ ", " + bloodType.length() + ", " + chronicIllnesses);
            Patient inPatient = new Patient( nric, email, password, firstName, lastName, ethnicity, phoneNumber, address, height, bloodType, chronicIllnesses);
        
            try {
                db.createPatient(inPatient);
            } catch (SQLException ex) {
                Logger.getLogger(AndroidServlet.class.getName()).log(Level.SEVERE, null, ex);
                out.println(ex.getMessage());
            }
        }
        if(request.getParameter("action").equals("login")){
            
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            System.out.println("Params received: " + email + ", " + password);
            if(db.verified(email, password)){
                Patient loggedIn = db.getPatient(email);
                Gson gson = new Gson();
                String patientInfo = gson.toJson(loggedIn);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(patientInfo);
                out.flush();
            }
            else{
                out.println("Password incorrect!");
            }
        }
        if(request.getParameter("action").equals("createAppointment")){
            Appointment inAppointment = new Appointment();
            
            System.out.println("createdTime: " + request.getParameter("createdTime"));
            inAppointment.setCreatedDateTime(request.getParameter("createdTime"));
            
            System.out.println("Symptoms: " + request.getParameter("symptoms"));
            inAppointment.setSymptoms(request.getParameter("symptoms"));
            
            System.out.println("OtherDescription: " + request.getParameter("otherDescription"));
            inAppointment.setOtherDescription(request.getParameter("otherDescription"));
            
            System.out.println("appointmentDate: " + request.getParameter("appointmentDate"));
            inAppointment.setAppointmentDate(request.getParameter("appointmentDate"));
            
            System.out.println("appointmentTime: " + request.getParameter("appointmentTime"));
            inAppointment.setAppointmentTime(request.getParameter("appointmentTime"));
            
            System.out.println("appointmentStatus: " + request.getParameter("appointmentStatus"));
            inAppointment.setAppointmentStatus(request.getParameter("appointmentStatus"));
            
            System.out.println("patientID: " + request.getParameter("patientID"));
            inAppointment.setPatientID(request.getParameter("patientID"));
            
            
            db.createAppointment(inAppointment);
            
        }
        
        if(request.getParameter("action").equals("getAppointments")){
            System.out.println("Hello");
            JsonArray jsonArray = new JsonArray();
            String patientID = request.getParameter("patientID");
            System.out.println("Patient ID: " + patientID);
            try {
                ArrayList<Appointment> tempAppointments = db.getAppointments(patientID);
                
                for(int i = 0; i < tempAppointments.size(); i++){
                    System.out.println("ID: " + tempAppointments.get(i).getAppointmentID() 
                            + ", createdDate: " + tempAppointments.get(i).getCreatedDateTime()
                            + ", Date: " + tempAppointments.get(i).getAppointmentDate()
                            + ", Time: " + tempAppointments.get(i).getAppointmentTime()
                            + ", Status: " + tempAppointments.get(i).getAppointmentStatus());
                }
                // Create a Gson instance
                Gson gson = new Gson();

                // Convert the ArrayList to a JSON string
                String json = gson.toJson(tempAppointments);
                System.out.println(json);
                
                response.setContentType("text/plain");
                PrintWriter out = response.getWriter();
                out.write(json);
                out.flush();
            } catch (SQLException ex) {
                Logger.getLogger(AndroidJsonServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(request.getParameter("action").equals("viewAppointment")){
            String appointmentID = request.getParameter("appointmentID");
            JsonObject appointment = new JsonObject();
            try {
                appointment = db.viewAppointmentJSON(appointmentID);
                Gson gson = new Gson();
                String appointmentInfo = gson.toJson(appointment);
                System.out.println(appointmentInfo);
                response.setContentType("text/plain");
                PrintWriter out = response.getWriter();
                out.print(appointmentInfo);
                out.flush();
            } catch (SQLException ex) {
                Logger.getLogger(AndroidServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        if(request.getParameter("action").equals("cancelAppointment")){
            String appointmentID = request.getParameter("appointmentID");
            Appointment appointment;
            
            try {
                db.cancelAppointment(appointmentID);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            
        }
        
        if(request.getParameter("action").equals("viewProfile")){
            String patientID = request.getParameter("patientID");
            try {
                Patient rs = db.viewProfile(patientID);
                Gson gson = new Gson();
                String patientInfo = gson.toJson(rs);
                System.out.println(patientInfo);
                response.setContentType("text/plain");
                PrintWriter out = response.getWriter();
                out.print(patientInfo);
                out.flush();
            } catch (SQLException ex) {
                Logger.getLogger(AndroidServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(request.getParameter("action").equals("updateProfile")){
            String patientID = request.getParameter("patientID");
            String phoneNumber = request.getParameter("phoneNumber");
            String address = request.getParameter("address");
            String  height = request.getParameter("height");
            
            try {
                db.updateProfile(patientID, phoneNumber, address, height);
            } catch (SQLException ex) {
                Logger.getLogger(AndroidServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(request.getParameter("action").equals("viewDoctor")){
            String doctorID = request.getParameter("doctorID");
            
            JsonObject drJsonObject = new JsonObject();
            try {
                drJsonObject = db.viewDrProfile(doctorID);
                Gson gson = new Gson();
                String drInfo = gson.toJson(drJsonObject);
                System.out.println(drInfo);
                response.setContentType("text/plain");
                PrintWriter out = response.getWriter();
                out.print(drInfo);
                out.flush();
            } catch (SQLException ex) {
                Logger.getLogger(AndroidServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
