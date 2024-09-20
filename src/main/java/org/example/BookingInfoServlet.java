package org.example;

import org.example.CLI.User;
import org.example.Database.ConnectionSetup;

import javax.jws.soap.SOAPBinding;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class BookingInfoServlet  extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        try {
            ConnectionSetup.setConnection();
            ArrayList<String> stations = User.listStations();
            req.setAttribute("stations",stations);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        RequestDispatcher view = req.getRequestDispatcher("bookingWindow.jsp");
        view.forward(req, resp);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        String src = req.getParameter("source");
        String dest = req.getParameter("dest");
        String date = req.getParameter("date");
        Calendar calendar = Calendar.getInstance();
        String[] dates = date.split("-");
        int year = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]);
        int day = Integer.parseInt(dates[2]);
        calendar.set(year, month, day,0,0,45);







        int source = Integer.parseInt(src);
        int destination = Integer.parseInt(dest);
        PrintWriter writer = resp.getWriter();
        ArrayList<String[]> result = new ArrayList<>();
        ResultSet trips = null;
        writer.println(calendar.getTimeInMillis());


        try {
          trips = User.getTrip(source,destination,calendar);

          while (trips.next()){
                String[] s = new String[2];
                writer.println(trips.getInt("trainId")+" "+trips.getLong("date"));
                s[0] = Helper.getTrainName(trips.getInt("trainId"))+"   "+Helper.convertMilliSecToTime(trips.getLong("departureTime"));
                s[1] = String.valueOf(trips.getInt("tripId"));
                writer.println(trips.getInt("tripId"));
                result.add(s);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        req.setAttribute("trips",result);
//        RequestDispatcher view = req.getRequestDispatcher("bookingWindow.jsp");
//        view.forward(req, resp);
    }
}
