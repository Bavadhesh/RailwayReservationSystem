package org.example;

import org.example.UserOperations.Booking;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BookTicketServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int trip = Integer.parseInt(req.getParameter("trip"));
        int noOfPersons = Integer.parseInt(req.getParameter("noOfPerson"));
        int classType = Integer.parseInt(req.getParameter("class"));

        try {
            String ticket = Booking.bookticket(trip,classType,noOfPersons);
            req.setAttribute("ticket", ticket);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        RequestDispatcher rd = req.getRequestDispatcher("ticket.jsp");
        rd.forward(req, resp);
    }
}
