package org.example;

import org.example.AdminOperations.Reports;
import org.example.Database.ConnectionSetup;
import org.example.Models.RevenueByTrips;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RevenueByTripServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        try {
            ConnectionSetup.setConnection();
            ArrayList<RevenueByTrips> revenueByTrips = Reports.revenueByTripForServlet();
            req.setAttribute("revenueByTrips", revenueByTrips);
            req.getRequestDispatcher("RevenueByTrips.jsp").forward(req, resp);

        }
        catch (Exception e) {
            out.println(e);
        }
    }
}
