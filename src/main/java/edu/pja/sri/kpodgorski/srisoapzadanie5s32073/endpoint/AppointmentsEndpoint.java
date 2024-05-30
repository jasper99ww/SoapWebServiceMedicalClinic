package edu.pja.sri.kpodgorski.srisoapzadanie5s32073.endpoint;

import com.xyzclinic.services.appointments.*;
import edu.pja.sri.kpodgorski.srisoapzadanie5s32073.config.SoapWSConfig;
import edu.pja.sri.kpodgorski.srisoapzadanie5s32073.model.Appointment;
import edu.pja.sri.kpodgorski.srisoapzadanie5s32073.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Endpoint
@RequiredArgsConstructor
public class AppointmentsEndpoint {

    private final AppointmentRepository appointmentRepository;

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_APPOINTMENTS, localPart = "getAppointmentsRequest")
    @ResponsePayload
    public GetAppointmentsResponse getAppointments() {
        List<Appointment> allAppointments = appointmentRepository.findAll();
        List<AppointmentDto> appointmentDtos = allAppointments.stream().map(this::convertToDto).toList();
        GetAppointmentsResponse response = new GetAppointmentsResponse();
        response.getAppointments().addAll(appointmentDtos);
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_APPOINTMENTS, localPart = "getAppointmentRequest")
    @ResponsePayload
    public GetAppointmentResponse getAppointment(@RequestPayload GetAppointmentRequest request) {
        Long appointmentId = request.getAppointmentId();
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);

        GetAppointmentResponse response = new GetAppointmentResponse();
        response.setAppointment(appointment.map(this::convertToDto).orElse(null));
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_APPOINTMENTS, localPart = "getAppointmentsTodayRequest")
    @ResponsePayload
    public GetAppointmentsTodayResponse getAppointmentsToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.plusDays(1).atStartOfDay();

        List<Appointment> todayAppointments = appointmentRepository.findByAppointmentDateBetween(startOfToday, endOfToday);
        List<AppointmentDto> appointmentDtos = todayAppointments.stream().map(this::convertToDto).toList();
        GetAppointmentsTodayResponse response = new GetAppointmentsTodayResponse();
        response.getAppointments().addAll(appointmentDtos);
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_APPOINTMENTS, localPart = "addAppointmentRequest")
    @ResponsePayload
    public AddAppointmentResponse addAppointment(@RequestPayload AddAppointmentRequest request) {
        Appointment appointment = convertToEntity(request.getAppointment());
        appointment = appointmentRepository.save(appointment);
        AddAppointmentResponse response = new AddAppointmentResponse();
        response.setAppointmentId(appointment.getId());
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_APPOINTMENTS, localPart = "updateAppointmentRequest")
    @ResponsePayload
    public UpdateAppointmentResponse updateAppointment(@RequestPayload UpdateAppointmentRequest request) {
        AppointmentDto appointmentDto = request.getAppointment();
        Optional<Appointment> existingAppointment = appointmentRepository.findById(appointmentDto.getId());

        UpdateAppointmentResponse response = new UpdateAppointmentResponse();
        if (existingAppointment.isEmpty()) {
            throw new RuntimeException("Appointment not found with ID: " + appointmentDto.getId());
        }

        try {
            Appointment appointment = convertToEntity(appointmentDto);
            appointmentRepository.save(appointment);
            response.setSuccess(true);
        } catch (Exception e) {
            response.setSuccess(false);
        }

        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_APPOINTMENTS, localPart = "deleteAppointmentRequest")
    @ResponsePayload
    public DeleteAppointmentResponse deleteAppointment(@RequestPayload DeleteAppointmentRequest request) {
        Long appointmentId = request.getAppointmentId();
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);

        DeleteAppointmentResponse response = new DeleteAppointmentResponse();
        if (appointment.isPresent()) {
            appointmentRepository.deleteById(appointmentId);
            response.setSuccess(true);
        } else {
            response.setSuccess(false);
        }

        return response;
    }

    private AppointmentDto convertToDto(Appointment appointment) {
        if (appointment == null) return null;
        AppointmentDto dto = new AppointmentDto();
        try {
            dto.setId(appointment.getId());
            dto.setPatientId(appointment.getPatientId());
            dto.setAppointmentDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(appointment.getAppointmentDate())
            ));
            dto.setReason(appointment.getReason());
        } catch (DatatypeConfigurationException ex) {
            System.err.println("Error converting date: " + ex.getMessage());
            throw new RuntimeException("Error during date conversion", ex);
        }
        return dto;
    }


    private Appointment convertToEntity(AppointmentDto dto) {
        return Appointment.builder()
                .id(dto.getId())
                .patientId(dto.getPatientId())
                .appointmentDate(dto.getAppointmentDate().toGregorianCalendar().toZonedDateTime().toLocalDateTime())
                .reason(dto.getReason())
                .build();
    }
}
