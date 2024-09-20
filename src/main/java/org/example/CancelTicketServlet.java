package org.example;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;

import org.example.Database.ConnectionSetup;
import org.example.Models.TicketTemplate;
import org.example.UserOperations.Cancellation;

public class CancelTicketServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        RequestDispatcher rd = request.getRequestDispatcher("CancelTicket.jsp");
        rd.forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int ticketId = Integer.parseInt(req.getParameter("ticketId"));

        PrintWriter out = resp.getWriter();

        try {
            ConnectionSetup.setConnection();
            if(Helper.isAlreadyChartPrepared(ticketId)) {
                out.print("<h1>chart prepared cancellation not allowed</h1>");
                return;
            }

            ArrayList<TicketTemplate> cancelList = Cancellation.cancelListWeb(ticketId);
            req.setAttribute("cancelList", cancelList);
            RequestDispatcher rd = req.getRequestDispatcher("1.jsp");
            rd.forward(req, resp);
        }
        catch (Exception e)
        {
            out.println(e);
            e.printStackTrace();
        }
    }

}
