package edu.pja.sri.kpodgorski.srisoapzadanie5s32073.repository;

import edu.pja.sri.kpodgorski.srisoapzadanie5s32073.model.Appointment;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends CrudRepository<Appointment, Long> {
    @Override
    List<Appointment> findAll();
    List<Appointment> findByAppointmentDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}

