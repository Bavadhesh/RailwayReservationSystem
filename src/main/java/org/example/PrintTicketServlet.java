package org.example;

import org.example.Database.ConnectionSetup;
import org.example.UserOperations.Booking;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PrintTicketServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int ticketId = Integer.parseInt(req.getParameter("ticketId"));
        try {
            ConnectionSetup.setConnection();
            String ticket = Booking.printTicketForServlet(ticketId);
            req.setAttribute("ticket", ticket);
            req.getRequestDispatcher("ticket.jsp").forward(req, resp);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("printRequest.jsp");
        dispatcher.forward(req, resp);
    }
}
