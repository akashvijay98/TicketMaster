package com.example.TicketMaster.controller;

import com.example.TicketMaster.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    BookingService bookingService;

    @PostMapping("/reserve")
    public String reserveTicket(@RequestParam UUID ticketId, @RequestParam UUID userId) {
        return bookingService.reserve(ticketId, userId);
    }

}
