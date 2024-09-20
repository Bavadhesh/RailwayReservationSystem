package org.example.Models;

import java.util.ArrayList;

public class ChartForTrip {
    public  int tripId;
    public String trainName;
    public  String source;
    public String destination;
    public  String date;
    public  String departureTime;
    public  ArrayList<ChartRow> chartRows;

    public ChartForTrip(int tripId, String trainName, String source, String destination, String date, String departureTime, ArrayList<ChartRow> chartRows) {
        this.tripId = tripId;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.departureTime = departureTime;
        this.chartRows = chartRows;
    }
}
