package org.example.UserOperations;

import org.example.Database.ConnectionSetup;
import org.example.Helper;

import javax.naming.NamingException;
import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.sql.*;

public class Booking {
    public static int[] RACCount = {2,2,2,4};
    public static String bookticket(int tripId,int classType,int noOfPerson) throws SQLException, ClassNotFoundException, NamingException {
        String availableSeatQuery = "select tripSeatId,coachNo,seatNo from Coach natural join (select * from TripSeat natural join Seat where Seat.seatNo%8 != 0 and " +
                "TripSeat.tripId = "+tripId+" and available = 1) as TripSeatAvailability " +
                "where class = "+classType;
        Connection connection = ConnectionSetup.getConnection();
        Statement statement = connection.createStatement();
        String costQuery = "";
        if(classType == 1)
        {
            costQuery = "select firstAcPrice from Trip where tripId = "+tripId;
        }
        else if(classType == 2)
        {
             costQuery = "select secondAcPrice from Trip where tripId = "+tripId;
        }
        else if(classType == 3){
             costQuery = "select thirdAcPrice from Trip where tripId = "+tripId;
        }else{
             costQuery = "select sleeperPrice from Trip where tripId = "+tripId;
        }

        ResultSet price = statement.executeQuery(costQuery);
        price.next();
        double cost = price.getDouble(1) * noOfPerson;
        PreparedStatement preparedStatement = connection.prepareStatement(availableSeatQuery,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        ResultSet availableSeats = preparedStatement.executeQuery();

        String newTicket = "insert into Ticket(tripId,class,price) values("+tripId+","+classType+","+cost+")";
        preparedStatement = connection.prepareStatement(newTicket,Statement.RETURN_GENERATED_KEYS);
        int noOfRowsAffected = preparedStatement.executeUpdate();

        //getting the new generated ticket id

        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        resultSet.next();
        int id = resultSet.getInt(1);
        availableSeats.last();

        int noOfSeats = availableSeats.getRow();
        availableSeats.beforeFirst();

        //Ticket distribution decision making
        int noOfSeatsToBeGiven = noOfPerson;
        int noOfRaCTobeGiven = 0;
        int noOfWaitingListToBeGiven = 0;
        int noOfRacFilled = getNoOfRACs(tripId,classType);
        int noOfRacAvailable = RACCount[classType-1] - noOfRacFilled;

        if(noOfSeats < noOfPerson){
            noOfSeatsToBeGiven = noOfSeats;

            noOfPerson -= noOfSeats;

            if(noOfPerson <= noOfRacAvailable)
                noOfRaCTobeGiven = noOfPerson;
            else {
                noOfRaCTobeGiven = noOfRacAvailable;
                noOfWaitingListToBeGiven = noOfPerson - noOfRacAvailable;
            }
        }

        for (int i = 0; i < noOfSeatsToBeGiven; i++){
                  availableSeats.next();
                  String ticketSeat = "insert into TicketSeat(ticketId,tripSeatId,seatStatus) values( "+id+","+availableSeats.getInt("tripSeatId")+","+1+")";
                  String updateSeatAvailability = "update TripSeat set available = 2 where tripSeatId = " + availableSeats.getInt("tripSeatId") ;
                  preparedStatement = connection.prepareStatement(ticketSeat);
                  preparedStatement.executeUpdate();
                  preparedStatement = connection.prepareStatement(updateSeatAvailability);
                  preparedStatement.executeUpdate();
        }
        int racNo = noOfRacFilled;
        for(int i=0;i<noOfRaCTobeGiven;i++){
                   racNo++;
                   String ticketSeat = "insert into TicketSeat(ticketId,seatStatus,racNo) values("+id+","+1+","+racNo+")";
                   preparedStatement = connection.prepareStatement(ticketSeat);
                   preparedStatement.executeUpdate();
               }

        int wlno = getNoOfWL(tripId,classType);
        for(int i=0;i<noOfWaitingListToBeGiven;i++) {
            wlno++;
            String ticketSeat = "insert into TicketSeat(ticketId,seatStatus,waitingListNo) values(" + id + "," + 1 + "," + wlno + ")";
            preparedStatement = connection.prepareStatement(ticketSeat);
            preparedStatement.executeUpdate();
        }
        printTicket(id);

        return printTicketForServlet(id);
    }

    public static int getNoOfRACs(int tripId,int classType) throws SQLException, ClassNotFoundException, NamingException {

        Connection connection = ConnectionSetup.getConnection();
        String query = "select count(*) from Ticket natural join TicketSeat where tripId = "+tripId+" and class = "+classType+" and racNo!=NULL;";
        Statement statement = connection.createStatement();
        statement.executeQuery(query);
        ResultSet resultSet = statement.getResultSet();
        resultSet.next();
        int noOfRACs = resultSet.getInt(1);

        return noOfRACs;
    }

    public static int getNoOfWL(int tripId,int classType) throws SQLException, ClassNotFoundException, NamingException {
        Connection connection = ConnectionSetup.getConnection();
        String query = "select count(*) from Ticket natural join TicketSeat where tripId = \"+tripId+\" and class = \"+classType+\" and waitingListNo!=NULL;";
        Statement statement = connection.createStatement();
        statement.executeQuery(query);
        ResultSet resultSet = statement.getResultSet();
        resultSet.next();
        int noOfWL = resultSet.getInt(1);

        return noOfWL;
    }

    public static void printTicket(int ticketid) throws SQLException, NamingException {
        System.out.println("-------------------------------------------------");
        ResultSet results =  Helper.getTicketSeats(ticketid);
        results.next();
        System.out.println("Ticket id:" + results.getInt("ticketId"));
        System.out.println(Helper.getTripInformation(results.getInt("tripId")));
        results.beforeFirst();
        int row = 1;
        while (results.next()) {
            System.out.print(row+".");
            if(results.getObject("tripSeatId") == null){
                if(results.getObject("racNo") == null){
                    System.out.println("WL no"+results.getInt("waitingListNo")+" "+Helper.seatStatus[results.getInt("seatStatus")-1]);
                }
                else{
                    System.out.println("RAC no"+results.getInt("racNo")+" "+Helper.seatStatus[results.getInt("seatStatus")-1]);
                }
            }
            else {
                if(results.getObject("racNo") != null) System.out.println("RAC no"+results.getInt("racNo"));
                if (results.getObject("waitingListNo") != null) System.out.println("Waiting list no"+results.getInt("waitingListNo"));
                 int classType = results.getInt("class");
                 int coachNo = results.getInt("coachNo");
                 int seatNo = results.getInt("seatNo");
                 System.out.println(Helper.coachType[classType-1]+"     "+"Coach :"+coachNo+"      "+"Seat :"+seatNo+" "+Helper.seatStatus[results.getInt("seatStatus")-1]);
            }
            row++;
        }
    }

    public static String printTicketForServlet(int ticketid) throws SQLException, NamingException {
        String result = "";
        ResultSet results =  Helper.getTicketSeats(ticketid);
        results.next();
        result = result + "Ticket id:" + results.getInt("ticketId") + "<br>";
        result = result + Helper.getTripInformation(results.getInt("tripId"))+ "<br>";
        results.beforeFirst();
        int row = 1;
        while (results.next()) {
            result = result + row+"." ;
            if(results.getObject("tripSeatId") == null){
                if(results.getObject("racNo") == null){
                    result = result +"WL no"+results.getInt("waitingListNo")+" "+Helper.seatStatus[results.getInt("seatStatus")-1] + "<br>";
                }
                else{
                    result = result + "RAC no"+results.getInt("racNo")+" "+Helper.seatStatus[results.getInt("seatStatus")-1] + "<br>";
                }
            }
            else {
                if(results.getObject("racNo") != null) System.out.println("RAC no"+results.getInt("racNo"));
                if (results.getObject("waitingListNo") != null) System.out.println("Waiting list no"+results.getInt("waitingListNo"));
                int classType = results.getInt("class");
                int coachNo = results.getInt("coachNo");
                int seatNo = results.getInt("seatNo");
                result = result + Helper.coachType[classType-1]+"     "+"Coach :"+coachNo+"      "+"Seat :"+seatNo+" "+Helper.seatStatus[results.getInt("seatStatus")-1]+"<br>";
            }
            row++;
        }
        return result;
    }
}
