package org.example;

import org.example.Database.ConnectionSetup;
import org.example.Models.ChartForTrip;
import org.example.Models.ChartRow;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Helper {
    public static String[] coachType = {"1AC","2AC","3AC","sleeper"};
    public static String[] seatStatus = {"Booked","Cancelled"};
    public static ResultSet getTicketRow(int id) throws SQLException, NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement ps = con.prepareStatement("select * from ticket where ticketId = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        return rs;
    }

    public static ResultSet getTicketSeats(int ticketid) throws SQLException,NamingException {
        Connection connection = ConnectionSetup.getConnection();

        ResultSet Ticket = Helper.getTicketRow(ticketid);
        Ticket.next();
        int tripId = Ticket.getInt("tripId");

        PreparedStatement preparedStatement = connection.prepareStatement("select * from (select * from Ticket natural join TicketSeat where ticketId = ?) as TicketInformation left join (select * from TripSeat natural join (select * from Coach natural join Seat) as CoachSeat where tripId = ?) as SeatInformation on TicketInformation.tripSeatId = SeatInformation.tripSeatId;",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        preparedStatement.setInt(1, ticketid);
        preparedStatement.setInt(2, tripId);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    public static String getTripInformation(int tripId) throws SQLException,NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("select * from Trip where tripId = ?");
        preparedStatement.setInt(1, tripId);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        String src = getStationName(resultSet.getInt("srcStation"));
        String dest = getStationName(resultSet.getInt("destStation"));

        String result = "Src : " + src  + " Dst : " + dest + "\n"  +
                getTrainName(resultSet.getInt("trainId")) + "\n" +
                "Date:"+convertMilliSecToDate(resultSet.getLong("date"))+"\n"+
                "Departure time :"+convertMilliSecToTime(resultSet.getLong("departureTime"));
        return result;
    }

    public static String getStationName(int stationId) throws SQLException,NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("select * from Station where stationId = ?");
        preparedStatement.setInt(1, stationId);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getString("stationName");
    }

    public static String getTrainName(int trainId) throws SQLException,NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("select * from Train where trainId = ?");
        preparedStatement.setInt(1, trainId);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getString("trainName");
    }

    public static String convertMilliSecToDate(long millisec){
        System.out.println(millisec);
       Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
       calendar.setTimeInMillis(millisec);
       SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
       return formatter.format(calendar.getTime());
    }

    public static String convertMilliSecToTime(long millisec){
        int[] time = new int[2];
        int sec = (int) (millisec / 1000);
        int min = sec / 60;
        int hour = min / 60;
        time[0] = hour;
        time[1] = min % 60;
        return time[0] + ":" + time[1];
    }

    public static void changeTheStateToCancelled(int ticketSeatId) throws SQLException,NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("update TicketSeat SET seatStatus = 2 where ticketSeatId = ?");
        preparedStatement.setInt(1, ticketSeatId);
        preparedStatement.executeUpdate();
        con.close();
    }

    public static ResultSet getTicketSeatsOnlyBooked(int ticketid) throws SQLException,NamingException {
        Connection connection = ConnectionSetup.getConnection();

        ResultSet Ticket = Helper.getTicketRow(ticketid);
        Ticket.next();
        int tripId = Ticket.getInt("tripId");

        PreparedStatement preparedStatement = connection.prepareStatement("select * from (select * from Ticket natural join TicketSeat where ticketId = ? and seatStatus=1) as TicketInformation left join (select * from TripSeat natural join (select * from Coach natural join Seat) as CoachSeat where tripId = ?) as SeatInformation on TicketInformation.tripSeatId = SeatInformation.tripSeatId;",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        preparedStatement.setInt(1, ticketid);
        preparedStatement.setInt(2, tripId);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    public static ArrayList<Integer> getListOfRacInATrip(int tripId,int classType) throws SQLException,NamingException {
        ArrayList<Integer> racList = new ArrayList<>();
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("select ticketSeatId from TicketSeat natural join Ticket where racNo is not null and tripId = ? and class=? order by racNo;",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        preparedStatement.setInt(1, tripId);
        preparedStatement.setInt(2, classType);
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            racList.add(resultSet.getInt("ticketSeatId"));
        }
        return racList;
    }

    public static ArrayList<Integer> getListOfCancelledTicketsInATrip(int tripId,int classType) throws SQLException,NamingException {
        ArrayList<Integer> cancelledList = new ArrayList<>();
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("select tripSeatId from TicketSeat inner join (select * from Ticket where tripId = ?) as Tickets on Tickets.ticketId = TicketSeat.ticketId where seatStatus = 2 and racNo is null and waitingListNo is null and class = ?;",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        preparedStatement.setInt(1, tripId);
        preparedStatement.setInt(2, classType);
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            cancelledList.add(resultSet.getInt("tripSeatId"));
        }
        return cancelledList;
    }

    public static void updateRacConfirmationStatus(int ticketSeatId,int tripSeatId) throws SQLException,NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement statement = con.prepareStatement("update TicketSeat SET tripSeatId = ?,racNo = NULL where ticketSeatId = ?");
        statement.setInt(1, tripSeatId);
        statement.setInt(2, ticketSeatId);
        statement.executeUpdate();
        con.close();
    }

    public static void removeCancelledTicket(int tripSeatId) throws SQLException,NamingException {
         Connection con = ConnectionSetup.getConnection();
         PreparedStatement preparedStatement = con.prepareStatement("delete from TicketSeat where tripSeatId = ?");
         preparedStatement.setInt(1, tripSeatId);
         preparedStatement.executeUpdate();
         con.close();
    }

    public static void racNoUpdation(int tripId,int classType) throws SQLException,NamingException {
        ArrayList<Integer> racList = getListOfRacInATrip(tripId,classType);
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement statement = con.prepareStatement("update TicketSeat SET racNo = ? where ticketSeatId = ?");
        for(int i=0;i<racList.size();i++){
            statement.setInt(2, racList.get(i));
            statement.setInt(1, i+1);
            statement.executeUpdate();
        }
        con.close();
    }

    public static ArrayList<Integer> getListOfWaitingList(int tripId,int classType) throws SQLException,NamingException {
        ArrayList<Integer> waitingList = new ArrayList<>();
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("select ticketSeatId from TicketSeat natural join Ticket where waitingListNo is not null and tripId = ? and class = ? order by waitingListNo;",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        preparedStatement.setInt(1, tripId);
        preparedStatement.setInt(2, classType);
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            waitingList.add(resultSet.getInt("ticketSeatId"));
        }
        return waitingList;
    }

    public static void waitingListConfirmation(int ticketSeatId ,int tripSeatId) throws SQLException,NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("update TicketSeat SET waitingListNo = NULL , tripSeatId = ? where ticketSeatId = ?");
        preparedStatement.setInt(1, tripSeatId);
        preparedStatement.setInt(2, ticketSeatId);
        preparedStatement.executeUpdate();
        con.close();
    }

    public static void waitingListRacUpdation(int ticketSeatId,int racNo) throws SQLException,NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("update TicketSeat set racNo = ?,waitingListNo = NULL where ticketSeatId = ?");
        preparedStatement.setInt(1, racNo);
        preparedStatement.setInt(2, ticketSeatId);
        preparedStatement.executeUpdate();
        con.close();
    }



    public static void waitingListNoUpdation(int tripId,int classType) throws SQLException,NamingException {
        Connection con = ConnectionSetup.getConnection();
        ArrayList<Integer> waitingList = getListOfWaitingList(tripId,classType);
        PreparedStatement preparedStatement = con.prepareStatement("update TicketSeat SET waitingListNo = ? where ticketSeatId = ?");
        for(int i=0;i<waitingList.size();i++){
            preparedStatement.setInt(2, waitingList.get(i));
            preparedStatement.setInt(1, i+1);
            preparedStatement.executeUpdate();
        }
        con.close();
    }

    public static void allocateSeatForRac(int tripId,int classType) throws SQLException,NamingException {
        ArrayList<Integer> racList = getListOfRacInATrip(tripId,classType);
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("update TicketSeat set tripSeatId = ? where ticketSeatId = ?");
        if(racList.size()>0){
            ArrayList<Integer> racBerthList = getRacBerthsInfo(tripId,classType);
            while (racBerthList.size()>0 && racList.size()>0){
                int berth = racBerthList.remove(0);
                preparedStatement.setInt(1, berth);
                preparedStatement.setInt(2, racList.remove(0));
                preparedStatement.executeUpdate();
                if(racList.size()>0){
                    preparedStatement.setInt(1, berth);
                    preparedStatement.setInt(2, racList.remove(0));
                    preparedStatement.executeUpdate();
                }
            }
        }
        con.close();
    }

    public static ArrayList<Integer> getRacBerthsInfo(int tripId,int classType) throws SQLException,NamingException {
        Connection con = ConnectionSetup.getConnection();
        ArrayList<Integer> berthsInfo = new ArrayList<>();
        PreparedStatement preparedStatement = con.prepareStatement("select * from TripSeat natural join (Seat natural join Coach) where tripId = ? and seatNo%8=0 and class = ?");
        preparedStatement.setInt(1, tripId);
        preparedStatement.setInt(2, classType);
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            berthsInfo.add(resultSet.getInt("tripSeatId"));
        }
        con.close();
        return berthsInfo;
    }

    public static void printChart(int tripId,int classType) throws SQLException,NamingException
    {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement statement = con.prepareStatement("select class,seatNo,coachNo from trip natural join tripseat natural join seat natural join coach where tripId = ? and class = ? and available = 2;");
        statement.setInt(1, tripId);
        statement.setInt(2, classType);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next())
        {
            System.out.println("COACH NO : " + resultSet.getString("coachNo") + " SEAT NO : " + resultSet.getString("seatNo"));
        }
        con.close();
    }

    public static void removeCancelledRac(int tripId) throws SQLException,NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement statement = con.prepareStatement("delete from TicketSeat where racNo is not null and seatStatus = 2;");
        statement.executeUpdate();
        con.close();
    }
    public static void removeCancelledWL(int tripId) throws SQLException,NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement statement = con.prepareStatement("delete from TicketSeat where waitingListNo is not null and seatStatus = 2;");
        statement.executeUpdate();
        con.close();
    }

    public static boolean isTripExist(int tripId) throws SQLException,NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("select * from Trip where tripId = ?");
        preparedStatement.setInt(1, tripId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){
            return true;
        }
        con.close();
        return false;
    }

    public static ChartForTrip chartForTrip(int tripId) throws SQLException,NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("select tripId,trainId,srcStation,destStation,date,departureTime,class,ticketId,coachNo,seatNo from TripSeat natural join Seat natural join Coach natural join (select * from ticket natural join ticketseat where tripId=?) as Passengers natural join Trip order by class;",ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        preparedStatement.setInt(1, tripId);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        ArrayList<ChartRow> list = new ArrayList<>();
        ChartForTrip chartForTrip = new ChartForTrip(resultSet.getInt(1),
                Helper.getTrainName(resultSet.getInt(2)),
                Helper.getStationName(resultSet.getInt(3)),
                Helper.getStationName(resultSet.getInt(4)),
                Helper.convertMilliSecToDate(resultSet.getLong(5)),
                Helper.convertMilliSecToTime(resultSet.getLong(6)),
                list
                );
        while(resultSet.next()){
               list.add(new ChartRow(coachType[resultSet.getInt(7)-1],resultSet.getInt(8),resultSet.getInt(9),resultSet.getInt(10)));
        }
        con.close();
        return chartForTrip;
    }

    public static boolean isAlreadyChartPrepared(int ticketId) throws SQLException,NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("select * from Ticket where ticketId = ?");
        preparedStatement.setInt(1, ticketId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){
            PreparedStatement preparedStatement1 = con.prepareStatement("select * from Trip where tripId = ?");
            preparedStatement1.setInt(1, resultSet.getInt(1));
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            if(resultSet1.next()){
                if(resultSet1.getInt("chartPrepared")==1) return true;
            }
        }
        con.close();
        return false;
    }
}

