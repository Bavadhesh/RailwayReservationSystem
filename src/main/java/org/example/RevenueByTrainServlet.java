package org.example;

import org.example.AdminOperations.Reports;
import org.example.Database.ConnectionSetup;
import org.example.Models.RevenueByTrain;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RevenueByTrainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();

       try {
           ConnectionSetup.setConnection();
           ArrayList<RevenueByTrain> revenueByTrains = Reports.revenueByTrainForServlet();
           req.setAttribute("revenueByTrains", revenueByTrains);
           RequestDispatcher rd = req.getRequestDispatcher("RevenueByTrain.jsp");
           rd.forward(req, resp);
       }catch (Exception e){
           writer.println(e.getMessage());
       }
    }
}
