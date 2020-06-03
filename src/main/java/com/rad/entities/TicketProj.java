package com.rad.entities;

import org.springframework.data.rest.core.config.Projection;

@Projection(name="ticketProj", types=Ticket.class)
public interface TicketProj {
    public Long getid();
    public String getNomClient();
    public double getPrix();
    public int getcodePayment();
    public boolean getReserve();
    public Place getPlace();
}
