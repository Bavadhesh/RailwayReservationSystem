package org.example.Database;

import org.example.UserOperations.Booking;
import org.example.UserOperations.Cancellation;

import javax.naming.NamingException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DataSetup {

    public static void CoachSeatSetup() throws SQLException, NamingException {
        Connection connection = ConnectionSetup.getConnection();
        int row = 1;
        for(int i=1;i<=3;i++){
            PreparedStatement preparedStatement = connection.prepareStatement("insert into Coach(trainId,class,coachNo) values (?,?,?)");
            preparedStatement.setInt(1, i);
            preparedStatement.setInt(2, 1);
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();
            for(int j=1;j<=8;j++){
                PreparedStatement preparedStatement1 = connection.prepareStatement("insert into Seat(coachId,seatNo) values (?,?)");
                preparedStatement1.setInt(1, row);
                preparedStatement1.setInt(2, j);
                preparedStatement1.executeUpdate();
            }
            row++;
        }
        for(int i=1;i<=3;i++){
            PreparedStatement preparedStatement = connection.prepareStatement("insert into Coach(trainId,class,coachNo) values (?,?,?)");
            preparedStatement.setInt(1, i);
            preparedStatement.setInt(2, 2);
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();
            for(int j=1;j<=8;j++){
                PreparedStatement preparedStatement1 = connection.prepareStatement("insert into Seat(coachId,seatNo) values (?,?)");
                preparedStatement1.setInt(1, row);
                preparedStatement1.setInt(2, j);
                preparedStatement1.executeUpdate();
            }
            row++;
        }
        for(int i=1;i<=3;i++){
            PreparedStatement preparedStatement = connection.prepareStatement("insert into Coach(trainId,class,coachNo) values (?,?,?)");
            preparedStatement.setInt(1, i);
            preparedStatement.setInt(2, 3);
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();
            for(int j=1;j<=8;j++){
                PreparedStatement preparedStatement1 = connection.prepareStatement("insert into Seat(coachId,seatNo) values (?,?)");
                preparedStatement1.setInt(1, row);
                preparedStatement1.setInt(2, j);
                preparedStatement1.executeUpdate();
            }
            row++;
        }
        for(int i=1;i<=3;i++){
            for(int j=1;j<=2;j++){
                PreparedStatement preparedStatement = connection.prepareStatement("insert into Coach(trainId,class,coachNo) values (?,?,?)");
                preparedStatement.setInt(1, i);
                preparedStatement.setInt(2, 4);
                preparedStatement.setInt(3, j);
                preparedStatement.executeUpdate();
                for(int z=1;z<=8;z++){
                    PreparedStatement preparedStatement1 = connection.prepareStatement("insert into Seat(coachId,seatNo) values (?,?)");
                    preparedStatement1.setInt(1, row);
                    preparedStatement1.setInt(2, z);
                    preparedStatement1.executeUpdate();
                }
                row++;
            }
        }
        connection.close();
    }
    public static void tripGeneration() throws SQLException, NamingException {
        float sl = 100;
        float fac = 400;
        float sac = 300;
        float tac = 100;
        int src = 2;
        int dest = 3;

        Connection con = ConnectionSetup.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery("select seatId from Seat natural join (select * from Train natural join Coach where trainId = 1) as A");
        ArrayList<Integer> arr = new ArrayList<>();
        while(rs.next()){
            arr.add(rs.getInt("seatId"));
        }

        Calendar start = Calendar.getInstance();
        start.set(2024,8,18,0,0);
        Calendar end = Calendar.getInstance();
        end.set(2024,8,31,0,0);
        int hour = 22;
        int minute = 30;
        int second = 00;
        long milliseconds = hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000;
        int row = 1;
        for (Calendar date = (Calendar)start.clone();!date.after(end);date.add(Calendar.DATE, 1)){
            long num = date.getTimeInMillis();

            PreparedStatement statement = con.prepareStatement("insert into Trip(trainId," +
                    "sleeperPrice," +
                    "firstAcPrice," +
                    "secondAcPrice," +
                    "thirdAcPrice," +
                    "generalPrice," +
                    "srcStation," +
                    "destStation," +
                    "date," +
                    "chartPrepared," +
                    "departureTime) values(1,100,400,300,200,50,2,3,?,0,?)");

            statement.setLong(1, num);
            statement.setLong(2, milliseconds);
            statement.executeUpdate();

            for(int i=0;i<arr.size();i++){
                PreparedStatement statement1 = con.prepareStatement("insert into TripSeat(tripId,seatId,available) values (?,?,1)");
                statement1.setInt(1, row);
                statement1.setInt(2, arr.get(i));
                statement1.executeUpdate();
            }

            row++;
        }
        con.close();


    }
    public static void reset() throws SQLException, ClassNotFoundException, NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("delete from TicketSeat");
        preparedStatement.executeUpdate();
        preparedStatement = con.prepareStatement("delete from Ticket");
        preparedStatement.executeUpdate();
        preparedStatement = con.prepareStatement("delete from TripSeat");
        preparedStatement.executeUpdate();
        preparedStatement = con.prepareStatement("delete from Trip");
        preparedStatement.executeUpdate();
        preparedStatement = con.prepareStatement("alter table ticket auto_increment = 1");
        preparedStatement.executeUpdate();
        preparedStatement = con.prepareStatement("alter table ticketseat auto_increment = 1");
        preparedStatement.executeUpdate();
        preparedStatement = con.prepareStatement("alter table tripseat auto_increment = 1");
        preparedStatement.executeUpdate();
        preparedStatement = con.prepareStatement("alter table trip auto_increment = 1");
        preparedStatement.executeUpdate();
        tripGeneration();
//        Booking.bookticket(1,4,20);
//        Cancellation.cancel(1);
        con.close();
    }
}
