package org.example.UserOperations;

import org.example.Database.ConnectionSetup;
import org.example.Helper;
import org.example.Models.TicketTemplate;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Cancellation {
    public static void cancel(int ticketid) throws SQLException, NamingException {
        System.out.println("-------------------------------------------------");
        ResultSet results = Helper.getTicketSeatsOnlyBooked(ticketid);
        results.next();
        System.out.println("Ticket id:" + results.getInt("ticketId"));
        System.out.println(Helper.getTripInformation(results.getInt("tripId")));
        results.beforeFirst();
        ArrayList<Integer> cancelList = new ArrayList<>();
        int row = 1;
        while (results.next()) {
            System.out.print(row + ".");
            cancelList.add(results.getInt("ticketSeatId"));
            if (results.getObject("tripSeatId") == null) {
                if (results.getObject("racNo") == null) {
                    System.out.println("WL no" + results.getInt("waitingListNo"));
                } else {
                    System.out.println("RAC no" + results.getInt("racNo"));
                }
            } else {
                int classType = results.getInt("class");
                int coachNo = results.getInt("coachNo");
                int seatNo = results.getInt("seatNo");
                System.out.println(Helper.coachType[classType - 1] + "     " + "Coach :" + coachNo + "      " + "Seat :" + seatNo);
            }
            row++;
        }
        System.out.println("Enter list of tickets you want to cancel: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] tickets = input.split(",");
        int[] ticketIds = new int[tickets.length];
        for (int i = 0; i < tickets.length; i++) {
            ticketIds[i] = Integer.parseInt(tickets[i]);
        }
        for (int i = 0; i < ticketIds.length; i++) {
            int ticketIndex = ticketIds[i];
            int ticketId = cancelList.get(ticketIndex-1);
            Helper.changeTheStateToCancelled(ticketId);
        }
        results.beforeFirst();
        results.next();
        double perTicketRefundAmount = getRefundAmount(results.getInt("tripId"), results.getInt("class"));
        double totalRefundAmount = perTicketRefundAmount * ticketIds.length;
        deduceRefundAmountFromTicket(results.getInt("ticketId"), totalRefundAmount);
        System.out.println("Total refund amount: " + totalRefundAmount);
    }

    public static double getRefundAmount(int tripId,int classType) throws SQLException, NamingException {
        Connection connection = ConnectionSetup.getConnection();
        Statement statement = connection.createStatement();
        String query = "SELECT firstAcPrice,secondAcPrice,thirdAcPrice,sleeperPrice FROM Trip WHERE tripId = " + tripId;
        ResultSet resultSet = statement.executeQuery(query);
        resultSet.next();

        if(classType==1){
            return resultSet.getDouble("firstAcPrice")*0.9;
        }
        else if(classType==2){
            return resultSet.getDouble("secondAcPrice")*0.9;
        }
        else if(classType==3){
            return resultSet.getDouble("thirdAcPrice")*0.9;
        }
        else {
            return resultSet.getDouble("sleeperPrice")*0.9;
        }

    }

    public static void deduceRefundAmountFromTicket(int ticketId,double refundamt) throws SQLException, NamingException {
        Connection connection = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("update ticket set price = price - ? where ticketId = ?");
        preparedStatement.setDouble(1, refundamt);
        preparedStatement.setInt(2, ticketId);
        preparedStatement.executeUpdate();
        connection.close();
    }

    public static ArrayList<TicketTemplate> cancelListWeb(int ticketId) throws SQLException, NamingException {
        Connection connection = ConnectionSetup.getConnection();
        ArrayList<TicketTemplate> cancelList = new ArrayList<>();
        ResultSet results = Helper.getTicketSeatsOnlyBooked(ticketId);
        while (results.next()) {
            cancelList.add(new TicketTemplate(ticketId,results.getInt("tripId"),results.getInt("class"),results.getInt("ticketSeatId"),results.getInt("coachNo"),results.getInt("seatNo"),results.getInt("racNo"),results.getInt("waitingListNo")));
        }

        return cancelList;
    }

    public static double cancelListOfTicketSeats(int[] ticketSeats,int ticketId,int tripId,int classType) throws SQLException, NamingException {
        Connection connection = ConnectionSetup.getConnection();
        for (int i = 0; i < ticketSeats.length; i++) {
            Helper.changeTheStateToCancelled(ticketSeats[i]);
        }
        double totalRefundAmount = ticketSeats.length * getRefundAmount(tripId, classType);
        deduceRefundAmountFromTicket(ticketId, totalRefundAmount);

        return totalRefundAmount;
    }


}
