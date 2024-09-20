package org.example;

import org.example.AdminOperations.Chart;
import org.example.Database.ConnectionSetup;
import org.example.Models.ChartForTrip;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ChartPreparationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("chartPrepareRequest.jsp");
        rd.forward(req, resp);
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int tripId = Integer.parseInt(req.getParameter("tripId"));
        PrintWriter out = resp.getWriter();
        if(tripId==0){
            req.setAttribute("error","Invalid trip id");
            RequestDispatcher rd = req.getRequestDispatcher("chartPrepareRequest.jsp");
            rd.forward(req, resp);
        }
        try {
            ConnectionSetup.setConnection();
            out.println(Helper.isTripExist(tripId));
            if(!Helper.isTripExist(tripId)){
                req.setAttribute("error","Trip does not exist");
                RequestDispatcher rd = req.getRequestDispatcher("chartPrepareRequest.jsp");
                rd.forward(req, resp);
            }
            else {
                Chart.prepareChart(tripId);
                ChartForTrip chartForTrip = Helper.chartForTrip(tripId);
                req.setAttribute("chartForTrip",chartForTrip);
                req.getRequestDispatcher("ChartViewer.jsp").forward(req, resp);
            }

        }
        catch(Exception e){
           // RequestDispatcher rd = req.getRequestDispatcher("chartPrepareRequest.jsp");
          //  rd.forward(req, resp);

            out.println(e.getMessage());
        }
    }
}
