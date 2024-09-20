package org.example.AdminOperations;

import org.example.Database.ConnectionSetup;
import org.example.Helper;
import org.example.UserOperations.Booking;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Chart {
    public static void prepareChart(int tripId) throws SQLException, IOException, NamingException {
        if(isAlreadyChartPrepared(tripId)){
            System.out.println("Already Chart Prepared");
            return;
        }
        Helper.removeCancelledRac(tripId);
        Helper.removeCancelledWL(tripId);


        //Handle RAC status
        for (int classType = 1; classType <= 4; classType++) {
            Helper.racNoUpdation(tripId, classType);
            Helper.waitingListNoUpdation(tripId, classType);
            ArrayList<Integer> racList = Helper.getListOfRacInATrip(tripId,classType);
            ArrayList<Integer> freeSeats = Helper.getListOfCancelledTicketsInATrip(tripId,classType);

            for(int freeSeat : freeSeats){
                if(racList.isEmpty()) break;
                Helper.removeCancelledTicket(freeSeat);
                Helper.updateRacConfirmationStatus(racList.remove(0),freeSeat);
            }

            Helper.racNoUpdation(tripId,classType);
            ArrayList<Integer> waitingList = Helper.getListOfWaitingList(tripId,classType);
            freeSeats = Helper.getListOfCancelledTicketsInATrip(tripId,classType);
            for(int freeSeat : freeSeats){
                if(waitingList.isEmpty()) break;
                Helper.waitingListConfirmation(waitingList.remove(0),freeSeat);
                Helper.removeCancelledTicket(freeSeat);
            }

            waitingList = Helper.getListOfWaitingList(tripId,classType);
            racList = Helper.getListOfRacInATrip(tripId,classType);
            int racCount = racList.size();

            while (!waitingList.isEmpty() && racCount < Booking.RACCount[classType-1]) {
                System.out.println(waitingList.get(0)+" "+racCount);
                racCount++;
                Helper.waitingListRacUpdation(waitingList.remove(0),racCount);
            }
            Helper.waitingListNoUpdation(tripId,classType);
            Helper.allocateSeatForRac(tripId,classType);
            System.out.println("------------------------------"+Helper.coachType[classType-1]+"--------------------------------");
            Helper.printChart(tripId,classType);

        }
        setChartPrepared(tripId);
    }
    public static boolean isAlreadyChartPrepared(int tripId) throws SQLException, IOException, NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement ps = con.prepareStatement("select chartPrepared from Trip where tripId = ?");
        ps.setInt(1,tripId);
        ResultSet rs = ps.executeQuery();
        rs.next();

        if(rs.getInt(1)==0) return false;
        else return true;

    }

    public static void setChartPrepared(int tripId) throws SQLException, IOException, NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement ps = con.prepareStatement("update Trip set chartPrepared = 1 where tripId = ?");
        ps.setInt(1,tripId);
        ps.executeUpdate();
        con.close();
    }

}

