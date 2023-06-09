/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unikl.umams.web;

import com.unikl.umams.entities.Appointment;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Amirrudin
 */
public class DoctorServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        DBController db = new DBController();
        
        String submitType = request.getParameter("submit");
        String viewAppointmentID = request.getParameter("viewAppointment");
        String consultAppointmentID = request.getParameter("consultAppointment");
        
        HttpSession session = null;
        RequestDispatcher dispatcher;
        if(submitType != null){
            switch(submitType){
                case "doctorLogin":
                    //db.updateMissedAppointments();
                    String doctorEmail = request.getParameter("email");
                    String doctorPassword = request.getParameter("password");
                    
                    if(db.verifiedDoctor(doctorEmail, doctorPassword)){
                        session = request.getSession();
                        session.setAttribute("doctorID", db.getDoctorID(doctorEmail));
                        session.setAttribute("email", doctorEmail);
                        session.setAttribute("loggedIn", true);

                        dispatcher = request.getRequestDispatcher("doctor_dashboard.jsp");
                        dispatcher.forward(request, response);
                        return;
                    }else{
                        request.setAttribute("error", "Invalid credentials");
                        dispatcher = request.getRequestDispatcher("doctor_login.jsp");
                        dispatcher.forward(request, response);
                        return;
                    }
                    
                
                case "doctorLogout":
                    session = request.getSession();
                    session.invalidate(); 
                    dispatcher = request.getRequestDispatcher("doctor_login.jsp");
                    dispatcher.forward(request, response);
                    break;
                
                case "complete":
                    consultAppointmentID = request.getParameter("appointmentID");
                    float weight = Float.valueOf(request.getParameter("weight"));
                    float systolicBP = Float.valueOf(request.getParameter("systolicBP"));
                    float diastolicBP = Float.valueOf(request.getParameter("diastolicBP"));
                    float temperature = Float.valueOf(request.getParameter("temperature"));
                    float oxygenLevel = Float.valueOf(request.getParameter("oxygenLevel"));
                    String diagnosis = request.getParameter("diagnosis");
                    String additionalNotes = request.getParameter("additionalNotes");
                    String prescription = request.getParameter("prescription");
                    
                    db.completeAppointment(consultAppointmentID, weight, temperature, oxygenLevel, diagnosis, additionalNotes, prescription, systolicBP, diastolicBP);
                    dispatcher = request.getRequestDispatcher("doctor_dashboard.jsp");
                    dispatcher.forward(request, response);
                    return;
                    
                case "cancel":
                    db.cancelAppointment(request.getParameter("appointmentID"));
                    dispatcher = request.getRequestDispatcher("doctor_dashboard.jsp");
                    dispatcher.forward(request, response);
                    
                    
                    
                default:
                    out.println("Error");
            }
        }
        
        if(consultAppointmentID != null){
            request.setAttribute("appointmentID", consultAppointmentID);
            Appointment appointment = db.viewAppointment(consultAppointmentID);
            request.setAttribute("appointmentDate", appointment.getAppointmentDate());
            request.setAttribute("appointmentTime", appointment.getAppointmentTime());
            request.setAttribute("status", appointment.getAppointmentStatus());
            request.setAttribute("symptoms", appointment.getSymptoms());
            request.setAttribute("additionalDescription", appointment.getOtherDescription());
            dispatcher = request.getRequestDispatcher("consult_appointment.jsp");
            dispatcher.forward(request, response);
        }
        
        if(viewAppointmentID != null){
            request.setAttribute("appointmentID", viewAppointmentID);
            Appointment appointment = db.viewAppointment(viewAppointmentID);
            request.setAttribute("appointmentDate", appointment.getAppointmentDate());
            request.setAttribute("appointmentTime", appointment.getAppointmentTime());
            request.setAttribute("status", appointment.getAppointmentStatus());
            request.setAttribute("symptoms", appointment.getSymptoms());
            request.setAttribute("additionalDescription", appointment.getOtherDescription());
            request.setAttribute("weight", appointment.getWeight());
            request.setAttribute("temperature", appointment.getTemperature());
            request.setAttribute("oxygenLevel", appointment.getOxygenLevel());
            request.setAttribute("diagnosis", appointment.getDiagnosis());
            request.setAttribute("additionalNotes", appointment.getAdditionalNotes());
            request.setAttribute("prescription", appointment.getPrescription());
            request.setAttribute("systolicBP", appointment.getSystolicBP());
            request.setAttribute("diastolicBP", appointment.getDiastolicBP());
            dispatcher = request.getRequestDispatcher("view_assigned_appointment.jsp");
            dispatcher.forward(request, response);
        }
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(DoctorServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(DoctorServlet.class.getName()).log(Level.SEVERE, null, ex);
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
