package org.example.Models;

public class TicketTemplate {
    public int ticketId;
    public int tripId;
    public int classType;
    public int ticketSeatId;
    public int coachNo;
    public int seatNo;
    public int racNo;
    public int wlNo;

    public TicketTemplate(int ticketId,int tripId,int classType,int ticketSeatId, int coachNo, int seatNo, int racNo, int wlNo) {
        this.ticketId = ticketId;
        this.tripId = tripId;
        this.classType = classType;
        this.ticketSeatId = ticketSeatId;
        this.coachNo = coachNo;
        this.seatNo = seatNo;
        this.racNo = racNo;
        this.wlNo = wlNo;
    }
}
