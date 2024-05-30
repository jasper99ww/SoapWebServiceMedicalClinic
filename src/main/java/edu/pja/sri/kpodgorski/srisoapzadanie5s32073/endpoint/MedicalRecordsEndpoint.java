package edu.pja.sri.kpodgorski.srisoapzadanie5s32073.endpoint;

import com.xyzclinic.services.medicalrecords.*;
import edu.pja.sri.kpodgorski.srisoapzadanie5s32073.config.SoapWSConfig;
import edu.pja.sri.kpodgorski.srisoapzadanie5s32073.model.MedicalRecord;
import edu.pja.sri.kpodgorski.srisoapzadanie5s32073.repository.MedicalRecordRepository;
import edu.pja.sri.kpodgorski.srisoapzadanie5s32073.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Endpoint
@RequiredArgsConstructor
public class MedicalRecordsEndpoint {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_MEDICAL_RECORDS, localPart = "getMedicalRecordsRequest")
    @ResponsePayload
    public GetMedicalRecordsResponse getMedicalRecords() {
        List<MedicalRecord> allMedicalRecords = medicalRecordRepository.findAll();
        List<MedicalRecordDto> medicalRecordDtos = allMedicalRecords.stream().map(this::convertToDto).toList();
        GetMedicalRecordsResponse response = new GetMedicalRecordsResponse();
        response.getMedicalRecords().addAll(medicalRecordDtos);
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_MEDICAL_RECORDS, localPart = "getMedicalRecordRequest")
    @ResponsePayload
    public GetMedicalRecordResponse getMedicalRecord(@RequestPayload GetMedicalRecordRequest request) {
        Optional<MedicalRecord> medicalRecord = medicalRecordRepository.findById(request.getMedicalRecordId());
        if (medicalRecord.isEmpty()) {
            throw new RuntimeException("Medical record not found with ID: " + request.getMedicalRecordId());
        }
        GetMedicalRecordResponse response = new GetMedicalRecordResponse();
        response.setMedicalRecord(medicalRecord.map(this::convertToDto).orElse(null));
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_MEDICAL_RECORDS, localPart = "addMedicalRecordRequest")
    @ResponsePayload
    public AddMedicalRecordResponse addMedicalRecord(@RequestPayload AddMedicalRecordRequest request) {
        if (!patientRepository.existsById(request.getMedicalRecord().getPatientId())) {
            throw new RuntimeException("Patient not found with ID: " + request.getMedicalRecord().getPatientId());
        }
        MedicalRecord medicalRecord = convertToEntity(request.getMedicalRecord());
        medicalRecord = medicalRecordRepository.save(medicalRecord);
        AddMedicalRecordResponse response = new AddMedicalRecordResponse();
        response.setMedicalRecordId(medicalRecord.getId());
        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_MEDICAL_RECORDS, localPart = "updateMedicalRecordRequest")
    @ResponsePayload
    public UpdateMedicalRecordResponse updateMedicalRecord(@RequestPayload UpdateMedicalRecordRequest request) {
        MedicalRecordDto medicalRecordDto = request.getMedicalRecord();
        Optional<MedicalRecord> existingMedicalRecord = medicalRecordRepository.findById(medicalRecordDto.getId());

        UpdateMedicalRecordResponse response = new UpdateMedicalRecordResponse();
        if (existingMedicalRecord.isEmpty()) {
            throw new RuntimeException("Medical record not found with ID: " + medicalRecordDto.getId());
        }

        try {
            MedicalRecord medicalRecord = convertToEntity(medicalRecordDto);
            medicalRecordRepository.save(medicalRecord);
            response.setSuccess(true);
        } catch (Exception e) {
            response.setSuccess(false);
        }

        return response;
    }

    @PayloadRoot(namespace = SoapWSConfig.NAMESPACE_MEDICAL_RECORDS, localPart = "deleteMedicalRecordRequest")
    @ResponsePayload
    public DeleteMedicalRecordResponse deleteMedicalRecord(@RequestPayload DeleteMedicalRecordRequest request) {
        Long medicalRecordId = request.getMedicalRecordId();
        Optional<MedicalRecord> medicalRecord = medicalRecordRepository.findById(medicalRecordId);

        DeleteMedicalRecordResponse response = new DeleteMedicalRecordResponse();

        if (medicalRecord.isEmpty()) {
            throw new RuntimeException("Medical record not found with ID: " + medicalRecordId);
        }

        try {
            medicalRecordRepository.deleteById(medicalRecordId);
            response.setSuccess(true);
        } catch (Exception e) {
            response.setSuccess(false);
        }

        return response;
    }

    private MedicalRecordDto convertToDto(MedicalRecord medicalRecord) {
        if (medicalRecord == null) return null;
        MedicalRecordDto dto = new MedicalRecordDto();
        try {
            dto.setId(medicalRecord.getId());
            dto.setPatientId(medicalRecord.getPatientId());
            dto.setDescription(medicalRecord.getDescription());
            dto.setCreatedAt(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(medicalRecord.getCreatedAt())
            ));
        } catch (DatatypeConfigurationException ex) {
            throw new RuntimeException("Error during date conversion", ex);
        }
        return dto;
    }

    private MedicalRecord convertToEntity(MedicalRecordDto dto) {

        return MedicalRecord.builder()
                .id(dto.getId())
                .patientId(dto.getPatientId())
                .description(dto.getDescription())
                .createdAt(dto.getCreatedAt().toGregorianCalendar().toZonedDateTime().toLocalDateTime())
                .build();
    }
}