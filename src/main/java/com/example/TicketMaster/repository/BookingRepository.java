package com.example.TicketMaster.repository;

import com.example.TicketMaster.DAO.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookingRepository  extends JpaRepository<Booking, UUID> {


}
