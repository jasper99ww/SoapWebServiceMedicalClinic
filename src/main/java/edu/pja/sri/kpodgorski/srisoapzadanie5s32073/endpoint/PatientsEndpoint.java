package edu.pja.sri.kpodgorski.srisoapzadanie5s32073.endpoint;

import com.xyzclinic.services.patients.*;
import edu.pja.sri.kpodgorski.srisoapzadanie5s32073.config.SoapWSConfig;
import edu.pja.sri.kpodgorski.srisoapzadanie5s32073.model.Patient;
import edu.pja.sri.kpodgorski.srisoapzadanie5s32073.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.List;
import java.util.Optional;

@Endpoint
@RequiredArgsConstructor
public class PatientsEndpoint {

    private final PatientRepository patientRepository;

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_PATIENTS, localPart = "getPatientsRequest")
    @ResponsePayload
    public GetPatientsResponse getPatients() {
        List<Patient> allPatients = patientRepository.findAll();
        List<PatientDto> patientDtos = allPatients.stream().map(this::convertToDto).toList();
        GetPatientsResponse response = new GetPatientsResponse();
        response.getPatients().addAll(patientDtos);
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_PATIENTS, localPart = "getPatientByIdRequest")
    @ResponsePayload
    public GetPatientByIdResponse getPatientById(@RequestPayload GetPatientByIdRequest request) {
        Long id = request.getPatientId();
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isEmpty()) {
            throw new RuntimeException("Patient not found with ID: " + id);
        }
        GetPatientByIdResponse response = new GetPatientByIdResponse();
        response.setPatient(patient.map(this::convertToDto).orElse(null));
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_PATIENTS, localPart = "addPatientRequest")
    @ResponsePayload
    public AddPatientResponse addPatient(@RequestPayload AddPatientRequest request) {
        Patient patient = convertToEntity(request.getPatient());
        patient = patientRepository.save(patient);
        AddPatientResponse response = new AddPatientResponse();
        response.setPatientId(patient.getId());
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_PATIENTS, localPart = "updatePatientRequest")
    @ResponsePayload
    public UpdatePatientResponse updatePatient(@RequestPayload UpdatePatientRequest request) {
        Optional<Patient> existingPatient = patientRepository.findById(request.getPatient().getId());
        if (existingPatient.isEmpty()) {
            throw new RuntimeException("Patient not found with ID: " + request.getPatient().getId());
        }
        Patient patient = convertToEntity(request.getPatient());
        patientRepository.save(patient);
        UpdatePatientResponse response = new UpdatePatientResponse();
        response.setSuccess(true);
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_PATIENTS, localPart = "deletePatientRequest")
    @ResponsePayload
    public DeletePatientResponse deletePatient(@RequestPayload DeletePatientRequest request) {
        Long patientId = request.getPatientId();
        if (!patientRepository.existsById(patientId)) {
            throw new RuntimeException("Patient not found with ID: " + patientId);
        }
        patientRepository.deleteById(patientId);
        DeletePatientResponse response = new DeletePatientResponse();
        response.setSuccess(true);
        return response;
    }

    private PatientDto convertToDto(Patient patient) {
        if (patient == null) return null;
        try {
            PatientDto dto = new PatientDto();
            dto.setId(patient.getId());
            dto.setFirstName(patient.getFirstName());
            dto.setLastName(patient.getLastName());
            dto.setDateOfBirth(DatatypeFactory.newInstance().newXMLGregorianCalendar(patient.getDateOfBirth().toString()));
            return dto;
        } catch (DatatypeConfigurationException ex) {
            throw new RuntimeException("Error during date conversion", ex);
        }
    }

    private Patient convertToEntity(PatientDto dto) {
        return Patient.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .dateOfBirth(dto.getDateOfBirth().toGregorianCalendar().toZonedDateTime().toLocalDate())
                .build();
    }

}

