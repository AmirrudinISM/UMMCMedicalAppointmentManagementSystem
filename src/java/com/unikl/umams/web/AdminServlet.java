/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unikl.umams.web;

import com.unikl.umams.entities.Appointment;
import com.unikl.umams.utilities.IDGenerator;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Amirrudin
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/AdminServlet"})
public class AdminServlet extends HttpServlet {

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
        String appointmentID = request.getParameter("viewAppointment");
        HttpSession session = null;
        RequestDispatcher dispatcher;
        if(submitType != null){
            switch(submitType){
                case "adminLogin":
                    String adminName = request.getParameter("email");
                    String adminPassword = request.getParameter("password");


                    if(adminName.equals("admin@ummc.com") && adminPassword.equals("123")){
                        session = request.getSession();
                        session.setAttribute("email", "admin@ummc.com");
                        session.setAttribute("loggedIn", true);

                        dispatcher = request.getRequestDispatcher("admin_dashboard.jsp");
                        dispatcher.forward(request, response);
                    }
                    else{
                        request.setAttribute("error", "Invalid credentials");
                        dispatcher = request.getRequestDispatcher("admin_login.jsp");
                        dispatcher.forward(request, response);
                    }
                    break;
                
                case "adminLogout":
                    session = request.getSession();
                    session.invalidate(); 
                    dispatcher = request.getRequestDispatcher("admin_login.jsp"); // using RequestDispatcher method forward to login page.
                    dispatcher.forward(request, response);
                    break;
                
                case "registerDoctor":
                    
                    String name = request.getParameter("doctorName");
                    String eduBackground = request.getParameter("educationalBackground");
                    String phoneNumber= request.getParameter("phoneNumber");
                    String location = request.getParameter("location");
                    String email = request.getParameter("doctorEmail");
                    String password = request.getParameter("doctorPassword");
                    
                    db.registerDoctor(name, eduBackground, phoneNumber, location, email, password);
                    dispatcher = request.getRequestDispatcher("view_doctors.jsp");
                    dispatcher.forward(request, response);
                    break;
                
                
                case "confirm":
                    if(request.getParameter("selectedDoctor").equals("")){
                        request.setAttribute("error", "Please select a doctor to assign to!");
                        
                        
                        dispatcher = request.getRequestDispatcher("admin_dashboard.jsp");
                        dispatcher.forward(request, response);
                    }
                    else{
                        appointmentID = request.getParameter("appointmentID");
                        String doctorID = request.getParameter("selectedDoctor");

                        db.confirmAppointment(appointmentID, doctorID);
                        dispatcher = request.getRequestDispatcher("admin_dashboard.jsp");
                        dispatcher.forward(request, response);
                    }
                    
                    break;
                
                case "cancel":
                    db.cancelAppointment(request.getParameter("appointmentID"));
                    dispatcher = request.getRequestDispatcher("admin_dashboard.jsp");
                    dispatcher.forward(request, response);
                    
                    
                    
                default:
                    out.println("Error");
            }
        }
        
        if(appointmentID != null){
            
            request.setAttribute("appointmentID", appointmentID);
            Appointment appointment = db.viewAppointment(appointmentID);
            request.setAttribute("appointmentDate", appointment.getAppointmentDate());
            request.setAttribute("appointmentTime", appointment.getAppointmentTime());
            request.setAttribute("status", appointment.getAppointmentStatus());
            request.setAttribute("symptoms", appointment.getSymptoms());
            request.setAttribute("additionalDescription", appointment.getOtherDescription());
            dispatcher = request.getRequestDispatcher("view_appointment.jsp");
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
            Logger.getLogger(AdminServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(AdminServlet.class.getName()).log(Level.SEVERE, null, ex);
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
