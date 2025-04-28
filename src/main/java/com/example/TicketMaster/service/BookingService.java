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

        String lockId = UUID.randomUUID().toString();
        System.out.println("lockID====="+lockId);
        boolean lock = redisLockService.acquireLock(ticketId, lockId, 40);
        System.out.println("Lock==========="+lock);

        if (!lock) {
            throw new RuntimeException("Ticket is currently being reserved by another user. Try again later.");
        }



        List<Ticket> ticketList = ticketRepository.findAll();
        for(Ticket ticketItem:ticketList) {
            if (ticketItem.getId().equals(ticketId)) {
                ticket = ticketItem;
                System.out.println("ticket details" + ticketItem.toString());

                if (!ticket.getStatus().equals(Status.AVAILABLE)) {
                    throw new RuntimeException("Ticket not available");
                }
                Booking booking = new Booking();
                booking.setUserID(userId);

                booking.setPrice(price);
                booking.setStatus(Status.LOCKED);

                List<Ticket> tickets = new ArrayList<>();
                tickets.add(ticket);

                booking.setTickets(tickets);

                booking = bookingRepository.save(booking);

                ticket.setBooking(booking);
                ticket.setStatus(Status.AVAILABLE);

                ticketRepository.save(ticket);

                return booking.getId().toString();

            }

        }
            return "booking failed";

        }
    }
