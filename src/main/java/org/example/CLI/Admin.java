package org.example.CLI;

import org.example.AdminOperations.Chart;
import org.example.AdminOperations.Reports;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Admin {
     public static void menu() throws SQLException, IOException,ClassNotFoundException, NamingException {
         Scanner scanner = new Scanner(System.in);
         System.out.println("\n*****************************************************************************");
         System.out.println("Welcome to the Admin Menu");
         System.out.println("1.prepare chart \n2.revenue by trip \n3.revenue by train \n4.Back to user menu");
         int choice = scanner.nextInt();
         if(choice == 1){
             System.out.println("Enter trip ID");
             int tripId = scanner.nextInt();
             Chart.prepareChart(tripId);
         }
         else if(choice == 2){
             Reports.revenueByTrip();
         }
         else if(choice == 3){
             Reports.revenueByTrain();
         }else if(choice == 4){
             User.menu();
         }

     }
}
