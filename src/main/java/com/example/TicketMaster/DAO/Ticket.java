package com.example.TicketMaster.DAO;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "ticket")

@Getter
@Setter
public class Ticket {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id")
    private UUID id;


    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = true)
    private Booking booking;

    @Column(name = "seat_no")
    private String seatNo;

    @Enumerated(EnumType.STRING)
    private Status status;

    private double price;

}
