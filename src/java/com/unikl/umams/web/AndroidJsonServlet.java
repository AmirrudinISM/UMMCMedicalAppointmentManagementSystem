/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unikl.umams.web;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unikl.umams.entities.Appointment;
import com.unikl.umams.entities.Patient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 *
 * @author Amirrudin
 */
public class AndroidJsonServlet extends HttpServlet {
    DBController db;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AndroidLogin</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AndroidLogin at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        db = new DBController();
        
         // Get the request body as a JSON string
        String action = request.getParameter("action");
        System.out.println("Servlet action: "+action);
    }

  
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        db = new DBController();
        
        // Get the request body as an InputStream
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        
        // Read the request body into a StringBuilder
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        
        // Parse the JSON object from the request body
        JsonObject jsonObject = JsonParser.parseString(requestBody.toString()).getAsJsonObject();
        
       
        String action = jsonObject.get("action").getAsString();
        System.out.println("Action from JSON: " + action);
        
        if(action.equals("login")){
            String email = jsonObject.get("email").getAsString();
            Patient loggedIn = db.getPatient(email);
            Gson gson = new Gson();
            String patientInfo = gson.toJson(loggedIn);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(patientInfo);
            out.flush();
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
