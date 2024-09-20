package org.example.CLI;

import org.example.Database.ConnectionSetup;
import org.example.Helper;
import org.example.UserOperations.Booking;
import org.example.UserOperations.Cancellation;

import javax.imageio.IIOException;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class User {
    public static void menu() throws SQLException,ClassNotFoundException, IOException,NamingException
    {
        System.out.println("RAILWAY RESERVATION SYSTEM");
        Scanner scanner = new Scanner(System.in);
        while(true)
        {
            System.out.println("\n\n========================================================================================");
            System.out.println("1.Booking \n2.Cancel \n3.Print Ticket\n 4.Admin");
            int choice = scanner.nextInt();
            if(choice == 1){
                book();
            }
            else if(choice == 2){
                cancel();
            }
            else if (choice == 3) {
                printTicket();
            }
            else {
                Admin.menu();
            }
        }
    }
    public static void cancel() throws SQLException, NamingException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the ticketId");
        int ticketId = scanner.nextInt();
        Cancellation.cancel(ticketId);
    }

    public static void printTicket() throws SQLException, NamingException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the ticketId");
        int ticketId = scanner.nextInt();
        Booking.printTicket(ticketId);
    }

    public static void book() throws SQLException, ClassNotFoundException,NamingException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please select (source): ");
        getStations();
        int src = scanner.nextInt();
        System.out.println("Please select (destination): ");
        getStations();
        int dest = scanner.nextInt();
        int tripId = getTripId(src, dest);
        System.out.println("Select Class type");
        for (int i=0;i<Helper.coachType.length;i++){
            System.out.println(i+1+"."+Helper.coachType[i]);
        }
        int classId = scanner.nextInt();
        System.out.println("No of passengers:");
        int no  = scanner.nextInt();
        Booking.bookticket(tripId,classId,no);
    }

    public static void getStations() throws SQLException, NamingException {
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement ps = con.prepareStatement("select * from station");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            System.out.print(rs.getString(1)+". ");
            System.out.println(rs.getString(2));
        }
        con.close();
    }

    public static ArrayList<String> listStations() throws SQLException, NamingException {
        Connection con = ConnectionSetup.getConnection();
        ArrayList<String> stations = new ArrayList<>();
        PreparedStatement ps = con.prepareStatement("select * from station");
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
           stations.add(rs.getString(2));
        }

        return stations;
    }

    public static int getTripId(int src,int dest) throws SQLException,NamingException {


        System.out.println("Enter year :");
        Scanner sc = new Scanner(System.in);
        int year = sc.nextInt();
        System.out.println("Enter month :");
        int month = sc.nextInt();
        System.out.println("Enter day :");
        int day = sc.nextInt();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day,0,0,0);
        long millis = calendar.getTimeInMillis();
        System.out.println(millis+src+dest);
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement ps = con.prepareStatement("select * from trip where srcStation = ? and destStation = ? and date div 100000000 = ?");
        ps.setInt(1,src);
        ps.setInt(2,dest);
        ps.setLong(3,millis/100000000);
        ResultSet rs = ps.executeQuery();
        rs.next();
        System.out.println(Helper.getTrainName(rs.getInt("trainId"))+"    "+Helper.convertMilliSecToTime(rs.getLong("departureTime")));
        return rs.getInt("tripId");
    }

    public static ResultSet getTrip(int src,int dest,Calendar calendar) throws SQLException, NamingException {

        long millis = calendar.getTimeInMillis();
        System.out.println(millis+src+dest);
        Connection con = ConnectionSetup.getConnection();
        PreparedStatement ps = con.prepareStatement("select * from trip where srcStation = ? and destStation = ? and date div 10000 = ?",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        ps.setInt(1,src);
        ps.setInt(2,dest);
        ps.setLong(3,millis/ 10000);
        ResultSet rs = ps.executeQuery();
        return rs;
    }
}
