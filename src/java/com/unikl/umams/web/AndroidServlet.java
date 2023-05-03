/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unikl.umams.web;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.unikl.umams.entities.*;
import static java.lang.System.out;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Amirrudin
 */
public class AndroidServlet extends HttpServlet {
    DBController db;
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
        processRequest(request, response);
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
        //processRequest(request, response);
        db = new DBController();
        System.out.println("IM HERE___________________________");
        
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
            System.out.println("Params received: " + nric + ", " + email+ ", " + bloodType.length());
            Patient inPatient = new Patient( nric, email, password, firstName, lastName, ethnicity, phoneNumber, address, height, bloodType);
        
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
