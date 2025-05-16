package com.example.TicketMaster.service;

import com.example.TicketMaster.DAO.Booking;
import com.example.TicketMaster.DAO.Status;
import com.example.TicketMaster.DAO.Ticket;
import com.example.TicketMaster.repository.BookingRepository;
import com.example.TicketMaster.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    double price = 10.00;
    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    RedisLockService redisLockService;

    @Transactional
    public String reserve(UUID ticketId, UUID userId){
        Ticket ticket;


        boolean lock = redisLockService.acquireLock(ticketId, userId, 40);
        System.out.println("Lock==========="+lock);

        if (!lock) {
            throw new RuntimeException("Ticket is currently being reserved by another user. Try again later.");
        }

        return "ticket locked";

        }


    @Transactional
    public String confirmBooking(UUID ticketId, UUID userId) {
        // Ensure user holds the lock
        if (!redisLockService.isLockedByUser(ticketId, userId)) {
            throw new RuntimeException("Unauthorized or expired reservation");
        }

        System.out.println("All tickets: ");

        List<Ticket> tickets = ticketRepository.findAll();
        for(Ticket item: tickets){
            System.out.println(item);
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!ticket.getStatus().equals(Status.AVAILABLE)) {
            throw new RuntimeException("Ticket already booked");
        }

        // Create booking
        Booking booking = new Booking();
        booking.setUserID(userId);
        booking.setPrice(price);
        booking.setStatus(Status.BOOKED);
        booking.setTickets(List.of(ticket));

        booking = bookingRepository.save(booking);

        // Update ticket to BOOKED and link to booking
        ticket.setStatus(Status.BOOKED);
        ticket.setBooking(booking);
        ticketRepository.save(ticket);

        // Optionally release lock early (otherwise TTL will clear it)
        redisLockService.releaseLock(ticketId, userId);

        return booking.getId().toString();
    }
}
