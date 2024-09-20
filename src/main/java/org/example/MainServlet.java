package org.example;

import org.example.Database.ConnectionSetup;
import org.example.Database.DataSetup;
import org.example.UserOperations.Booking;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req,HttpServletResponse res)
            throws ServletException,IOException
    {
        PrintWriter out = res.getWriter();
      try
      {
          DataSetup.tripGeneration();
      }
      catch (Exception e)
      {
         out.println(e);
      }

    }
}
