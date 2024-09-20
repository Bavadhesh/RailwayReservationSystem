package org.example.AdminOperations;

import org.example.Database.ConnectionSetup;
import org.example.Helper;
import org.example.Models.RevenueByTrain;
import org.example.Models.RevenueByTrips;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Reports {
    public static void revenueByTrip() throws SQLException, ClassNotFoundException, NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement ps = con.prepareStatement("select tripId,trainId,date,srcStation,destStation,Revenue from trip natural join (select tripId,sum(price) as Revenue from ticket group by tripId) as tripWiseRevenue;");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            System.out.println("Trip :"+rs.getInt("tripId")+" "+Helper.getTrainName(rs.getInt("trainId")) +" "+rs.getString("Revenue"));
        }
        con.close();
    }

    public static void revenueByTrain() throws SQLException, ClassNotFoundException, NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("select trainId,sum(price) as revenue from trip natural join ticket group by trainId;");
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            System.out.println("Train : "+Helper.getTrainName(rs.getInt("trainId"))+" "+rs.getString("Revenue"));
        }
        con.close();
    }

    public static ArrayList<RevenueByTrain> revenueByTrainForServlet() throws SQLException, ClassNotFoundException, NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("select trainId,sum(price) as revenue from trip natural join ticket group by trainId;");
        ResultSet rs = preparedStatement.executeQuery();
        ArrayList<RevenueByTrain> revenueByTrain = new ArrayList<>();
        while (rs.next()) {
            //System.out.println("Train : "+Helper.getTrainName(rs.getInt("trainId"))+" "+rs.getString("Revenue"));
            revenueByTrain.add(new RevenueByTrain(Helper.getTrainName(rs.getInt("trainId")),rs.getDouble("Revenue")));
        }

        return revenueByTrain;
    }

    public static ArrayList<RevenueByTrips> revenueByTripForServlet() throws SQLException, ClassNotFoundException, NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement ps = con.prepareStatement("select tripId,trainId,date,srcStation,destStation,Revenue from trip natural join (select tripId,sum(price) as Revenue from ticket group by tripId) as tripWiseRevenue;");
        ResultSet rs = ps.executeQuery();
        ArrayList<RevenueByTrips> revenueByTrip = new ArrayList<>();
        while (rs.next()) {
            String tripInfo = Helper.convertMilliSecToDate(rs.getLong("date"))+" "+Helper.getStationName(rs.getInt("srcStation"))+"-"+Helper.getStationName(rs.getInt("destStation"));
            revenueByTrip.add(new RevenueByTrips(tripInfo,rs.getDouble("Revenue")));
        }

        return revenueByTrip;
    }




}
