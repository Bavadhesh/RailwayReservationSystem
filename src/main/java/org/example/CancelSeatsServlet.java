package org.example;

import org.example.Models.TicketTemplate;
import org.example.UserOperations.Cancellation;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CancelSeatsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] selectedSeats;
        selectedSeats = req.getParameterValues("selectedSeats");
        PrintWriter out = resp.getWriter();
        int[] cancelledSeats = new int[selectedSeats.length];
        for (int i = 0; i < selectedSeats.length; i++) {
            cancelledSeats[i] = Integer.parseInt(selectedSeats[i]);
        }
        int ticketId = Integer.parseInt(req.getParameter("ticketId"));
        int tripId = Integer.parseInt(req.getParameter("tripId"));
        int classType = Integer.parseInt(req.getParameter("classType"));
        try {
           double refund = Cancellation.cancelListOfTicketSeats(cancelledSeats,ticketId,tripId,classType);
           req.setAttribute("ticketCount",selectedSeats.length);
           req.setAttribute("refund", refund);
           RequestDispatcher rd = req.getRequestDispatcher("ticket-cancel-seats.jsp");
           rd.forward(req, resp);
        }
        catch (Exception e) {
            out.println(e.getMessage());
        }



    }
}
